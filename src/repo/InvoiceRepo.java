package repo;

import model.Subscription;
import model.Invoice;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InvoiceRepo {
    private final HashMap<UUID, Invoice> storage = new HashMap<>();

    public InvoiceRepo(SubscriptionRepo subscriptionRepo) {
        List<Subscription> subs = subscriptionRepo.findAll();
        YearMonth currentMonth = YearMonth.now();
        if (subs.size() >= 3) {
            // Invoice for first subscription (e.g., Basic Plan: 199.0)
            UUID inv1Id = UUID.randomUUID();
            storage.put(inv1Id, new Invoice(inv1Id, subs.get(0).getId(), currentMonth, 199.0, 0.0, 0.0, 0.0, 0.0, 199.0 * 0.18, 199.0 * 1.18, false));

            // Invoice for second subscription (e.g., Standard Plan: 499.0)
            UUID inv2Id = UUID.randomUUID();
            storage.put(inv2Id, new Invoice(inv2Id, subs.get(1).getId(), currentMonth, 499.0, 50.0, 20.0, 10.0, 0.0, (499.0 + 50.0 + 20.0 - 10.0) * 0.18, (499.0 + 50.0 + 20.0 - 10.0) * 1.18, true));

            // Invoice for third subscription (e.g., Premium Plan: 999.0)
            UUID inv3Id = UUID.randomUUID();
            storage.put(inv3Id, new Invoice(inv3Id, subs.get(2).getId(), currentMonth, 999.0, 100.0, 50.0, 20.0, 10.0, (999.0 + 100.0 + 50.0 - 20.0 + 10.0) * 0.18, (999.0 + 100.0 + 50.0 - 20.0 + 10.0) * 1.18, false));
        }
    }

    public Invoice save(Invoice invoice) {
        if (invoice.getId() == null) {
            invoice.setId(UUID.randomUUID());
        }
        storage.put(invoice.getId(), invoice);
        return invoice;
    }

    public Invoice findById(UUID id) {
        return storage.get(id);
    }

    public List<Invoice> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(UUID id) {
        storage.remove(id);
    }

    public List<Invoice> findBySubscriptionId(UUID subscriptionId) {
        return storage.values().stream()
                .filter(inv -> subscriptionId.equals(inv.getSubscriptionId()))
                .collect(Collectors.toList());
    }
}