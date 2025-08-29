package repo;

import model.Plan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlanRepo {
    private final HashMap<UUID, Plan> storage = new HashMap<>();

    public PlanRepo() {
        UUID basicId = UUID.randomUUID();
        storage.put(basicId, new Plan(basicId, "Basic Plan", 199.0, 5.0, 100, 50, 10.0, 1.0, 0.5, false, 0.0));

        UUID standardId = UUID.randomUUID();
        storage.put(standardId, new Plan(standardId, "Standard Plan", 499.0, 20.0, 500, 200, 8.0, 0.8, 0.4, true, 50.0));

        UUID premiumId = UUID.randomUUID();
        storage.put(premiumId, new Plan(premiumId, "Premium Plan", 999.0, 50.0, 1000, 500, 5.0, 0.5, 0.2, true, 100.0));
    }

    public Plan save(Plan plan) {
        if (plan.getId() == null) {
            plan.setId(UUID.randomUUID());
        }
        storage.put(plan.getId(), plan);
        return plan;
    }

    public Plan findById(UUID id) {
        return storage.get(id);
    }

    public List<Plan> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(UUID id) {
        storage.remove(id);
    }
}