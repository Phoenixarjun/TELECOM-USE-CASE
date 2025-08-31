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
    private final HashMap<UUID, List<UsageRecord>> storage = new HashMap<>();
    private final SubscriptionRepo subscriptionRepo;

    public UsageRecordRepo(SubscriptionRepo subscriptionRepo) {
        this.subscriptionRepo = subscriptionRepo;
        // Initialize sample records for testing (optional, can be removed in production)
        List<Subscription> subs = subscriptionRepo.findAll();
        if (subs.size() >= 3) {
            UUID sub1Id = subs.get(0).getId();
            UUID sub2Id = subs.get(1).getId();
            UUID sub3Id = subs.get(2).getId();
            storage.computeIfAbsent(sub1Id, k -> new ArrayList<>())
                    .add(new UsageRecord(UUID.randomUUID(), sub1Id, 3.0, 50, 20, false, false, false, LocalDateTime.of(2025, 8, 1, 0, 0)));
            storage.computeIfAbsent(sub2Id, k -> new ArrayList<>())
                    .add(new UsageRecord(UUID.randomUUID(), sub2Id, 10.0, 200, 100, true, false, true, LocalDateTime.of(2025, 8, 1, 0, 0)));
            storage.computeIfAbsent(sub3Id, k -> new ArrayList<>())
                    .add(new UsageRecord(UUID.randomUUID(), sub3Id, 25.0, 600, 300, false, true, false, LocalDateTime.of(2025, 8, 1, 0, 0)));
        }
    }

    public UsageRecord save(UsageRecord usageRecord) {
        if (usageRecord.getId() == null) {
            usageRecord.setId(UUID.randomUUID());
        }
        storage.computeIfAbsent(usageRecord.getSubscriptionId(), k -> new ArrayList<>()).add(usageRecord);
        System.out.println("DEBUG: Saved usage record: ID=" + usageRecord.getId() + ", SubscriptionID=" + usageRecord.getSubscriptionId() +
                ", Data=" + usageRecord.getDataGb() + ", Voice=" + usageRecord.getVoiceMinutes() +
                ", SMS=" + usageRecord.getSmsCount() + ", Timestamp=" + usageRecord.getTimestamp());
        return usageRecord;
    }

    public UsageRecord findById(UUID id) {
        return storage.values().stream()
                .flatMap(List::stream)
                .filter(ur -> id.equals(ur.getId()))
                .findFirst()
                .orElse(null);
    }

    public List<UsageRecord> findAll() {
        return storage.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        storage.values().forEach(list -> list.removeIf(ur -> id.equals(ur.getId())));
    }

    public List<UsageRecord> findBySubscriptionId(UUID subscriptionId) {
        List<UsageRecord> records = storage.getOrDefault(subscriptionId, new ArrayList<>());
        System.out.println("DEBUG: Retrieved " + records.size() + " records for subscription ID " + subscriptionId + ": " + records);
        return records;
    }
}