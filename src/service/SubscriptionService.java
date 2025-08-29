package service;

import model.Subscription;

import java.util.List;
import java.util.UUID;

public interface SubscriptionService {

    Subscription choosePlan(String role, UUID customerId, UUID planId);

    Subscription addSubscription(String role, Subscription subscription);
    Subscription updateSubscription(String role, Subscription subscription);
    void deleteSubscription(String role, UUID id);

    Subscription getSubscription(UUID id);
    List<Subscription> getSubscriptionsByCustomer(UUID customerId);
}
