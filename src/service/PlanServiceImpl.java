package service;

import exceptions.UnauthorizedAccessException;
import exceptions.InvalidPlanException;
import model.Plan;
import repo.PlanRepo;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlanServiceImpl implements PlanService{
    private final PlanRepo planRepo;

    public PlanServiceImpl(PlanRepo planRepo) {
        this.planRepo = planRepo;
    }

    @Override
    public Plan addPlan(Plan plan, String role) {
        checkAdminRole(role);
        validatePlan(plan, true);
        if (planRepo.findAll().stream().anyMatch(p -> p.getName().equalsIgnoreCase(plan.getName()))) {
            throw new InvalidPlanException("Plan name already exists: " + plan.getName());
        }
        return planRepo.save(plan);
    }

    @Override
    public void updatePlan(Plan plan, String role) {
        checkAdminRole(role);
        if (plan.getId() == null) {
            throw new InvalidPlanException("Cannot update plan without ID");
        }
        validatePlan(plan, false);
        if (planRepo.findById(plan.getId()) == null) {
            throw new InvalidPlanException("Plan not found for update: " + plan.getId());
        }
        if (planRepo.findAll().stream()
                .filter(p -> !p.getId().equals(plan.getId()))
                .anyMatch(p -> p.getName().equalsIgnoreCase(plan.getName()))) {
            throw new InvalidPlanException("Plan name already exists: " + plan.getName());
        }
        planRepo.save(plan);
    }

    @Override
    public void deletePlan(UUID id, String role) {
        checkAdminRole(role);
        if (id == null || planRepo.findById(id) == null) {
            throw new InvalidPlanException("Plan not found for deletion: " + id);
        }
        planRepo.delete(id);
    }

    @Override
    public Plan getPlan(UUID id) {
        if (id == null) {
            throw new InvalidPlanException("Plan ID cannot be null");
        }
        Plan plan = planRepo.findById(id);
        if (plan == null) {
            throw new InvalidPlanException("Plan not found: " + id);
        }
        return plan;
    }

    @Override
    public List<Plan> getAllPlans() {
        return planRepo.findAll();
    }

    @Override
    public List<Plan> searchPlansByName(String nameSubstring) {
        if (nameSubstring == null || nameSubstring.isBlank()) {
            return List.of();
        }
        return planRepo.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(nameSubstring.toLowerCase()))
                .toList();
    }

    private void validatePlan(Plan plan, boolean isNew) {
        Objects.requireNonNull(plan, "Plan cannot be null");
        if (plan.getName() == null || plan.getName().isBlank()) {
            throw new InvalidPlanException("Plan name is required");
        }
        if (plan.getMonthlyRental() < 0) {
            throw new InvalidPlanException("Monthly rental cannot be negative");
        }
        if (plan.getDataAllowanceGb() < 0 || plan.getVoiceAllowanceMinutes() < 0 || plan.getSmsAllowance() < 0) {
            throw new InvalidPlanException("Allowances cannot be negative");
        }
        if (plan.getOverageRateData() < 0 || plan.getOverageRateVoice() < 0 || plan.getOverageRateSms() < 0) {
            throw new InvalidPlanException("Overage rates cannot be negative");
        }
        if (plan.getFamilyShareCap() < 0) {
            throw new InvalidPlanException("Family share cap cannot be negative");
        }
        if (!isNew && plan.getId() == null) {
            throw new InvalidPlanException("Plan ID required for update");
        }
    }

    private void checkAdminRole(String role) {
        if (!"admin".equals(role)) {
            throw new UnauthorizedAccessException("Only admin can perform this operation");
        }
    }
}