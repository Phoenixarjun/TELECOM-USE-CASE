package service;

import model.Customer;

public interface AuthService {
    Customer login(String email, String password);
    Customer register(String name, String email, String password);
}
