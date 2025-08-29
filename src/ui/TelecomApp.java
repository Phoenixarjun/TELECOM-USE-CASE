package ui;

import model.Customer;
import model.Plan;
import model.Subscription;
import repo.CustomerRepo;
import repo.PlanRepo;
import repo.SubscriptionRepo;
import service.SubscriptionService;
import service.SubscriptionServiceImpl;
import java.util.List;
import java.util.UUID;

public class TelecomApp {
    public static void main(String[] args) {
        CustomerRepo customerRepo = new CustomerRepo();
        PlanRepo planRepo = new PlanRepo();
        SubscriptionRepo subscriptionRepo = new SubscriptionRepo(customerRepo, planRepo);
        SubscriptionService subscriptionService = new SubscriptionServiceImpl(customerRepo, planRepo, subscriptionRepo);

        List<Customer> admins = customerRepo.findAdmins();
        String adminRole = admins.get(0).getRole(); // "admin"
        UUID customerId = admins.get(0).getId(); // Admin's ID
        UUID planId = planRepo.findAll().get(2).getId(); // Premium

        try {
            Subscription newSub = subscriptionService.choosePlan(adminRole, customerId, planId);
            System.out.println("New Subscription: " + newSub);

            List<Subscription> customerSubs = subscriptionService.getSubscriptionsByCustomer(customerId);
            System.out.println("Subscriptions for Sahana: " + customerSubs);

            // Test non-admin access (should throw UnauthorizedAccessException)
            try {
                subscriptionService.choosePlan("customer", customerId, planId);
            } catch (Exception e) {
                System.out.println("Expected error: " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}