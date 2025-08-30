package service;

import exceptions.InvalidCustomerException;
import exceptions.UnauthorizedAccessException;
import model.Customer;
import repo.CustomerRepo;

import java.util.List;
import java.util.UUID;

public class CustomerServiceImpl implements CustomerService{
    private CustomerRepo customerRepo;

    public CustomerServiceImpl(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public Customer addCustomer(Customer customer) {
        return customerRepo.save(customer);
    }

    @Override
    public Customer getCustomer(UUID id) {

        return customerRepo.findById(id);
    }

    @Override
    public void deleteCustomer(UUID id) {
        customerRepo.delete(id);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if (customer.getId()==null){
            throw new InvalidCustomerException("Customer Id cannot be null.");
        }
        return customerRepo.save(customer);
    }

    @Override
    public List<Customer> getAllCustomer() {
        return customerRepo.findAll();
    }

    @Override
    public boolean roleChange(UUID actingAdminId,UUID id, String newRole){

        Customer actingCustomer = customerRepo.findById(actingAdminId);
        if (actingCustomer == null || !"admin".equalsIgnoreCase(actingCustomer.getRole())) {
            throw new UnauthorizedAccessException("Unauthorized: Only admins can change roles");
        }


        Customer customer;
        if(customerRepo.findById(id)==null){
            return false;
        }
        else {
            customer=customerRepo.findById(id);
        }
        if (newRole == null || newRole.isBlank()) {
            throw new IllegalArgumentException("Role cannot be null or empty");
        }
        customer.setRole(newRole);
        customerRepo.save(customer);
        return true;


    }


}
