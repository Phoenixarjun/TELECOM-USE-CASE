package model;

import java.util.UUID;

public class Plan {
    private UUID id;
    private String name;
    private double monthlyRental;
    private double dataAllowanceGb;
    private int voiceAllowanceMinutes;
    private int smsAllowance;
    private double overageRateData;
    private double overageRateVoice;
    private double overageRateSms;
    private boolean weekendFreeVoice;
    private double familyShareCap;

    public Plan() {}

    public Plan(UUID id, String name, double monthlyRental, double dataAllowanceGb, int voiceAllowanceMinutes,
                int smsAllowance, double overageRateData, double overageRateVoice, double overageRateSms,
                boolean weekendFreeVoice, double familyShareCap) {
        this.id = id;
        this.name = name;
        this.monthlyRental = monthlyRental;
        this.dataAllowanceGb = dataAllowanceGb;
        this.voiceAllowanceMinutes = voiceAllowanceMinutes;
        this.smsAllowance = smsAllowance;
        this.overageRateData = overageRateData;
        this.overageRateVoice = overageRateVoice;
        this.overageRateSms = overageRateSms;
        this.weekendFreeVoice = weekendFreeVoice;
        this.familyShareCap = familyShareCap;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMonthlyRental() {
        return monthlyRental;
    }

    public void setMonthlyRental(double monthlyRental) {
        this.monthlyRental = monthlyRental;
    }

    public double getDataAllowanceGb() {
        return dataAllowanceGb;
    }

    public void setDataAllowanceGb(double dataAllowanceGb) {
        this.dataAllowanceGb = dataAllowanceGb;
    }

    public int getVoiceAllowanceMinutes() {
        return voiceAllowanceMinutes;
    }

    public void setVoiceAllowanceMinutes(int voiceAllowanceMinutes) {
        this.voiceAllowanceMinutes = voiceAllowanceMinutes;
    }

    public int getSmsAllowance() {
        return smsAllowance;
    }

    public void setSmsAllowance(int smsAllowance) {
        this.smsAllowance = smsAllowance;
    }

    public double getOverageRateData() {
        return overageRateData;
    }

    public void setOverageRateData(double overageRateData) {
        this.overageRateData = overageRateData;
    }

    public double getOverageRateVoice() {
        return overageRateVoice;
    }

    public void setOverageRateVoice(double overageRateVoice) {
        this.overageRateVoice = overageRateVoice;
    }

    public double getOverageRateSms() {
        return overageRateSms;
    }

    public void setOverageRateSms(double overageRateSms) {
        this.overageRateSms = overageRateSms;
    }

    public boolean isWeekendFreeVoice() {
        return weekendFreeVoice;
    }

    public void setWeekendFreeVoice(boolean weekendFreeVoice) {
        this.weekendFreeVoice = weekendFreeVoice;
    }

    public double getFamilyShareCap() {
        return familyShareCap;
    }

    public void setFamilyShareCap(double familyShareCap) {
        this.familyShareCap = familyShareCap;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", monthlyRental=" + monthlyRental +
                ", dataAllowanceGb=" + dataAllowanceGb +
                ", voiceAllowanceMinutes=" + voiceAllowanceMinutes +
                ", smsAllowance=" + smsAllowance +
                ", overageRateData=" + overageRateData +
                ", overageRateVoice=" + overageRateVoice +
                ", overageRateSms=" + overageRateSms +
                ", weekendFreeVoice=" + weekendFreeVoice +
                ", familyShareCap=" + familyShareCap +
                '}';
    }
}