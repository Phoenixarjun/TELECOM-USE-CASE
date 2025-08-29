package repo;

import model.Customer;
import model.Plan;
import model.Subscription;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SubscriptionRepo {
    private final ConcurrentHashMap<UUID, Subscription> storage = new ConcurrentHashMap<>();

    public SubscriptionRepo(CustomerRepo customerRepo, PlanRepo planRepo) {
        List<Customer> customers = customerRepo.findAll();
        List<Plan> plans = planRepo.findAll();

        if (!customers.isEmpty() && !plans.isEmpty()) {
            // Assign Basic to Prabhat
            UUID sub1Id = UUID.randomUUID();
            storage.put(sub1Id, new Subscription(sub1Id, customers.get(0).getId(), plans.get(0).getId(), LocalDate.now(), null, null, false));

            // Assign Standard to Ankit
            UUID sub2Id = UUID.randomUUID();
            storage.put(sub2Id, new Subscription(sub2Id, customers.get(1).getId(), plans.get(1).getId(), LocalDate.now(), null, null, false));

            // Assign Premium to Naresh
            UUID sub3Id = UUID.randomUUID();
            storage.put(sub3Id, new Subscription(sub3Id, customers.get(2).getId(), plans.get(2).getId(), LocalDate.now(), null, null, false));

            // Add more for other customers
            UUID sub4Id = UUID.randomUUID();
            storage.put(sub4Id, new Subscription(sub4Id, customers.get(3).getId(), plans.get(0).getId(), LocalDate.now(), null, null, false));

            UUID sub5Id = UUID.randomUUID();
            storage.put(sub5Id, new Subscription(sub5Id, customers.get(4).getId(), plans.get(1).getId(), LocalDate.now(), null, null, false));
        }
    }

    public Subscription save(Subscription subscription) {
        if (subscription.getId() == null) {
            subscription.setId(UUID.randomUUID());
        }
        storage.put(subscription.getId(), subscription);
        return subscription;
    }

    public Subscription findById(UUID id) {
        return storage.get(id);
    }

    public List<Subscription> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(UUID id) {
        storage.remove(id);
    }

    public List<Subscription> findByCustomerId(UUID customerId) {
        return storage.values().stream()
                .filter(sub -> customerId.equals(sub.getCustomerId()))
                .collect(Collectors.toList());
    }
}