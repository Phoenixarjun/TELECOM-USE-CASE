package service;

import model.Customer;
import repo.CustomerRepo;
import exceptions.InvalidCustomerException;
import exceptions.UnauthorizedAccessException;

import java.util.List;
import java.util.UUID;

public interface CustomerService {

    Customer addCustomer(Customer customer);
    Customer getCustomer(UUID id);
    void deleteCustomer(UUID id);
    Customer updateCustomer(Customer customer);
    List<Customer> getAllCustomer();

    boolean roleChange(UUID actingAdminId,String phoneNumber,String newRole);

}