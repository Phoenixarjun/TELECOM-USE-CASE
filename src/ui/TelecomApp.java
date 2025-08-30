package ui;

import exceptions.InvalidCustomerException;
import exceptions.UnauthorizedAccessException;
import model.Customer;
import model.Plan;
import model.Subscription;
import repo.CustomerRepo;
import repo.PlanRepo;
import repo.SubscriptionRepo;
import service.CustomerService;
import service.CustomerServiceImpl;
import service.SubscriptionService;
import service.SubscriptionServiceImpl;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class TelecomApp {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        CustomerRepo customerRepo = new CustomerRepo();
        PlanRepo planRepo = new PlanRepo();
        SubscriptionRepo subscriptionRepo = new SubscriptionRepo(customerRepo, planRepo);
        SubscriptionService subscriptionService = new SubscriptionServiceImpl(customerRepo, planRepo, subscriptionRepo);
        CustomerService customerService = new CustomerServiceImpl(customerRepo);


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

//        boolean flag=true;
//        while (flag){
//            System.out.println("===== TELECOM APP MENU =====");
//            System.out.println("1. Add New Customer");
//            System.out.println("2. View All Customers");
//            System.out.println("3. get Customer By Id");
//            System.out.println("4. Delete Customer (Admin only)");
//            System.out.println("5. role change (Admin Only)");
//
//            int choice=Integer.parseInt(sc.nextLine());
//
//            switch (choice){
//                case 1:
//                    System.out.print("Enter Name: ");
//                    String name = sc.nextLine();
//                    System.out.print("Enter Email: ");
//                    String email = sc.nextLine();
//                    System.out.print("Enter Password: ");
//                    String password = sc.nextLine();
//
//                    Customer newCustomer = new Customer(
//                            null,
//                            name,
//                            email,
//                            password,
//                            null,      // referredBy
//                            false,     // creditBlocked
//                            LocalDate.now(),
//                            "customer" // default role
//                    );
//                    customerService.addCustomer(newCustomer);
//                    System.out.println("Customer added: " + newCustomer);
//                    break;
//                case 2:
//                    System.out.println("--- Customer List ---");
//                    customerService.getAllCustomer().forEach(System.out::println);
//                    break;
//                case 3:
//                    System.out.print("Enter Customer ID: ");
//                    UUID getId = UUID.fromString(sc.nextLine());
//
//                    Customer found = customerService.getCustomer(getId);
//                    if (found != null) {
//                        System.out.println("Found Customer: " + found);
//                    } else {
//                        throw new InvalidCustomerException("Customer not found.");
//                    }
//                    break;
//                case 4:
//                    System.out.println("--- Delete Customer ---");
//                    System.out.print("Enter Admin Customer ID: ");
//                    UUID adminId = UUID.fromString(sc.nextLine());
//
//                    System.out.print("Enter Target Customer ID to delete: ");
//                    UUID deleteTargetId = UUID.fromString(sc.nextLine());
//
//
//                    Customer acting = customerRepo.findById(adminId);
//                    if (acting != null && "admin".equalsIgnoreCase(acting.getRole())) {
//                        customerService.deleteCustomer(deleteTargetId);
//                        System.out.println("Customer deleted successfully.");
//                    } else {
//                        throw new UnauthorizedAccessException("Access Denied");
//                    }
//
//                    break;
//                case 5:
//                    System.out.println("--- Change Role ---");
//                    System.out.print("Enter Acting Admin ID: ");
//                    UUID roleAdminId = UUID.fromString(sc.nextLine());
//                    System.out.print("Enter Target Customer ID: ");
//                    UUID targetCustId = UUID.fromString(sc.nextLine());
//                    System.out.print("Enter New Role (admin/customer): ");
//                    String newRole = sc.nextLine();
//
//                    try {
//                        boolean changed = customerService.roleChange(roleAdminId, targetCustId, newRole);
//                        if (changed) {
//                            System.out.println("Role updated successfully.");
//                        } else {
//                            System.out.println("Target customer not found.");
//                        }
//                    } catch (Exception e) {
//                        System.out.println("Error: " + e.getMessage());
//                    }
//                    break;
//
//            }
       // }
    }
}