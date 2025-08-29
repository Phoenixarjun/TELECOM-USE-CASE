package service;

import model.Customer;
import repo.CustomerRepo;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidCustomerException;
import java.time.LocalDate;
import java.util.UUID;

public class AuthServiceImpl implements AuthService {
    private final CustomerRepo customerRepo;

    public AuthServiceImpl(CustomerRepo customerRepo) {
        this.customerRepo = customerRepo;
    }

    @Override
    public Customer login(String email, String password) throws InvalidCredentialsException {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new InvalidCredentialsException("Email and password cannot be empty");
        }
        Customer customer = customerRepo.findByEmail(email);
        if (customer == null || !password.equals(customer.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }
        return customer;
    }

    @Override
    public Customer register(String name, String email, String password) throws InvalidCustomerException {
        if (name == null || name.isBlank()) {
            throw new InvalidCustomerException("Name cannot be empty");
        }
        if (email == null || email.isBlank()) {
            throw new InvalidCustomerException("Email cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidCustomerException("Password cannot be empty");
        }
        if (customerRepo.findByEmail(email) != null) {
            throw new InvalidCustomerException("Email already registered: " + email);
        }
        Customer customer = new Customer(UUID.randomUUID(), name, email, password, null, false, LocalDate.now(), "customer");
        return customerRepo.save(customer);
    }
}