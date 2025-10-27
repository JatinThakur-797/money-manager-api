package in.jatinthakur.moneymanager.service;


import in.jatinthakur.moneymanager.dto.ExpenseDTO;
import in.jatinthakur.moneymanager.dto.IncomeDTO;
import in.jatinthakur.moneymanager.entity.CategoryEntity;
import in.jatinthakur.moneymanager.entity.ExpenseEntity;
import in.jatinthakur.moneymanager.entity.IncomeEntity;
import in.jatinthakur.moneymanager.entity.ProfileEntity;
import in.jatinthakur.moneymanager.repository.CategoryRepo;
import in.jatinthakur.moneymanager.repository.IncomeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IncomeService {

    private final CategoryService categoryService;
    private final ProfileService profileService;
    private final CategoryRepo categoryRepo;
    private final IncomeRepo incomeRepo;

    public IncomeDTO addIncome(IncomeDTO incomeDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity category = categoryRepo.findById(incomeDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + incomeDTO.getCategoryId()));
        IncomeEntity incomeEntity = toEntity(incomeDTO, profile, category);
        incomeEntity = incomeRepo.save(incomeEntity);
            return toDTO(incomeEntity);
    }

    //Helper method to create expense
    private IncomeEntity toEntity(IncomeDTO dto , ProfileEntity profile, CategoryEntity category){
        return IncomeEntity.builder()
                .name(dto.getName())
                .icon(dto.getIcon())
                .amount(dto.getAmount())
                .date(dto.getDate())
                .profile(profile)
                .category(category).build();
    }
    private IncomeDTO toDTO(IncomeEntity entity ){
        return IncomeDTO.builder()
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

    //Retrieve all incomes for current month/based on the start date and end date
    public List<IncomeDTO> getCurrentMonthExpensesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        LocalDate currentDate = LocalDate.now();
        LocalDate startDate = currentDate.withDayOfMonth(1);
        LocalDate endDate = currentDate.withDayOfMonth(currentDate.lengthOfMonth());
        List<IncomeEntity> list  = incomeRepo.findByProfileIdAndDateBetween(profile.getId(), startDate, endDate);
        return list.stream().map(this::toDTO).toList();
    }

    //Delete income by id for current user
    public void deleteIncomeById(Long incomeId){
        ProfileEntity profile = profileService.getCurrentProfile();
        IncomeEntity incomeEntity = incomeRepo.findById(incomeId)
                .orElseThrow(() -> new RuntimeException("Income not found with id: " + incomeId));
        if(!incomeEntity.getProfile().getId().equals(profile.getId())){
            throw new RuntimeException("You are not authorized to delete this income");
        }
        incomeRepo.deleteById(incomeId);
    }

    //Get Latest 5 expenses for current user
    public List<IncomeDTO> getLatest5IncomesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepo.findTop5ByProfileIdOrderByDateDesc(profile.getId());
        return list.stream().map(this::toDTO).toList();
    }

    //Get Total Expense Amount for current user
    public BigDecimal getTotalIncomeForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        BigDecimal totalIncome = incomeRepo.findTotalIncomeAmountByProfileId(profile.getId());
        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }
    //Filter income
    public List<IncomeDTO> filterIncomes(LocalDate startDate, LocalDate endDate, String keyword, Sort sort) {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<IncomeEntity> list = incomeRepo.findByProfileIdAndDateBetweenAndNameContainingIgnoreCase(
                profile.getId(),
                startDate,
                endDate,
                keyword,
                sort);
        return list.stream().map(this::toDTO).toList();
    }
}
