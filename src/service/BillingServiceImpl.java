package service;

public class BillingServiceImpl implements BillingService{
   @Override
    public Invoice generateInvoice(String role, UUID subscriptionId, YearMonth month, List<UsageRecord> usageRecords) {
    Subscription subscription = null;
    Plan plan = null;
    Customer customer = null;

    try {
        subscription = subscriptionRepo.findById(subscriptionId).get();
    } catch (Exception e) {
        System.out.println("Error: Invalid subscription");
        throw new IllegalArgumentException("Invalid subscription");
    }

    try {
        plan = planRepo.findById(subscription.planId()).get();
    } catch (Exception e) {
        System.out.println("Error: Invalid plan");
        throw new IllegalArgumentException("Invalid plan");
    }

    try {
        customer = customerRepo.findById(subscription.customerId()).get();
    } catch (Exception e) {
        System.out.println("Error: Invalid customer");
        throw new IllegalArgumentException("Invalid customer");
    }


        double baseRental = plan.monthlyRental();
        double taxRate = 0.18;

        // 2. Calculate charges
        double overageCharge = calculateOverage(usageRecords, plan.allowance());
        double roamingSurcharge = calculateRoaming(usageRecords, plan.roamingRate());
        double referralDiscount = calculateReferralDiscount(customer);
        double fairnessSurcharge = calculateFairnessSurcharge(subscription);

        // 3. Total
        double subtotal = baseRental + overageCharge + roamingSurcharge + fairnessSurcharge - referralDiscount;
        double tax = subtotal * taxRate;
        double total = subtotal + tax;

        Invoice invoice = new Invoice(
            UUID.randomUUID(),
            subscriptionId,
            month,
            baseRental,
            overageCharge,
            roamingSurcharge,
            referralDiscount,
            fairnessSurcharge,
            tax,
            total,
            false
        );

        return invoiceRepo.save(invoice);
    }

    @Override
    public List<Invoice> generateAllInvoices(String role, YearMonth month) {
        // iterate subscriptions, collect usageRecords, and call generateInvoice for each
        return subscriptionRepo.findAll().stream()
                .map(sub -> generateInvoice(role, sub.id(), month, List.of())) // TODO: pull usage from UsageRepo
                .toList();
    }

    private double calculateOverage(List<UsageRecord> usageRecords, int allowance) {
        int totalUsage = usageRecords.stream().mapToInt(UsageRecord::units).sum();
        return totalUsage > allowance ? (totalUsage - allowance) * 1.0 : 0.0;
    }

    private double calculateRoaming(List<UsageRecord> usageRecords, double roamingRate) {
        return usageRecords.stream()
            .filter(UsageRecord::isRoaming)
            .mapToDouble(u -> u.units() * roamingRate)
            .sum();
    }

    private double calculateReferralDiscount(Customer customer) {
        return (customer.referredBy() != null) ? 10.0 : 0.0;
    }

    private double calculateFairnessSurcharge(Subscription subscription) {
        return subscription.isPooled() ? 5.0 : 0.0;
    }
}
}
