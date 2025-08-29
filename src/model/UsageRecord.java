package model;

import java.time.LocalDateTime;
import java.util.UUID;

public class UsageRecord {
    private UUID id;
    private UUID subscriptionId;
    private double dataGb;
    private int voiceMinutes;
    private int smsCount;
    private boolean roaming;
    private boolean international;
    private boolean nightTime;
    private LocalDateTime timestamp;

    public UsageRecord(UUID id, UUID subscriptionId, double dataGb, int voiceMinutes, int smsCount,
                       boolean roaming, boolean international, boolean nightTime, LocalDateTime timestamp) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.dataGb = dataGb;
        this.voiceMinutes = voiceMinutes;
        this.smsCount = smsCount;
        this.roaming = roaming;
        this.international = international;
        this.nightTime = nightTime;
        this.timestamp = timestamp != null ? timestamp : LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(UUID subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public double getDataGb() {
        return dataGb;
    }

    public void setDataGb(double dataGb) {
        this.dataGb = dataGb;
    }

    public int getVoiceMinutes() {
        return voiceMinutes;
    }

    public void setVoiceMinutes(int voiceMinutes) {
        this.voiceMinutes = voiceMinutes;
    }

    public int getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(int smsCount) {
        this.smsCount = smsCount;
    }

    public boolean isRoaming() {
        return roaming;
    }

    public void setRoaming(boolean roaming) {
        this.roaming = roaming;
    }

    public boolean isInternational() {
        return international;
    }

    public void setInternational(boolean international) {
        this.international = international;
    }

    public boolean isNightTime() {
        return nightTime;
    }

    public void setNightTime(boolean nightTime) {
        this.nightTime = nightTime;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "UsageRecord{" +
                "id=" + id +
                ", subscriptionId=" + subscriptionId +
                ", dataGb=" + dataGb +
                ", voiceMinutes=" + voiceMinutes +
                ", smsCount=" + smsCount +
                ", roaming=" + roaming +
                ", international=" + international +
                ", nightTime=" + nightTime +
                ", timestamp=" + timestamp +
                '}';
    }
}
