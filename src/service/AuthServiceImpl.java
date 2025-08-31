package service;

import model.Customer;
import repo.CustomerRepo;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidCustomerException;
import java.time.LocalDate;
import java.util.UUID;
import java.util.regex.Pattern;

public class AuthServiceImpl implements AuthService {
    private final CustomerRepo customerRepo;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{10}$");

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
    public Customer register(String name, String email, String password, String phoneNumber) throws InvalidCustomerException {
        if (name == null || name.isBlank()) {
            throw new InvalidCustomerException("Name cannot be empty");
        }
        if (name.length() >= 10) {
            throw new InvalidCustomerException("Name must be less than 10 characters");
        }
        if (email == null || email.isBlank()) {
            throw new InvalidCustomerException("Email cannot be empty");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new InvalidCustomerException("Invalid email format");
        }
        if (password == null || password.isBlank()) {
            throw new InvalidCustomerException("Password cannot be empty");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new InvalidCustomerException("Password must be at least 8 characters, with 1 uppercase, 1 lowercase, 1 digit, and 1 special character");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new InvalidCustomerException("Phone number cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new InvalidCustomerException("Phone number must be exactly 10 digits");
        }
        if (customerRepo.findByEmail(email) != null) {
            throw new InvalidCustomerException("Email already registered: " + email);
        }
        if (customerRepo.findByPhoneNumber(phoneNumber) != null) {
            throw new InvalidCustomerException("Phone number already registered: " + phoneNumber);
        }
        Customer customer = new Customer(UUID.randomUUID(), name, email, password, phoneNumber, null, false, LocalDate.now(), "customer");
        return customerRepo.save(customer);
    }
}