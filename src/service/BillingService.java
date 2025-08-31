package service;

public interface BillingService {
  Invoice generateInvoice(String role, UUID subscriptionId, YearMonth month, List<UsageRecord> usageRecords);
    
    // Admin
    List<Invoice> generateAllInvoices(String role, YearMonth month);
}
