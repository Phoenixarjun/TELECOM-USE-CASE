package repo;

import model.Subscription;
import model.UsageRecord;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class UsageRecordRepo {
    private final HashMap<UUID, UsageRecord> storage = new HashMap<>();

    public UsageRecordRepo(SubscriptionRepo subscriptionRepo) {
        List<Subscription> subs = subscriptionRepo.findAll();
        if (subs.size() >= 3) {
            UUID ur1Id = UUID.randomUUID();
            storage.put(ur1Id, new UsageRecord(ur1Id, subs.get(0).getId(), 3.0, 50, 20, false, false, false, LocalDateTime.now()));

            UUID ur2Id = UUID.randomUUID();
            storage.put(ur2Id, new UsageRecord(ur2Id, subs.get(1).getId(), 10.0, 200, 100, true, false, true, LocalDateTime.now()));

            UUID ur3Id = UUID.randomUUID();
            storage.put(ur3Id, new UsageRecord(ur3Id, subs.get(2).getId(), 25.0, 600, 300, false, true, false, LocalDateTime.now()));
        }
    }

    public UsageRecord save(UsageRecord usageRecord) {
        if (usageRecord.getId() == null) {
            usageRecord.setId(UUID.randomUUID());
        }
        storage.put(usageRecord.getId(), usageRecord);
        return usageRecord;
    }

    public UsageRecord findById(UUID id) {
        return storage.get(id);
    }

    public List<UsageRecord> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(UUID id) {
        storage.remove(id);
    }

    public List<UsageRecord> findBySubscriptionId(UUID subscriptionId) {
        return storage.values().stream()
                .filter(ur -> subscriptionId.equals(ur.getSubscriptionId()))
                .collect(Collectors.toList());
    }
}