package service;

import exceptions.InvalidCustomerException;
import exceptions.InvalidPlanException;
import exceptions.InvalidSubscriptionException;
import exceptions.UnauthorizedAccessException;
import model.Customer;
import model.Plan;
import model.Subscription;
import repo.CustomerRepo;
import repo.PlanRepo;
import repo.SubscriptionRepo;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class SubscriptionServiceImpl implements SubscriptionService{

    private final CustomerRepo customerRepo;
    private final PlanRepo planRepo;
    private final SubscriptionRepo subscriptionRepo;

    public SubscriptionServiceImpl(CustomerRepo customerRepo, PlanRepo planRepo, SubscriptionRepo subscriptionRepo) {
        this.customerRepo = customerRepo;
        this.planRepo = planRepo;
        this.subscriptionRepo = subscriptionRepo;
    }

    @Override
    public Subscription choosePlan(String role, String phoneNumber, UUID planId) {
        Customer customer = customerRepo.findByPhoneNumber(phoneNumber);
        Plan plan = planRepo.findById(planId);
        if(customer == null){
            throw new InvalidCustomerException("Customer not found with phone number: " + phoneNumber);
        }
        if(plan == null){
            throw new InvalidPlanException("Plan not found: " + planId);
        }

        Subscription subscription = new Subscription(UUID.randomUUID(), customer.getId(), plan.getId(), LocalDate.now(), null, null, false);
        return  subscriptionRepo.save(subscription);
    }

    @Override
    public Subscription addSubscription(String role, Subscription subscription) {
        if (!"admin".equals(role)) {
            throw new UnauthorizedAccessException("Only admin can add subscriptions");
        }
        if (subscription == null || subscription.getCustomerId() == null || subscription.getPlanId() == null) {
            throw new InvalidSubscriptionException("Subscription must have valid customer and plan IDs");
        }
        Customer customer = customerRepo.findById(subscription.getCustomerId());
        if (customer == null) {
            throw new InvalidCustomerException("Customer not found: " + subscription.getCustomerId());
        }
        Plan plan = planRepo.findById(subscription.getPlanId());
        if (plan == null) {
            throw new InvalidPlanException("Plan not found: " + subscription.getPlanId());
        }
        return subscriptionRepo.save(subscription);
    }

    @Override
    public Subscription updateSubscription(String role, Subscription subscription) {
        if (!"admin".equals(role)) {
            throw new UnauthorizedAccessException("Only admin can update subscriptions");
        }
        if (subscription == null || subscription.getId() == null) {
            throw new InvalidSubscriptionException("Subscription ID cannot be null");
        }
        Subscription existing = subscriptionRepo.findById(subscription.getId());
        if (existing == null) {
            throw new InvalidSubscriptionException("Subscription not found: " + subscription.getId());
        }
        Customer customer = customerRepo.findById(subscription.getCustomerId());
        if (customer == null) {
            throw new InvalidCustomerException("Customer not found: " + subscription.getCustomerId());
        }
        Plan plan = planRepo.findById(subscription.getPlanId());
        if (plan == null) {
            throw new InvalidPlanException("Plan not found: " + subscription.getPlanId());
        }
        return subscriptionRepo.save(subscription);
    }

    @Override
    public void deleteSubscription(String role, UUID id) {
        if (!"admin".equals(role)) {
            throw new UnauthorizedAccessException("Only admin can delete subscriptions");
        }
        Subscription existing = subscriptionRepo.findById(id);
        if (existing == null) {
            throw new InvalidSubscriptionException("Subscription not found: " + id);
        }
        subscriptionRepo.delete(id);
    }

    @Override
    public Subscription getSubscription(UUID id) {
        Subscription subscription = subscriptionRepo.findById(id);
        if (subscription == null) {
            throw new InvalidSubscriptionException("Subscription not found: " + id);
        }
        return subscription;
    }

    @Override
    public List<Subscription> getSubscriptionsByCustomer(String phoneNumber) {
        Customer customer = customerRepo.findByPhoneNumber(phoneNumber);
        if (customer == null) {
            throw new InvalidCustomerException("Customer not found with phone number: " + phoneNumber);
        }
        return subscriptionRepo.findByCustomerId(customer.getId());
    }
}