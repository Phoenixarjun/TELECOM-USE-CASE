package service;


import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import model.Customer
import model.Subscription
import model.Plan
import model.Invoice
import model.UsageRecord

public interface AnalyticsService {
    List<Map.Entry<UUID, Double>> topDataUsers(String role, List<Invoice> invoices, int n);
    Map<Plan,Double> arpuByPlan(String role,List<Invoice> invoices);
    Map<UUID, DoubleSummaryStatistics> getOverageStatsByPlan(String role,List<Invoice> invoices, List<Subscription> subscriptions);
    List<String> detectCreditRisk(String role,List<Invoice> invoices,List<Subscriptions> subscriptions,List<Customer> customers);
    List<String> planRecommendation(String role,)
}

