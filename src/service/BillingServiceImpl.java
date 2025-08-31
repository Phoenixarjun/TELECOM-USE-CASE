package service;

import exceptions.InvalidCustomerException;
import exceptions.UnauthorizedAccessException;
import model.Customer;
import model.Plan;
import model.Subscription;
import model.UsageRecord;
import model.Invoice;
import repo.CustomerRepo;
import repo.PlanRepo;
import repo.SubscriptionRepo;
import repo.UsageRecordRepo;
import repo.InvoiceRepo;
import java.time.YearMonth;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class BillingServiceImpl implements BillingService {
    private final CustomerRepo customerRepo;
    private final PlanRepo planRepo;
    private final SubscriptionRepo subscriptionRepo;
    private final UsageRecordRepo usageRecordRepo;
    private final InvoiceRepo invoiceRepo;
    private static final double GST_RATE = 0.18;
    private static final double DOMESTIC_ROAMING_UPLIFT = 0.2;
    private static final double INTERNATIONAL_ROAMING_FLAT = 100.0;
    private static final double NIGHT_DISCOUNT_RATE = 0.5;
    private static final double REFERRAL_DISCOUNT = 50.0;
    private static final double FAIRNESS_SURCHARGE = 10.0;
    private static final double ROLLOVER_CAP = 0.5;
    private static final int CREDIT_BLOCK_DAYS = 60;

    public BillingServiceImpl(CustomerRepo customerRepo, PlanRepo planRepo, SubscriptionRepo subscriptionRepo,
                              UsageRecordRepo usageRecordRepo, InvoiceRepo invoiceRepo) {
        this.customerRepo = customerRepo;
        this.planRepo = planRepo;
        this.subscriptionRepo = subscriptionRepo;
        this.usageRecordRepo = usageRecordRepo;
        this.invoiceRepo = invoiceRepo;
    }

    @Override
    public Invoice generateInvoice(String role, UUID subscriptionId, YearMonth billingMonth)
            throws InvalidCustomerException, UnauthorizedAccessException {
        // Validate role and permissions
        if (!"admin".equals(role) && !"customer".equals(role)) {
            throw new UnauthorizedAccessException("Only admin or subscription owner can generate invoices");
        }
        if (subscriptionId == null) {
            throw new InvalidCustomerException("Subscription ID cannot be null");
        }
        if (billingMonth == null) {
            throw new InvalidCustomerException("Billing month cannot be null");
        }

        // Validate subscription, customer, and plan
        Subscription subscription = subscriptionRepo.findById(subscriptionId);
        if (subscription == null) {
            throw new InvalidCustomerException("Subscription not found: " + subscriptionId);
        }
        Customer customer = customerRepo.findById(subscription.getCustomerId());
        if (customer == null) {
            throw new InvalidCustomerException("Customer not found: " + subscription.getCustomerId());
        }
        Plan plan = planRepo.findById(subscription.getPlanId());
        if (plan == null) {
            throw new InvalidCustomerException("Plan not found: " + subscription.getPlanId());
        }
        if ("customer".equals(role) && !customer.getId().equals(subscription.getCustomerId())) {
            throw new UnauthorizedAccessException("Customers can only generate invoices for their own subscriptions");
        }

        // Fetch usage records for the subscription and billing month
        List<UsageRecord> records = usageRecordRepo.findBySubscriptionId(subscriptionId).stream()
                .filter(ur -> ur.getTimestamp() != null &&
                        YearMonth.from(ur.getTimestamp()).equals(billingMonth))
                .collect(Collectors.toList());

        // Debug: Log the number of records found
        System.out.println("DEBUG: Found " + records.size() + " usage records for subscription ID " + subscriptionId +
                " in billing month " + billingMonth);

        // Calculate usage totals
        double dataUsed = records.stream().mapToDouble(UsageRecord::getDataGb).sum();
        int voiceUsed = records.stream().mapToInt(UsageRecord::getVoiceMinutes).sum();
        int smsUsed = records.stream().mapToInt(UsageRecord::getSmsCount).sum();

        // Debug: Log usage totals
        System.out.println("DEBUG: Usage for subscription ID " + subscriptionId + ": Data=" + dataUsed + "GB, Voice=" +
                voiceUsed + "min, SMS=" + smsUsed);

        // Calculate rollover data from previous month
        double rolloverData = calculateRollover(subscriptionId, billingMonth.minusMonths(1),
                plan.getDataAllowanceGb());
        double adjustedDataUsed = Math.max(0, dataUsed - rolloverData);

        // Calculate overage charges using plan-specific rates
        double dataOverage = Math.max(0, adjustedDataUsed - plan.getDataAllowanceGb()) *
                plan.getOverageRateData();
        double voiceOverage = Math.max(0, voiceUsed - plan.getVoiceAllowanceMinutes()) *
                plan.getOverageRateVoice();
        double smsOverage = Math.max(0, smsUsed - plan.getSmsAllowance()) *
                plan.getOverageRateSms();
        double overageCharge = dataOverage + voiceOverage + smsOverage;

        // Calculate roaming surcharge
        double roamingSurcharge = records.stream().filter(UsageRecord::isRoaming).mapToDouble(ur -> {
            if (ur.isInternational()) {
                return INTERNATIONAL_ROAMING_FLAT;
            } else {
                return (ur.getDataGb() + ur.getVoiceMinutes() + ur.getSmsCount()) *
                        DOMESTIC_ROAMING_UPLIFT;
            }
        }).sum();

        // Calculate night discount for voice usage
        double nightDiscount = records.stream()
                .filter(UsageRecord::isNightTime)
                .mapToDouble(ur -> ur.getVoiceMinutes() * NIGHT_DISCOUNT_RATE *
                        plan.getOverageRateVoice())
                .sum();

        // Calculate other charges
        double baseRental = plan.getMonthlyRental();
        double referralDiscount = customer.getReferredBy() != null ? REFERRAL_DISCOUNT : 0.0;
        double fairnessSurcharge = plan.getFamilyShareCap() > 0 ? FAIRNESS_SURCHARGE : 0.0;

        // Calculate total
        double subtotal = baseRental + overageCharge + roamingSurcharge +
                fairnessSurcharge - referralDiscount - nightDiscount;
        double tax = subtotal * GST_RATE;
        double total = subtotal + tax;

        // Debug: Log calculated charges
        System.out.println("DEBUG: Invoice for subscription ID " + subscriptionId + ": BaseRental=" + baseRental +
                ", OverageCharge=" + overageCharge + ", RoamingSurcharge=" + roamingSurcharge +
                ", ReferralDiscount=" + referralDiscount + ", FairnessSurcharge=" + fairnessSurcharge +
                ", NightDiscount=" + nightDiscount + ", Subtotal=" + subtotal + ", Tax=" + tax + ", Total=" + total);

        // Create and save invoice
        Invoice invoice = new Invoice(UUID.randomUUID(), subscriptionId, billingMonth,
                baseRental, overageCharge, roamingSurcharge,
                referralDiscount, fairnessSurcharge, tax, total, false);
        invoiceRepo.save(invoice); // Ensure invoice is saved
        return invoice;
    }

    @Override
    public List<Invoice> getSubscriptionInvoices(UUID subscriptionId)
            throws InvalidCustomerException {
        if (subscriptionId == null) {
            throw new InvalidCustomerException("Subscription ID cannot be null");
        }
        Subscription subscription = subscriptionRepo.findById(subscriptionId);
        if (subscription == null) {
            throw new InvalidCustomerException("Subscription not found: " + subscriptionId);
        }
        return invoiceRepo.findBySubscriptionId(subscriptionId);
    }

    private double calculateRollover(UUID subscriptionId, YearMonth previousMonth,
                                     double allowance) {
        List<UsageRecord> previousRecords = usageRecordRepo.findBySubscriptionId(subscriptionId)
                .stream()
                .filter(ur -> ur.getTimestamp() != null &&
                        YearMonth.from(ur.getTimestamp()).equals(previousMonth))
                .collect(Collectors.toList());
        double previousDataUsed = previousRecords.stream()
                .mapToDouble(UsageRecord::getDataGb).sum();
        double unusedData = Math.max(0, allowance - previousDataUsed);
        return Math.min(unusedData, allowance * ROLLOVER_CAP);
    }

    private void applyCreditControl(Customer customer, YearMonth billingMonth) {
        List<Invoice> unpaidInvoices = invoiceRepo.findAll().stream()
                .filter(inv -> !inv.isPaid() && inv.getSubscriptionId() != null &&
                        subscriptionRepo.findById(inv.getSubscriptionId())
                                .getCustomerId().equals(customer.getId()))
                .filter(inv -> inv.getBillingMonth() != null &&
                        inv.getBillingMonth().isBefore(billingMonth.minusMonths(2)))
                .collect(Collectors.toList());
        if (!unpaidInvoices.isEmpty()) {
            customer.setCreditBlocked(true);
            customerRepo.save(customer);
        }
    }
}