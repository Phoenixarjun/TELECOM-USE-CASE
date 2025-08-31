package ui;

import model.Customer;
import model.Plan;
import model.Subscription;
import model.UsageRecord;
import model.Invoice;
import service.AuthService;
import service.AuthServiceImpl;
import service.CustomerService;
import service.CustomerServiceImpl;
import service.PlanService;
import service.PlanServiceImpl;
import service.SubscriptionService;
import service.SubscriptionServiceImpl;
import service.AnalyticsService;
import service.AnalyticsServiceImpl;
import service.BillingService;
import service.BillingServiceImpl;
import service.UsageRecordService;
import service.UsageRecordServiceImpl;
import repo.CustomerRepo;
import repo.PlanRepo;
import repo.SubscriptionRepo;
import repo.UsageRecordRepo;
import repo.InvoiceRepo;
import exceptions.InvalidCustomerException;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidPlanException;
import exceptions.InvalidSubscriptionException;
import exceptions.UnauthorizedAccessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;

public class TelecomApp {
    private static CustomerRepo customerRepo = new CustomerRepo();
    private static PlanRepo planRepo = new PlanRepo();
    private static SubscriptionRepo subscriptionRepo = new SubscriptionRepo(customerRepo, planRepo);
    private static UsageRecordRepo usageRecordRepo = new UsageRecordRepo(subscriptionRepo);
    private static InvoiceRepo invoiceRepo = new InvoiceRepo(subscriptionRepo);
    private static AuthService authService = new AuthServiceImpl(customerRepo);
    private static CustomerService customerService = new CustomerServiceImpl(customerRepo);
    private static PlanService planService = new PlanServiceImpl(planRepo);
    private static SubscriptionService subscriptionService = new SubscriptionServiceImpl(customerRepo, planRepo, subscriptionRepo);
    private static AnalyticsService analyticsService = new AnalyticsServiceImpl(customerRepo, subscriptionRepo, usageRecordRepo, invoiceRepo, planRepo);
    private static BillingService billingService = new BillingServiceImpl(customerRepo, planRepo, subscriptionRepo, usageRecordRepo, invoiceRepo);
    private static UsageRecordService usageRecordService = new UsageRecordServiceImpl(usageRecordRepo);
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            displayMainMenu();
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        handleLogin();
                        break;
                    case "2":
                        handleRegister();
                        break;
                    case "3":
                        System.out.println("Exiting Telecom App. Goodbye!");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Invalid option. Please choose 1, 2, or 3.");
                }
            } catch (InvalidCredentialsException | InvalidCustomerException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n=============================");
        System.out.println("Telecom App Menu");
        System.out.println("=============================");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option (1-3): ");
    }

    private static void handleLogin() throws InvalidCredentialsException {
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        Customer customer = authService.login(email, password);
        System.out.println("Login successful! Welcome, " + customer.getName() + " (" + customer.getRole() + ")");
        if ("admin".equals(customer.getRole())) {
            handleAdminDashboard(customer);
        } else {
            handleCustomerDashboard(customer);
        }
    }

    private static void handleRegister() throws InvalidCustomerException {
        System.out.print("Enter name (less than 10 characters): ");
        String name = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        System.out.print("Enter password (min 8 chars, 1 upper, 1 lower, 1 digit, 1 special): ");
        String password = scanner.nextLine();
        System.out.print("Enter phone number (10 digits): ");
        String phoneNumber = scanner.nextLine();
        Customer customer = authService.register(name, email, password, phoneNumber);
        System.out.println("Registration successful! Welcome, " + customer.getName() + " (customer)");
    }

    private static void handleAdminDashboard(Customer admin) {
        while (true) {
            displayAdminMenu();
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        handleCustomerManagement(admin);
                        break;
                    case "2":
                        handlePlanManagement(admin.getRole());
                        break;
                    case "3":
                        handleSubscriptionManagement(admin.getRole(), null);
                        break;
                    case "4":
                        handleBillingManagement(admin.getRole(), admin);
                        break;
                    case "5":
                        handleAnalyticsManagement(admin.getRole(), admin);
                        break;
                    case "6":
                        System.out.println("Logging out admin...");
                        return;
                    default:
                        System.out.println("Invalid option. Please choose 1-6.");
                }
            } catch (InvalidCustomerException | InvalidPlanException | InvalidSubscriptionException | UnauthorizedAccessException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayAdminMenu() {
        System.out.println("\n=============================");
        System.out.println("Admin Dashboard");
        System.out.println("=============================");
        System.out.println("1. Customer Management");
        System.out.println("2. Plan Management");
        System.out.println("3. Subscription Management");
        System.out.println("4. Billing Management");
        System.out.println("5. Analytics");
        System.out.println("6. Logout");
        System.out.print("Choose an option (1-6): ");
    }

    private static void handleCustomerDashboard(Customer customer) {
        while (true) {
            displayCustomerMenu();
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        handlePlanManagement(customer.getRole());
                        break;
                    case "2":
                        handleSubscriptionManagement(customer.getRole(), customer.getPhoneNumber());
                        break;
                    case "3":
                        handleBillingManagement(customer.getRole(), customer);
                        break;
                    case "4":
                        handleAnalyticsManagement(customer.getRole(), customer);
                        break;
                    case "5":
                        System.out.println("Logging out...");
                        return;
                    default:
                        System.out.println("Invalid option. Please choose 1-5.");
                }
            } catch (InvalidCustomerException | InvalidPlanException | InvalidSubscriptionException | UnauthorizedAccessException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void displayCustomerMenu() {
        System.out.println("\n=============================");
        System.out.println("Customer Dashboard");
        System.out.println("=============================");
        System.out.println("1. Plan Management");
        System.out.println("2. Subscription Management");
        System.out.println("3. Billing Management");
        System.out.println("4. Analytics");
        System.out.println("5. Logout");
        System.out.print("Choose an option (1-5): ");
    }

    private static void handleCustomerManagement(Customer admin) {
        while (true) {
            System.out.println("\n=============================");
            System.out.println("Customer Management");
            System.out.println("=============================");
            System.out.println("1. Add Customer");
            System.out.println("2. Update Customer");
            System.out.println("3. Delete Customer");
            System.out.println("4. View All Customers");
            System.out.println("5. Change Role");
            System.out.println("6. Back");
            System.out.print("Choose an option (1-6): ");
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter name (less than 10 characters): ");
                        String name = scanner.nextLine();
                        System.out.print("Enter email: ");
                        String email = scanner.nextLine();
                        System.out.print("Enter password (min 8 chars, 1 upper, 1 lower, 1 digit, 1 special): ");
                        String password = scanner.nextLine();
                        System.out.print("Enter phone number (10 digits): ");
                        String phoneNumber = scanner.nextLine();
                        Customer newCustomer = new Customer(null, name, email, password, phoneNumber, null, false, LocalDate.now(), "customer");
                        Customer saved = customerService.addCustomer(newCustomer);
                        System.out.println("Customer added: " + saved);
                        break;
                    case "2":
                        System.out.print("Enter customer phone number: ");
                        String phone = scanner.nextLine();
                        Customer existing = customerRepo.findByPhoneNumber(phone);
                        if (existing == null) {
                            System.out.println("Error: Customer not found with phone number: " + phone);
                            break;
                        }
                        System.out.print("Enter new name (current: " + existing.getName() + ", less than 10 characters): ");
                        name = scanner.nextLine();
                        if (!name.isBlank()) existing.setName(name);
                        System.out.print("Enter new email (current: " + existing.getEmail() + "): ");
                        email = scanner.nextLine();
                        if (!email.isBlank()) existing.setEmail(email);
                        System.out.print("Enter new password (current: [hidden], min 8 chars, 1 upper, 1 lower, 1 digit, 1 special): ");
                        password = scanner.nextLine();
                        if (!password.isBlank()) existing.setPassword(password);
                        System.out.print("Enter new phone number (current: " + existing.getPhoneNumber() + ", 10 digits): ");
                        phoneNumber = scanner.nextLine();
                        if (!phoneNumber.isBlank()) existing.setPhoneNumber(phoneNumber);
                        customerService.updateCustomer(existing);
                        System.out.println("Customer updated.");
                        break;
                    case "3":
                        System.out.print("Enter customer phone number: ");
                        phone = scanner.nextLine();
                        existing = customerRepo.findByPhoneNumber(phone);
                        if (existing == null) {
                            System.out.println("Error: Customer not found with phone number: " + phone);
                            break;
                        }
                        customerService.deleteCustomer(existing.getId());
                        System.out.println("Customer deleted.");
                        break;
                    case "4":
                        System.out.println("All Customers:");
                        List<Customer> customers = customerService.getAllCustomer();
                        for (Customer c : customers) {
                            System.out.println(c);
                        }
                        break;
                    case "5":
                        System.out.print("Enter customer phone number: ");
                        phone = scanner.nextLine();
                        System.out.print("Enter new role: ");
                        String newRole = scanner.nextLine();
                        customerService.roleChange(admin.getId(), phone, newRole);
                        System.out.println("Role changed.");
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (InvalidCustomerException | IllegalArgumentException | UnauthorizedAccessException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void handlePlanManagement(String role) {
        while (true) {
            System.out.println("\n=============================");
            System.out.println("Plan Management");
            System.out.println("=============================");
            if ("admin".equals(role)) {
                System.out.println("1. Add Plan");
                System.out.println("2. Update Plan");
                System.out.println("3. Delete Plan");
            }
            System.out.println("4. View All Plans");
            System.out.println("5. Search Plans by Name");
            System.out.println("6. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        if ("admin".equals(role)) {
                            System.out.print("Enter plan name: ");
                            String planName = scanner.nextLine();
                            System.out.print("Enter monthly rental: ");
                            double monthlyRental = Double.parseDouble(scanner.nextLine());
                            System.out.print("Enter data allowance (GB): ");
                            double dataAllowance = Double.parseDouble(scanner.nextLine());
                            System.out.print("Enter voice allowance (minutes): ");
                            int voiceAllowance = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter SMS allowance: ");
                            int smsAllowance = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter overage rate data ($/GB): ");
                            double overageRateData = Double.parseDouble(scanner.nextLine());
                            System.out.print("Enter overage rate voice ($/minute): ");
                            double overageRateVoice = Double.parseDouble(scanner.nextLine());
                            System.out.print("Enter overage rate SMS ($/SMS): ");
                            double overageRateSms = Double.parseDouble(scanner.nextLine());
                            System.out.print("Weekend free voice (true/false): ");
                            boolean weekendFreeVoice = Boolean.parseBoolean(scanner.nextLine());
                            System.out.print("Family share cap: ");
                            double familyShareCap = Double.parseDouble(scanner.nextLine());
                            Plan newPlan = new Plan(null, planName, monthlyRental, dataAllowance, voiceAllowance, smsAllowance, overageRateData, overageRateVoice, overageRateSms, weekendFreeVoice, familyShareCap);
                            planService.addPlan(newPlan, role);
                            System.out.println("Plan added.");
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "2":
                        if ("admin".equals(role)) {
                            System.out.println("Available Plans:");
                            List<Plan> plans = planService.getAllPlans();
                            for (Plan plan : plans) {
                                System.out.println("Plan: ID=" + plan.getId() + ", Name=" + plan.getName() + ", Monthly Rental=" + plan.getMonthlyRental() + ", Data=" + plan.getDataAllowanceGb() + "GB, Voice=" + plan.getVoiceAllowanceMinutes() + "min, SMS=" + plan.getSmsAllowance());
                            }
                            System.out.print("Enter plan ID: ");
                            UUID planId = UUID.fromString(scanner.nextLine());
                            Plan existing = planService.getPlan(planId);
                            System.out.print("Enter new plan name (current: " + existing.getName() + "): ");
                            String planName = scanner.nextLine();
                            if (planName.isBlank()) planName = existing.getName();
                            System.out.print("Enter new monthly rental (current: " + existing.getMonthlyRental() + "): ");
                            String input = scanner.nextLine();
                            double monthlyRental = input.isBlank() ? existing.getMonthlyRental() : Double.parseDouble(input);
                            System.out.print("Enter new data allowance (GB) (current: " + existing.getDataAllowanceGb() + "): ");
                            input = scanner.nextLine();
                            double dataAllowance = input.isBlank() ? existing.getDataAllowanceGb() : Double.parseDouble(input);
                            System.out.print("Enter new voice allowance (minutes) (current: " + existing.getVoiceAllowanceMinutes() + "): ");
                            input = scanner.nextLine();
                            int voiceAllowance = input.isBlank() ? existing.getVoiceAllowanceMinutes() : Integer.parseInt(input);
                            System.out.print("Enter new SMS allowance (current: " + existing.getSmsAllowance() + "): ");
                            input = scanner.nextLine();
                            int smsAllowance = input.isBlank() ? existing.getSmsAllowance() : Integer.parseInt(input);
                            System.out.print("Enter new overage rate data ($/GB) (current: " + existing.getOverageRateData() + "): ");
                            input = scanner.nextLine();
                            double overageRateData = input.isBlank() ? existing.getOverageRateData() : Double.parseDouble(input);
                            System.out.print("Enter new overage rate voice ($/minute) (current: " + existing.getOverageRateVoice() + "): ");
                            input = scanner.nextLine();
                            double overageRateVoice = input.isBlank() ? existing.getOverageRateVoice() : Double.parseDouble(input);
                            System.out.print("Enter new overage rate SMS ($/SMS) (current: " + existing.getOverageRateSms() + "): ");
                            input = scanner.nextLine();
                            double overageRateSms = input.isBlank() ? existing.getOverageRateSms() : Double.parseDouble(input);
                            System.out.print("Weekend free voice (true/false) (current: " + existing.isWeekendFreeVoice() + "): ");
                            input = scanner.nextLine();
                            boolean weekendFreeVoice = input.isBlank() ? existing.isWeekendFreeVoice() : Boolean.parseBoolean(input);
                            System.out.print("Family share cap (current: " + existing.getFamilyShareCap() + "): ");
                            input = scanner.nextLine();
                            double familyShareCap = input.isBlank() ? existing.getFamilyShareCap() : Double.parseDouble(input);
                            Plan updatedPlan = new Plan(planId, planName, monthlyRental, dataAllowance, voiceAllowance, smsAllowance, overageRateData, overageRateVoice, overageRateSms, weekendFreeVoice, familyShareCap);
                            planService.updatePlan(updatedPlan, role);
                            System.out.println("Plan updated: Name=" + updatedPlan.getName() + ", Monthly Rental=" + updatedPlan.getMonthlyRental() + ", Data Allowance=" + updatedPlan.getDataAllowanceGb() + "GB");
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "3":
                        if ("admin".equals(role)) {
                            System.out.println("Available Plans:");
                            List<Plan> plans = planService.getAllPlans();
                            for (Plan plan : plans) {
                                System.out.println("Plan: ID=" + plan.getId() + ", Name=" + plan.getName() + ", Monthly Rental=" + plan.getMonthlyRental() + ", Data=" + plan.getDataAllowanceGb() + "GB, Voice=" + plan.getVoiceAllowanceMinutes() + "min, SMS=" + plan.getSmsAllowance());
                            }
                            System.out.print("Enter plan ID: ");
                            UUID planId = UUID.fromString(scanner.nextLine());
                            planService.deletePlan(planId, role);
                            System.out.println("Plan deleted.");
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "4":
                        System.out.println("Available Plans:");
                        List<Plan> plans = planService.getAllPlans();
                        if (plans.isEmpty()) {
                            System.out.println("No plans available.");
                        } else {
                            for (Plan plan : plans) {
                                System.out.println("Plan: ID=" + plan.getId() + ", Name=" + plan.getName() + ", Monthly Rental=" + plan.getMonthlyRental() + ", Data=" + plan.getDataAllowanceGb() + "GB, Voice=" + plan.getVoiceAllowanceMinutes() + "min, SMS=" + plan.getSmsAllowance());
                            }
                        }
                        break;
                    case "5":
                        System.out.print("Enter name substring to search: ");
                        String nameSubstring = scanner.nextLine();
                        List<Plan> searchedPlans = planService.searchPlansByName(nameSubstring);
                        System.out.println("Searched Plans:");
                        if (searchedPlans.isEmpty()) {
                            System.out.println("No plans found.");
                        } else {
                            for (Plan plan : searchedPlans) {
                                System.out.println("Plan: ID=" + plan.getId() + ", Name=" + plan.getName() + ", Monthly Rental=" + plan.getMonthlyRental() + ", Data=" + plan.getDataAllowanceGb() + "GB, Voice=" + plan.getVoiceAllowanceMinutes() + "min, SMS=" + plan.getSmsAllowance());
                            }
                        }
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (InvalidPlanException | IllegalArgumentException | UnauthorizedAccessException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void handleSubscriptionManagement(String role, String loggedInPhoneNumber) {
        while (true) {
            System.out.println("\n=============================");
            System.out.println("Subscription Management");
            System.out.println("=============================");
            System.out.println("1. Choose Plan");
            if ("admin".equals(role)) {
                System.out.println("2. Add Subscription");
                System.out.println("3. Update Subscription");
                System.out.println("4. Delete Subscription");
            }
            System.out.println("5. View Subscriptions");
            System.out.println("6. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            try {
                String phoneNumber = loggedInPhoneNumber;
                if ("admin".equals(role)) {
                    phoneNumber = null;
                } else if (phoneNumber == null) {
                    System.out.println("Error: No logged-in customer phone number provided.");
                    break;
                }
                switch (choice) {
                    case "1":
                        if (!"admin".equals(role)) {
                            phoneNumber = loggedInPhoneNumber;
                        } else {
                            System.out.print("Enter customer phone number: ");
                            phoneNumber = scanner.nextLine();
                        }
                        Customer customer = customerRepo.findByPhoneNumber(phoneNumber);
                        if (customer == null) {
                            System.out.println("Error: Customer not found with phone number: " + phoneNumber);
                            break;
                        }
                        System.out.println("Available Plans:");
                        List<Plan> plans = planService.getAllPlans();
                        if (plans.isEmpty()) {
                            System.out.println("No plans available.");
                            break;
                        }
                        for (Plan plan : plans) {
                            System.out.println("Plan: ID=" + plan.getId() + ", Name=" + plan.getName() + ", Monthly Rental=" + plan.getMonthlyRental() + ", Data=" + plan.getDataAllowanceGb() + "GB, Voice=" + plan.getVoiceAllowanceMinutes() + "min, SMS=" + plan.getSmsAllowance());
                        }
                        System.out.print("Enter plan ID: ");
                        UUID planId = UUID.fromString(scanner.nextLine());
                        Subscription sub = subscriptionService.choosePlan(role, phoneNumber, planId);
                        Plan selectedPlan = planService.getPlan(sub.getPlanId());
                        System.out.println("Plan chosen: Subscription ID=" + sub.getId() + ", Customer=" + customer.getName() + ", Plan=" + selectedPlan.getName() + ", Start Date=" + sub.getStartDate());
                        break;
                    case "2":
                        if ("admin".equals(role)) {
                            System.out.print("Enter customer phone number: ");
                            phoneNumber = scanner.nextLine();
                            customer = customerRepo.findByPhoneNumber(phoneNumber);
                            if (customer == null) {
                                System.out.println("Error: Customer not found with phone number: " + phoneNumber);
                                break;
                            }
                            System.out.println("Available Plans:");
                            plans = planService.getAllPlans();
                            if (plans.isEmpty()) {
                                System.out.println("No plans available.");
                                break;
                            }
                            for (Plan plan : plans) {
                                System.out.println("Plan: ID=" + plan.getId() + ", Name=" + plan.getName() + ", Monthly Rental=" + plan.getMonthlyRental() + ", Data=" + plan.getDataAllowanceGb() + "GB, Voice=" + plan.getVoiceAllowanceMinutes() + "min, SMS=" + plan.getSmsAllowance());
                            }
                            System.out.print("Enter plan ID: ");
                            planId = UUID.fromString(scanner.nextLine());
                            Subscription newSub = new Subscription(null, customer.getId(), planId, LocalDate.now(), null, null, false);
                            subscriptionService.addSubscription(role, newSub);
                            Plan addedPlan = planService.getPlan(newSub.getPlanId());
                            System.out.println("Subscription added: ID=" + newSub.getId() + ", Customer=" + customer.getName() + ", Plan=" + addedPlan.getName() + ", Start Date=" + newSub.getStartDate());
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "3":
                        if ("admin".equals(role)) {
                            System.out.print("Enter subscription ID: ");
                            UUID subId = UUID.fromString(scanner.nextLine());
                            System.out.print("Enter customer phone number: ");
                            phoneNumber = scanner.nextLine();
                            customer = customerRepo.findByPhoneNumber(phoneNumber);
                            if (customer == null) {
                                System.out.println("Error: Customer not found with phone number: " + phoneNumber);
                                break;
                            }
                            System.out.println("Available Plans:");
                            plans = planService.getAllPlans();
                            if (plans.isEmpty()) {
                                System.out.println("No plans available.");
                                break;
                            }
                            for (Plan plan : plans) {
                                System.out.println("Plan: ID=" + plan.getId() + ", Name=" + plan.getName() + ", Monthly Rental=" + plan.getMonthlyRental() + ", Data=" + plan.getDataAllowanceGb() + "GB, Voice=" + plan.getVoiceAllowanceMinutes() + "min, SMS=" + plan.getSmsAllowance());
                            }
                            System.out.print("Enter plan ID: ");
                            planId = UUID.fromString(scanner.nextLine());
                            Subscription updatedSub = new Subscription(subId, customer.getId(), planId, LocalDate.now(), null, null, false);
                            subscriptionService.updateSubscription(role, updatedSub);
                            Plan updatedPlan = planService.getPlan(updatedSub.getPlanId());
                            System.out.println("Subscription updated: ID=" + updatedSub.getId() + ", Customer=" + customer.getName() + ", Plan=" + updatedPlan.getName() + ", Start Date=" + updatedSub.getStartDate());
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "4":
                        if ("admin".equals(role)) {
                            System.out.print("Enter subscription ID: ");
                            UUID subId = UUID.fromString(scanner.nextLine());
                            subscriptionService.deleteSubscription(role, subId);
                            System.out.println("Subscription deleted.");
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "5":
                        if (!"admin".equals(role)) {
                            phoneNumber = loggedInPhoneNumber;
                        } else {
                            System.out.print("Enter customer phone number: ");
                            phoneNumber = scanner.nextLine();
                        }
                        List<Subscription> subs = subscriptionService.getSubscriptionsByCustomer(phoneNumber);
                        System.out.println("Subscriptions:");
                        if (subs.isEmpty()) {
                            System.out.println("No subscriptions found.");
                        } else {
                            for (Subscription s : subs) {
                                Customer subCustomer = customerRepo.findById(s.getCustomerId());
                                Plan subPlan = planService.getPlan(s.getPlanId());
                                System.out.println("Subscription: ID=" + s.getId() + ", Customer=" + (subCustomer != null ? subCustomer.getName() : "Unknown") + ", Plan=" + (subPlan != null ? subPlan.getName() : "Unknown") + ", Start Date=" + s.getStartDate());
                            }
                        }
                        break;
                    case "6":
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (InvalidSubscriptionException | InvalidCustomerException | InvalidPlanException | UnauthorizedAccessException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void handleBillingManagement(String role, Customer loggedInCustomer) {
        while (true) {
            System.out.println("\n=============================");
            System.out.println("Billing Management");
            System.out.println("=============================");
            System.out.println("1. Generate Invoice");
            System.out.println("2. View Invoices");
            System.out.println("3. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            try {
                String phoneNumber = "admin".equals(role) ? null : loggedInCustomer.getPhoneNumber();
                switch (choice) {
                    case "1":
                        if ("admin".equals(role)) {
                            System.out.print("Enter customer phone number: ");
                            phoneNumber = scanner.nextLine();
                        }
                        Customer customer = customerRepo.findByPhoneNumber(phoneNumber);
                        if (customer == null) {
                            System.out.println("Error: Customer not found with phone number: " + phoneNumber);
                            break;
                        }
                        List<Subscription> subs = subscriptionService.getSubscriptionsByCustomer(phoneNumber);
                        if (subs.isEmpty()) {
                            System.out.println("Error: No subscriptions found for customer: " + customer.getName());
                            break;
                        }
                        System.out.println("Customer Subscriptions:");
                        for (Subscription s : subs) {
                            Plan subPlan = planService.getPlan(s.getPlanId());
                            System.out.println("Subscription: ID=" + s.getId() + ", Plan=" + (subPlan != null ? subPlan.getName() : "Unknown") + ", Start Date=" + s.getStartDate());
                        }
                        System.out.print("Enter subscription ID: ");
                        UUID subId = UUID.fromString(scanner.nextLine());
                        Subscription selectedSub = subscriptionRepo.findById(subId);
                        if (selectedSub == null) {
                            System.out.println("Error: Subscription not found with ID: " + subId);
                            break;
                        }
                        // Prompt to record usage if none exists for the billing period
                        System.out.print("Do you want to record usage for this subscription before generating the invoice? (yes/no): ");
                        String recordUsage = scanner.nextLine();
                        if ("yes".equalsIgnoreCase(recordUsage)) {
                            System.out.print("Enter data used (GB): ");
                            double dataGb = Double.parseDouble(scanner.nextLine());
                            System.out.print("Enter voice minutes used: ");
                            int voiceMinutes = Integer.parseInt(scanner.nextLine());
                            System.out.print("Enter SMS count: ");
                            int smsCount = Integer.parseInt(scanner.nextLine());
                            System.out.print("Is roaming (true/false): ");
                            boolean roaming = Boolean.parseBoolean(scanner.nextLine());
                            System.out.print("Is international (true/false): ");
                            boolean international = Boolean.parseBoolean(scanner.nextLine());
                            System.out.print("Is nighttime (true/false): ");
                            boolean nightTime = Boolean.parseBoolean(scanner.nextLine());
                            UsageRecord usageRecord = new UsageRecord(null, subId, dataGb, voiceMinutes, smsCount, roaming, international, nightTime, LocalDateTime.now());
                            usageRecordService.saveUsageRecord(usageRecord);
                            System.out.println("Usage recorded for Subscription ID=" + subId + ", Customer=" + customer.getName());
                        }
                        System.out.print("Enter billing month (YYYY-MM): ");
                        YearMonth billingMonth = YearMonth.parse(scanner.nextLine());
                        Invoice invoice = billingService.generateInvoice(role, subId, billingMonth);
                        System.out.println("Invoice generated: ID=" + invoice.getId() + ", Subscription ID=" + invoice.getSubscriptionId() + ", Billing Month=" + invoice.getBillingMonth() + ", Base Rental=" + invoice.getBaseRental() + ", Overage Charge=" + invoice.getOverageCharge() + ", Roaming Surcharge=" + invoice.getRoamingSurcharge() + ", Referral Discount=" + invoice.getReferralDiscount() + ", Fairness Surcharge=" + invoice.getFairnessSurcharge() + ", Tax=" + invoice.getTax() + ", Total=" + invoice.getTotal() + ", Paid=" + invoice.isPaid());
                        break;
                    case "2":
                        if ("admin".equals(role)) {
                            System.out.print("Enter customer phone number: ");
                            phoneNumber = scanner.nextLine();
                        }
                        customer = customerRepo.findByPhoneNumber(phoneNumber);
                        if (customer == null) {
                            System.out.println("Error: Customer not found with phone number: " + phoneNumber);
                            break;
                        }
                        subs = subscriptionService.getSubscriptionsByCustomer(phoneNumber);
                        if (subs.isEmpty()) {
                            System.out.println("Error: No subscriptions found for customer: " + customer.getName());
                            break;
                        }
                        System.out.println("Customer Subscriptions:");
                        for (Subscription s : subs) {
                            Plan subPlan = planService.getPlan(s.getPlanId());
                            System.out.println("Subscription: ID=" + s.getId() + ", Plan=" + (subPlan != null ? subPlan.getName() : "Unknown") + ", Start Date=" + s.getStartDate());
                        }
                        System.out.print("Enter subscription ID: ");
                        subId = UUID.fromString(scanner.nextLine());
                        List<Invoice> invoices = billingService.getSubscriptionInvoices(subId);
                        System.out.println("Invoices:");
                        if (invoices.isEmpty()) {
                            System.out.println("No invoices found.");
                        } else {
                            for (Invoice i : invoices) {
                                System.out.println("Invoice: ID=" + i.getId() + ", Subscription ID=" + i.getSubscriptionId() + ", Billing Month=" + i.getBillingMonth() + ", Base Rental=" + i.getBaseRental() + ", Overage Charge=" + i.getOverageCharge() + ", Roaming Surcharge=" + i.getRoamingSurcharge() + ", Referral Discount=" + i.getReferralDiscount() + ", Fairness Surcharge=" + i.getFairnessSurcharge() + ", Tax=" + i.getTax() + ", Total=" + i.getTotal() + ", Paid=" + i.isPaid());
                            }
                        }
                        break;
                    case "3":
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (InvalidCustomerException | UnauthorizedAccessException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void handleAnalyticsManagement(String role, Customer loggedInCustomer) {
        while (true) {
            System.out.println("\n=============================");
            System.out.println("Analytics Management");
            System.out.println("=============================");
            System.out.println("1. View Customer Usage");
            System.out.println("2. Record Usage");
            if ("admin".equals(role)) {
                System.out.println("3. View Active Subscriptions Count");
                System.out.println("4. View Total Revenue");
                System.out.println("5. View Top Data Users");
                System.out.println("6. View ARPU by Plan");
                System.out.println("7. View Overage Stats by Plan");
                System.out.println("8. Detect Credit Risk Customers");
            }
            System.out.println("9. Recommend Plan");
            System.out.println("10. Back");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine();
            try {
                String phoneNumber = "admin".equals(role) ? null : loggedInCustomer.getPhoneNumber();
                switch (choice) {
                    case "1":
                        if ("admin".equals(role)) {
                            System.out.print("Enter customer phone number: ");
                            phoneNumber = scanner.nextLine();
                        }
                        Customer customer = customerRepo.findByPhoneNumber(phoneNumber);
                        if (customer == null) {
                            System.out.println("Error: Customer not found with phone number: " + phoneNumber);
                            break;
                        }
                        UsageRecord usage = analyticsService.getCustomerUsage(customer.getId());
                        System.out.println("Customer Usage: Customer=" + customer.getName() + ", Data=" + usage.getDataGb() + "GB, Voice=" + usage.getVoiceMinutes() + "min, SMS=" + usage.getSmsCount() + ", Roaming=" + usage.isRoaming() + ", International=" + usage.isInternational() + ", NightTime=" + usage.isNightTime());
                        break;
                    case "2":
                        if ("admin".equals(role)) {
                            System.out.print("Enter customer phone number: ");
                            phoneNumber = scanner.nextLine();
                        }
                        customer = customerRepo.findByPhoneNumber(phoneNumber);
                        if (customer == null) {
                            System.out.println("Error: Customer not found with phone number: " + phoneNumber);
                            break;
                        }
                        List<Subscription> subs = subscriptionService.getSubscriptionsByCustomer(phoneNumber);
                        if (subs.isEmpty()) {
                            System.out.println("Error: No subscriptions found for customer: " + customer.getName());
                            break;
                        }
                        System.out.println("Customer Subscriptions:");
                        for (Subscription s : subs) {
                            Plan subPlan = planService.getPlan(s.getPlanId());
                            System.out.println("Subscription: ID=" + s.getId() + ", Plan=" + (subPlan != null ? subPlan.getName() : "Unknown") + ", Start Date=" + s.getStartDate());
                        }
                        System.out.print("Enter subscription ID: ");
                        UUID subId = UUID.fromString(scanner.nextLine());
                        System.out.print("Enter data used (GB): ");
                        double dataGb = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter voice minutes used: ");
                        int voiceMinutes = Integer.parseInt(scanner.nextLine());
                        System.out.print("Enter SMS count: ");
                        int smsCount = Integer.parseInt(scanner.nextLine());
                        System.out.print("Is roaming (true/false): ");
                        boolean roaming = Boolean.parseBoolean(scanner.nextLine());
                        System.out.print("Is international (true/false): ");
                        boolean international = Boolean.parseBoolean(scanner.nextLine());
                        System.out.print("Is nighttime (true/false): ");
                        boolean nightTime = Boolean.parseBoolean(scanner.nextLine());
                        UsageRecord usageRecord = new UsageRecord(null, subId, dataGb, voiceMinutes, smsCount, roaming, international, nightTime, LocalDateTime.now());
                        usageRecordService.saveUsageRecord(usageRecord);
                        System.out.println("Usage recorded for Subscription ID=" + subId + ", Customer=" + customer.getName());
                        break;
                    case "3":
                        if ("admin".equals(role)) {
                            long count = analyticsService.getActiveSubscriptionsCount(role);
                            System.out.println("Active Subscriptions Count: " + count);
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "4":
                        if ("admin".equals(role)) {
                            double revenue = analyticsService.getTotalRevenue(role);
                            System.out.println("Total Revenue: $" + revenue);
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "5":
                        if ("admin".equals(role)) {
                            System.out.print("Enter number of top data users to display: ");
                            int n = Integer.parseInt(scanner.nextLine());
                            List<Invoice> invoices = invoiceRepo.findAll();
                            List<UsageRecord> usageRecords = usageRecordRepo.findAll();
                            List<Map.Entry<UUID, Double>> topDataUsers = analyticsService.topNDataUsers(role, invoices, usageRecords, n);
                            System.out.println("Top " + n + " Data Users:");
                            if (topDataUsers.isEmpty()) {
                                System.out.println("No data usage records found.");
                            } else {
                                for (Map.Entry<UUID, Double> entry : topDataUsers) {
                                    Subscription sub = subscriptionRepo.findById(entry.getKey());
                                    Customer subCustomer = sub != null ? customerRepo.findById(sub.getCustomerId()) : null;
                                    System.out.println("Subscription ID=" + entry.getKey() + ", Customer=" + (subCustomer != null ? subCustomer.getName() : "Unknown") + ", Data Used=" + entry.getValue() + "GB");
                                }
                            }
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "6":
                        if ("admin".equals(role)) {
                            List<Invoice> invoices = invoiceRepo.findAll();
                            List<Plan> plans = planService.getAllPlans();
                            List<Subscription> subscriptions = subscriptionRepo.findAll();
                            Map<String, Double> arpuByPlan = analyticsService.arpuByPlan(role, invoices, plans, subscriptions);
                            System.out.println("ARPU by Plan:");
                            if (arpuByPlan.isEmpty()) {
                                System.out.println("No ARPU data available.");
                            } else {
                                for (Map.Entry<String, Double> entry : arpuByPlan.entrySet()) {
                                    System.out.println("Plan=" + entry.getKey() + ", ARPU=$" + entry.getValue());
                                }
                            }
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "7":
                        if ("admin".equals(role)) {
                            List<Invoice> invoices = invoiceRepo.findAll();
                            List<Subscription> subscriptions = subscriptionRepo.findAll();
                            Map<UUID, DoubleSummaryStatistics> overageStats = analyticsService.getOverageStatsByPlan(role, invoices, subscriptions);
                            System.out.println("Overage Stats by Plan:");
                            if (overageStats.isEmpty()) {
                                System.out.println("No overage stats available.");
                            } else {
                                for (Map.Entry<UUID, DoubleSummaryStatistics> entry : overageStats.entrySet()) {
                                    Plan plan = planService.getPlan(entry.getKey());
                                    System.out.println("Plan=" + (plan != null ? plan.getName() : "Unknown") + ", Stats=" + entry.getValue());
                                }
                            }
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "8":
                        if ("admin".equals(role)) {
                            List<Invoice> invoices = invoiceRepo.findAll();
                            List<Subscription> subscriptions = subscriptionRepo.findAll();
                            List<Customer> customers = customerService.getAllCustomer();
                            List<String> creditRiskCustomers = analyticsService.detectCreditRisk(role, invoices, subscriptions, customers);
                            System.out.println("Credit Risk Customers:");
                            if (creditRiskCustomers.isEmpty()) {
                                System.out.println("No credit risk customers found.");
                            } else {
                                for (String customerName : creditRiskCustomers) {
                                    System.out.println("Customer=" + customerName);
                                }
                            }
                        } else {
                            System.out.println("Invalid option.");
                        }
                        break;
                    case "9":
                        System.out.print("Enter expected monthly data usage (GB): ");
                        double avgDataGb = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter expected monthly voice minutes: ");
                        double avgVoiceMinutes = Double.parseDouble(scanner.nextLine());
                        System.out.print("Enter expected monthly SMS count: ");
                        double avgSmsCount = Double.parseDouble(scanner.nextLine());
                        Plan recommendedPlan = analyticsService.recommendPlan(avgDataGb, avgVoiceMinutes, avgSmsCount);
                        if (recommendedPlan == null) {
                            System.out.println("No suitable plan found based on your usage.");
                        } else {
                            System.out.println("Recommended Plan: Name=" + recommendedPlan.getName() + ", Monthly Rental=" + recommendedPlan.getMonthlyRental() + ", Data=" + recommendedPlan.getDataAllowanceGb() + "GB, Voice=" + recommendedPlan.getVoiceAllowanceMinutes() + "min, SMS=" + recommendedPlan.getSmsAllowance());
                        }
                        break;
                    case "10":
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (InvalidCustomerException | UnauthorizedAccessException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}