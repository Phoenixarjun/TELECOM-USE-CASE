package model;

import java.time.LocalDate;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String name;
    private String email;
    private UUID referredBy;
    private boolean creditBlocked;
    private LocalDate joinDate;
    private String role;

    public Customer() { }

    public Customer(UUID id, String name, String email, UUID referredBy, boolean creditBlocked, LocalDate joinDate, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.referredBy = referredBy;
        this.creditBlocked = creditBlocked;
        this.joinDate = joinDate != null ? joinDate : LocalDate.now();
        this.role = role != null ? role : "customer";
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UUID getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(UUID referredBy) {
        this.referredBy = referredBy;
    }

    public boolean isCreditBlocked() {
        return creditBlocked;
    }

    public void setCreditBlocked(boolean creditBlocked) {
        this.creditBlocked = creditBlocked;
    }

    public LocalDate getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(LocalDate joinDate) {
        this.joinDate = joinDate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", referredBy=" + referredBy +
                ", creditBlocked=" + creditBlocked +
                ", joinDate=" + joinDate +
                ", role='" + role + '\'' +
                '}';
    }
}
