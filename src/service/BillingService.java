package service;

import exceptions.InvalidCustomerException;
import exceptions.UnauthorizedAccessException;
import model.Invoice;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;

public interface BillingService {
    Invoice generateInvoice(String role, UUID subscriptionId, YearMonth billingMonth) throws InvalidCustomerException, UnauthorizedAccessException;
    List<Invoice> getSubscriptionInvoices(UUID subscriptionId) throws InvalidCustomerException;
}