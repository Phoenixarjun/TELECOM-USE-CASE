package repo;

import model.Customer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CustomerRepo {
    private final HashMap<UUID, Customer> storage = new HashMap<>();

    public CustomerRepo() {
        UUID prabhatId = UUID.randomUUID();
        storage.put(prabhatId, new Customer(prabhatId, "Prabhat", "prabhat@example.com", null, false, LocalDate.now(), "admin"));

        UUID ankitId = UUID.randomUUID();
        storage.put(ankitId, new Customer(ankitId, "Ankit Singh", "ankit@example.com", null, false, LocalDate.now(), "customer"));

        UUID nareshId = UUID.randomUUID();
        storage.put(nareshId, new Customer(nareshId, "Naresh B A", "naresh@example.com", null, false, LocalDate.now(), "customer"));

        UUID mittaId = UUID.randomUUID();
        storage.put(mittaId, new Customer(mittaId, "Mitta Jeevan Kumar", "mitta@example.com", null, false, LocalDate.now(), "customer"));

        UUID sanjivId = UUID.randomUUID();
        storage.put(sanjivId, new Customer(sanjivId, "Sanjiv T", "sanjiv@example.com", null, false, LocalDate.now(), "customer"));

        UUID sahanaId = UUID.randomUUID();
        storage.put(sahanaId, new Customer(sahanaId, "Sahana Bharadwaj", "sahana@example.com", null, false, LocalDate.now(), "customer"));

        UUID rajeshId = UUID.randomUUID();
        storage.put(rajeshId, new Customer(rajeshId, "Rajesh Kumar", "rajesh@example.com", null, false, LocalDate.now(), "customer"));

        UUID priyaId = UUID.randomUUID();
        storage.put(priyaId, new Customer(priyaId, "Priya Sharma", "priya@example.com", null, false, LocalDate.now(), "customer"));

        UUID amitId = UUID.randomUUID();
        storage.put(amitId, new Customer(amitId, "Amit Patel", "amit@example.com", null, false, LocalDate.now(), "customer"));

        UUID nehaId = UUID.randomUUID();
        storage.put(nehaId, new Customer(nehaId, "Neha Gupta", "neha@example.com", null, false, LocalDate.now(), "customer"));

        UUID vijayId = UUID.randomUUID();
        storage.put(vijayId, new Customer(vijayId, "Vijay Reddy", "vijay@example.com", null, false, LocalDate.now(), "customer"));
    }

    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID());
        }
        storage.put(customer.getId(), customer);
        return customer;
    }

    public Customer findById(UUID id) {
        return storage.get(id);
    }

    public List<Customer> findAll() {
        return new ArrayList<>(storage.values());
    }

    public void delete(UUID id) {
        storage.remove(id);
    }

    public List<Customer> findAdmins() {
        return storage.values().stream()
                .filter(c -> "admin".equals(c.getRole()))
                .collect(Collectors.toList());
    }
}