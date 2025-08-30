package service;

import model.Customer;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    Customer addCustomer(Customer customer);
    Customer getCustomer(UUID id);
    void deleteCustomer(UUID id);
    Customer updateCustomer(Customer customer);
    List<Customer> getAllCustomer();

    boolean roleChange(UUID actingAdminId,UUID id,String newRole);

}
