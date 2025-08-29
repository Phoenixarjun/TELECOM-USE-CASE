package model;

import java.time.LocalDate;
import java.util.UUID;

public class Subscription {
    private UUID id;
    private UUID customerId;
    private UUID planId;
    private LocalDate startDate;
    private LocalDate endDate;
    private UUID familyId;
    private boolean mnpPending;

    public Subscription() {}

    public Subscription(UUID id, UUID customerId, UUID planId, LocalDate startDate, LocalDate endDate,
                        UUID familyId, boolean mnpPending) {
        this.id = id;
        this.customerId = customerId;
        this.planId = planId;
        this.startDate = startDate != null ? startDate : LocalDate.now();
        this.endDate = endDate;
        this.familyId = familyId;
        this.mnpPending = mnpPending;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getPlanId() {
        return planId;
    }

    public void setPlanId(UUID planId) {
        this.planId = planId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public boolean isMnpPending() {
        return mnpPending;
    }

    public void setMnpPending(boolean mnpPending) {
        this.mnpPending = mnpPending;
    }

    @Override
    public String toString() {
        return "Subscription{" +
                "id=" + id +
                ", customerId=" + customerId +
                ", planId=" + planId +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", familyId=" + familyId +
                ", mnpPending=" + mnpPending +
                '}';
    }


}
