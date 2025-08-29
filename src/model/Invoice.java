package model;

import java.time.YearMonth;
import java.util.UUID;

public class Invoice {
    private UUID id;
    private UUID subscriptionId;
    private YearMonth billingMonth;
    private double baseRental;
    private double overageCharge;
    private double roamingSurcharge;
    private double referralDiscount;
    private double fairnessSurcharge;
    private double tax;
    private double total;
    private boolean paid;

    public Invoice(UUID id, UUID subscriptionId, YearMonth billingMonth, double baseRental, double overageCharge,
                   double roamingSurcharge, double referralDiscount, double fairnessSurcharge, double tax,
                   double total, boolean paid) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.billingMonth = billingMonth;
        this.baseRental = baseRental;
        this.overageCharge = overageCharge;
        this.roamingSurcharge = roamingSurcharge;
        this.referralDiscount = referralDiscount;
        this.fairnessSurcharge = fairnessSurcharge;
        this.tax = tax;
        this.total = total;
        this.paid = paid;
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

    public YearMonth getBillingMonth() {
        return billingMonth;
    }

    public void setBillingMonth(YearMonth billingMonth) {
        this.billingMonth = billingMonth;
    }

    public double getBaseRental() {
        return baseRental;
    }

    public void setBaseRental(double baseRental) {
        this.baseRental = baseRental;
    }

    public double getOverageCharge() {
        return overageCharge;
    }

    public void setOverageCharge(double overageCharge) {
        this.overageCharge = overageCharge;
    }

    public double getRoamingSurcharge() {
        return roamingSurcharge;
    }

    public void setRoamingSurcharge(double roamingSurcharge) {
        this.roamingSurcharge = roamingSurcharge;
    }

    public double getReferralDiscount() {
        return referralDiscount;
    }

    public void setReferralDiscount(double referralDiscount) {
        this.referralDiscount = referralDiscount;
    }

    public double getFairnessSurcharge() {
        return fairnessSurcharge;
    }

    public void setFairnessSurcharge(double fairnessSurcharge) {
        this.fairnessSurcharge = fairnessSurcharge;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }
    @Override
    public String toString() {
        return "Invoice{" +
                "id=" + id +
                ", subscriptionId=" + subscriptionId +
                ", billingMonth=" + billingMonth +
                ", baseRental=" + baseRental +
                ", overageCharge=" + overageCharge +
                ", roamingSurcharge=" + roamingSurcharge +
                ", referralDiscount=" + referralDiscount +
                ", fairnessSurcharge=" + fairnessSurcharge +
                ", tax=" + tax +
                ", total=" + total +
                ", paid=" + paid +
                '}';
    }

}
