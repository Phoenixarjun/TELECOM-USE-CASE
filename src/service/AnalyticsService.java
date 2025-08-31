package service;

import exceptions.InvalidCustomerException;
import exceptions.UnauthorizedAccessException;
import model.*;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AnalyticsService {
    UsageRecord getCustomerUsage(UUID customerId) throws InvalidCustomerException;
    long getActiveSubscriptionsCount(String role) throws UnauthorizedAccessException;
    double getTotalRevenue(String role) throws UnauthorizedAccessException;
    List<Map.Entry<UUID, Double>> topNDataUsers(String role, List<Invoice> invoices, List<UsageRecord> usageRecord, int n);
    Map<String, Double> arpuByPlan(String role, List<Invoice> invoices, List<Plan> plans, List<Subscription> subscriptions);
    Map<UUID, DoubleSummaryStatistics> getOverageStatsByPlan(String role, List<Invoice> invoices, List<Subscription> subscriptions);
    List<String> detectCreditRisk(String role, List<Invoice> invoices, List<Subscription> subscriptions, List<Customer> customers);
    Plan recommendPlan(double avgDataGb, double avgVoiceMinutes, double avgSmsCount);
}