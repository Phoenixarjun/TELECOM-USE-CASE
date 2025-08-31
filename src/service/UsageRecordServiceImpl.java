package service;

import model.UsageRecord;
import repo.UsageRecordRepo;

public class UsageRecordServiceImpl implements UsageRecordService {
    private final UsageRecordRepo usageRecordRepo;

    public UsageRecordServiceImpl(UsageRecordRepo usageRecordRepo) {
        this.usageRecordRepo = usageRecordRepo;
    }

    @Override
    public UsageRecord saveUsageRecord(UsageRecord usageRecord) {
        return usageRecordRepo.save(usageRecord);
    }
}