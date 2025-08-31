package service;

import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import model.Customer
import model.Subscription
import model.Plan
import model.Invoice
import model.UsageRecord

public class AnalyticsServiceImpl {
  @Override
    public List<Map.Entry<UUID, Double>> topNDataUsers(String role, List<Invoice> invoices,List<UsageRecord> usageRecord,int n) {
        List<Map.Entry<UUID, Double>> topDataUsers;
        if(role.toLowerCase().equals("admin")) {
            Map<UUID, Double> usageMap = usageRecord.stream().collect(Collectors.toMap(usageRecord::getSubscriptionId, usageRecord::getDataGb));
            topDataUsers = invoices.stream()
                    .filter(br -> usageMap.containsKey(br.getSubscriptionId()))
                    .map(br -> Map.entry(br.getSubscriptionId(), br.getDataGb()))
                    .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                    .limit(n)
                    .collect(Collectors.toList());
        }
        return topDataUsers;
    }

    @Override
    public Map<String, Double> arpuByPlan(String role, List<Invoice> invoices, List<Plan> plans, List<Subscription> subscriptions) {
        if (!"admin".equalsIgnoreCase(role)) {
            return Map.of();
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
                                entry -> planIdToNameMap.get(entry.getKey()), // Convert plan UUID to plan name
                                entry -> {
                                    UUID planId = entry.getKey();
                                    double totalRevenue = entry.getValue();
                                    Long subscriberCount = subscribersPerPlan.getOrDefault(planId, 0L);
                                    return subscriberCount > 0 ? totalRevenue / subscriberCount : 0.0;
                                }
                        )
                );
    }
    public Map<UUID, DoubleSummaryStatistics> getOverageStatsByPlan(String role,List<Invoice> invoices, List<Subscription> subscriptions){
        if (!"admin".equalsIgnoreCase(role)) {
            return Map.of();
        }
        //Lookup map for Plan ID and Subscriptions ID
        Map<UUID,UUID> subscriptionToPlanMap=subscriptions.stream()
                .collect(Collectors.toMap(
                        Subscription::getId,
                        Subscription::getPlanId
                ));
        Map<UUID,DoubleSummaryStatistics> overageStatsPerPlan=invoices.stream()
                .filter(invoice->subscriptionToPlanMap.containsKey(invoice.getSubscriptionId()))
                .collect(Collectors.groupingBy(
                        invoice->subscriptionToPlanMap.get(invoice.getSubscriptionId()),
                        Collectors.summarizingDouble(Invoice::getOverageCharge)
                ));
        //While printing in
        //// --- Method Call ---
        //        Map<UUID, DoubleSummaryStatistics> overageStats = calculator.getOverageStatsByPlan(invoices, subscriptions);
        //
        //        // --- Print Results ---
        //        System.out.println("Overage Statistics by Plan:");
        //        overageStats.forEach((planId, stats) -> {
        //            System.out.println("\nPlan ID: " + planId);
        //            System.out.println("  - Total Invoices: " + stats.getCount());
        //            System.out.println("  - Sum of Overage Charges: " + stats.getSum());
        //            System.out.println("  - Average Overage Charge: " + stats.getAverage());
        //            System.out.println("  - Minimum Overage Charge: " + stats.getMin());
        //            System.out.println("  - Maximum Overage Charge: " + stats.getMax());
        //Follow above code
        return overageStatsPerPlan;
    }
    public List<String> detectCreditRisk(String role,List<Invoice> invoices,List<Subscriptions> subscriptions,List<Customer> customers){
        if(!"admin".equalsIgnoreCase(role)){
            return List.of();
        }
        //Mapping customer id to subscription id in Subscription class
        Map<UUID,UUID> customerToSubscriptionMap=subscriptions.stream()
                .collect(Collector.toMap(subscriptions.getId()),subscriptions.getCustomerId());
        //Map customer Id s to names
        Map<UUID,String> customerIdNames=customers.stream()
                .collect(Collectors.toMap(customers.getId(),customers.getName()));
        List<String> customerWhoHaveNotPaidInvoice=invoices.stream()
                .filter(invoice->!invoice.isPaid())
                .map(invoice->customerToSubscriptionMap.get(invoice.getSubscriptionId()))
                .distinct()
                .map(customerIdNames::get)
                .collect(Collectors.toList());
      //Returns Customer names who have not paid invoices
        return customerWhoHaveNotPaidInvoice;
    }


}
