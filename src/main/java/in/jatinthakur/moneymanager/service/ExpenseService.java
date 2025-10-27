package in.jatinthakur.moneymanager.service;

import com.sun.jdi.connect.spi.TransportService;
import in.jatinthakur.moneymanager.dto.ExpenseDTO;
import in.jatinthakur.moneymanager.entity.CategoryEntity;
import in.jatinthakur.moneymanager.entity.ExpenseEntity;
import in.jatinthakur.moneymanager.entity.ProfileEntity;
import in.jatinthakur.moneymanager.repository.CategoryRepo;
import in.jatinthakur.moneymanager.repository.ExpenseRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    private final ExpenseRepo expenseRepo;
    private final CategoryService categoryService;
    private final ProfileService profileService;
    private final CategoryRepo categoryRepo;

    public ExpenseDTO addExpense(ExpenseDTO expenseDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepo.findById(expenseDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + expenseDTO.getCategoryId()));
        ExpenseEntity expenseEntity = toEntity(expenseDTO, profile, category);
        expenseEntity = expenseRepo.save(expenseEntity);
        return toDTO(expenseEntity);


    }

    //Helper method to create expense
    private ExpenseEntity toEntity(ExpenseDTO dto , ProfileEntity profile, CategoryEntity category){
        return ExpenseEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category).build();
    }
    private ExpenseDTO toDTO(ExpenseEntity entity ){
     return ExpenseDTO.builder()
             .id(entity.getId())
             .name(entity.getName())
             .icon(entity.getIcon())
             .amount(entity.getAmount())
             .date(entity.getDate())
             .categoryId(entity.getCategory() != null ? entity.getCategory().getId() : null)
             .categoryName(entity.getCategory() != null ? entity.getCategory().getName() : "N/A")
             .createdAt(entity.getCreatedAt())
             .updatedAt(entity.getUpdatedAt())
             .build();
    }

    //Retrieve all expenses for current month/based on the start date and end date
    public List<ExpenseDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.withDayOfMonth(1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        List<ExpenseEntity> list  = expenseRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    //Delete expense by id for current user
    public void deleteExpenseById(Long expenseId){
        ProfileEntity profile = profileService.getCurrentProfile();
        ExpenseEntity expenseEntity = expenseRepo.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found with id: " + expenseId));
        if(!expenseEntity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("You are not authorized to delete this expense");
        }
        expenseRepo.deleteById(expenseId);
    }


    //Get Latest 5 expenses for current user
    public List<ExpenseDTO> getLatest5ExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //Get Total Expense Amount for current user
    public BigDecimal getTotalExpenseForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal totalExpense = expenseRepo.findTotalExpenseAmountByProfileId(profile.getId());
        return totalExpense != null ? totalExpense : BigDecimal.ZERO;
    }

    //filter expenses
    public List<ExpenseDTO> filterExpenses(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<ExpenseEntity> list = expenseRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(),
                startDate,
                endDate,
                keyword,
                sort);
        return list.stream().map(this::toDTO).toList();
    }

    //Notifications
    public List<ExpenseDTO> getExpensesForUserOnDate(Long profileId, LocalDate date) {
        List<ExpenseEntity> expenses = expenseRepo.findByProfileIdAndDate(profileId, date);
        return expenses.stream().map(this::toDTO).toList();
    }
}
