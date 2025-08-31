package service;

import exceptions.InvalidCustomerException;
import exceptions.UnauthorizedAccessException;
import model.Customer;
import model.Subscription;
import model.Invoice;
import model.UsageRecord;
import model.Plan;
import repo.CustomerRepo;
import repo.SubscriptionRepo;
import repo.InvoiceRepo;
import repo.UsageRecordRepo;
import repo.PlanRepo;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class AnalyticsServiceImpl implements AnalyticsService {
    private final CustomerRepo customerRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final UsageRecordRepo usageRecordRepo;
    private final InvoiceRepo invoiceRepo;
    private final PlanRepo planRepo;

    public AnalyticsServiceImpl(CustomerRepo customerRepo, SubscriptionRepo subscriptionRepo, UsageRecordRepo usageRecordRepo, InvoiceRepo invoiceRepo, PlanRepo planRepo) {
        this.customerRepo = customerRepo;
        this.subscriptionRepo = subscriptionRepo;
        this.usageRecordRepo = usageRecordRepo;
        this.invoiceRepo = invoiceRepo;
        this.planRepo = planRepo;
    }

    @Override
    public UsageRecord getCustomerUsage(UUID customerId) throws InvalidCustomerException {
        if (customerId == null) {
            throw new InvalidCustomerException("Customer ID cannot be null");
        }
        Customer customer = customerRepo.findById(customerId);
        if (customer == null) {
            throw new InvalidCustomerException("Customer not found: " + customerId);
        }
        List<Subscription> subscriptions = subscriptionRepo.findByCustomerId(customerId);
        List<UUID> subscriptionIds = subscriptions.stream().map(Subscription::getId).toList();
        List<UsageRecord> records = usageRecordRepo.findAll().stream()
                .filter(ur -> subscriptionIds.contains(ur.getSubscriptionId()))
                .collect(Collectors.toList());
        double totalDataGb = records.stream().mapToDouble(UsageRecord::getDataGb).sum();
        int totalVoiceMinutes = records.stream().mapToInt(UsageRecord::getVoiceMinutes).sum();
        int totalSmsCount = records.stream().mapToInt(UsageRecord::getSmsCount).sum();
        boolean anyRoaming = records.stream().anyMatch(UsageRecord::isRoaming);
        boolean anyInternational = records.stream().anyMatch(UsageRecord::isInternational);
        boolean anyNightTime = records.stream().anyMatch(UsageRecord::isNightTime);
        return new UsageRecord(null, null, totalDataGb, totalVoiceMinutes, totalSmsCount,
                anyRoaming, anyInternational, anyNightTime, LocalDateTime.now());
    }

    @Override
    public long getActiveSubscriptionsCount(String role) throws UnauthorizedAccessException {
        if (!"admin".equals(role)) {
            throw new UnauthorizedAccessException("Only admin can access subscription analytics");
        }
        return subscriptionRepo.findAll().stream()
                .filter(sub -> sub.getEndDate() == null || sub.getEndDate().isAfter(java.time.LocalDate.now()))
                .count();
    }

    @Override
    public double getTotalRevenue(String role) throws UnauthorizedAccessException {
        if (!"admin".equals(role)) {
            throw new UnauthorizedAccessException("Only admin can access revenue analytics");
        }
        return invoiceRepo.findAll().stream()
                .mapToDouble(Invoice::getTotal)
                .sum();
    }

    @Override
    public List<Map.Entry<UUID, Double>> topNDataUsers(String role, List<Invoice> invoices,List<UsageRecord> usageRecord,int n) {
        List<Map.Entry<UUID, Double>> topDataUsers = new ArrayList<>();
        if(role.toLowerCase().equals("admin")) {
            Map<UUID, Double> usageMap = usageRecord.stream().collect(Collectors.toMap(UsageRecord::getSubscriptionId, UsageRecord::getDataGb, Double::sum));
            topDataUsers = usageMap.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(n)
                    .collect(Collectors.toList());
        }
        return topDataUsers;
    }

    @Override
    public Map<String, Double> arpuByPlan(String role, List<Invoice> invoices, List<Plan> plans, List<Subscription> subscriptions) {
        if (!"admin".equalsIgnoreCase(role)) {
            return new HashMap<>();
        }
        Map<UUID, String> planIdToNameMap = plans.stream()
                .collect(Collectors.toMap(Plan::getId, Plan::getName));

        Map<UUID, UUID> subscriptionToPlanMap = subscriptions.stream()
                .collect(Collectors.toMap(Subscription::getId, Subscription::getPlanId));
        Map<UUID, Double> totalRevenueByPlan = invoices.stream()
                .filter(invoice -> subscriptionToPlanMap.containsKey(invoice.getSubscriptionId()))
                .collect(
                        Collectors.groupingBy(
                                invoice -> subscriptionToPlanMap.get(invoice.getSubscriptionId()),
                                Collectors.summingDouble(Invoice::getTotal)
                        )
                );

        Map<UUID, Long> subscribersPerPlan = subscriptions.stream()
                .collect(Collectors.groupingBy(Subscription::getPlanId, Collectors.counting()));

        return totalRevenueByPlan.entrySet().stream()
                .collect(
                        Collectors.toMap(
                                entry -> planIdToNameMap.getOrDefault(entry.getKey(), "Unknown Plan"),
                                entry -> {
                                    UUID planId = entry.getKey();
                                    double totalRevenue = entry.getValue();
                                    Long subscriberCount = subscribersPerPlan.getOrDefault(planId, 0L);
                                    return subscriberCount > 0 ? totalRevenue / subscriberCount : 0.0;
                                }
                        )
                );
    }

    @Override
    public Map<UUID, DoubleSummaryStatistics> getOverageStatsByPlan(String role,List<Invoice> invoices, List<Subscription> subscriptions){
        if (!"admin".equalsIgnoreCase(role)) {
            return new HashMap<>();
        }
        //Mapping subscription to plan
        Map<UUID,UUID> subscriptionToPlanMap=subscriptions.stream()
                .collect(Collectors.toMap(Subscription::getId, Subscription::getPlanId));
        //Grouping overage charges by plan
        Map<UUID,DoubleSummaryStatistics> overageStatsPerPlan=invoices.stream()
                .filter(invoice->subscriptionToPlanMap.containsKey(invoice.getSubscriptionId()))
                .collect(Collectors.groupingBy(
                        invoice->subscriptionToPlanMap.get(invoice.getSubscriptionId()),
                        Collectors.summarizingDouble(Invoice::getOverageCharge)
                ));
        return overageStatsPerPlan;
    }

    @Override
    public List<String> detectCreditRisk(String role,List<Invoice> invoices,List<Subscription> subscriptions,List<Customer> customers){
        if(!"admin".equalsIgnoreCase(role)){
            return new ArrayList<>();
        }
        //Mapping subscription id to customer id
        Map<UUID,UUID> subscriptionToCustomerMap=subscriptions.stream()
                .collect(Collectors.toMap(Subscription::getId, Subscription::getCustomerId));
        //Mapping customer id to name
        Map<UUID,String> customerIdToNameMap=customers.stream()
                .collect(Collectors.toMap(Customer::getId, Customer::getName));
        //Finding customers with unpaid invoices >60 days
        List<String> creditRiskCustomers = invoices.stream()
                .filter(invoice -> !invoice.isPaid() && invoice.getBillingMonth().plusMonths(2).isBefore(YearMonth.now()))
                .map(Invoice::getSubscriptionId)
                .filter(subscriptionToCustomerMap::containsKey)
                .map(subscriptionToCustomerMap::get)
                .distinct()
                .filter(customerIdToNameMap::containsKey)
                .map(customerIdToNameMap::get)
                .collect(Collectors.toList());
        return creditRiskCustomers;
    }

    @Override
    public Plan recommendPlan(double avgDataGb, double avgVoiceMinutes, double avgSmsCount) {
        return planRepo.findAll().stream()
                .filter(p -> p.getDataAllowanceGb() >= avgDataGb * 0.8 &&
                        p.getVoiceAllowanceMinutes() >= avgVoiceMinutes * 0.8 &&
                        p.getSmsAllowance() >= avgSmsCount * 0.8)
                .map(p -> {
                    double overageData = Math.max(0, avgDataGb - p.getDataAllowanceGb()) * p.getOverageRateData();
                    double overageVoice = Math.max(0, avgVoiceMinutes - p.getVoiceAllowanceMinutes()) * p.getOverageRateVoice();
                    double overageSms = Math.max(0, avgSmsCount - p.getSmsAllowance()) * p.getOverageRateSms();
                    double totalCost = p.getMonthlyRental() + overageData + overageVoice + overageSms;
                    return new PlanCost(p, totalCost);
                })
                .min(Comparator.comparingDouble(pc -> pc.cost))
                .map(pc -> pc.plan)
                .orElse(null);
    }

    private static class PlanCost {
        final Plan plan;
        final double cost;

        PlanCost(Plan plan, double cost) {
            this.plan = plan;
            this.cost = cost;
        }
    }
}