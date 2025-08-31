package service;

import model.Customer;
import repo.CustomerRepo;
import exceptions.InvalidCredentialsException;
import exceptions.InvalidCustomerException;

public interface AuthService {
    Customer login(String email, String password) throws InvalidCredentialsException;
    Customer register(String name, String email, String password, String phoneNumber) throws InvalidCustomerException;
}