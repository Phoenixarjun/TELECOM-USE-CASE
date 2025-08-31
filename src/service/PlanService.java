package service;
import model.Plan;
import java.util.UUID;
import java.util.List;

public interface PlanService {
    Plan addPlan(Plan plan, String role);
    void updatePlan(Plan plan, String role);
    void deletePlan(UUID id, String role);
    Plan getPlan(UUID id);
    List<Plan> getAllPlans();
    List<Plan> searchPlansByName(String nameSubstring);
}