package in.jatinthakur.moneymanager.service;

import in.jatinthakur.moneymanager.dto.ExpenseDTO;
import in.jatinthakur.moneymanager.dto.IncomeDTO;
import in.jatinthakur.moneymanager.dto.RecentTransactionDTO;
import in.jatinthakur.moneymanager.entity.ProfileEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Stream.concat;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;
    private final ProfileService profileService;

    public Map<String , Object> getDashboardData(){
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();
       List<RecentTransactionDTO> recentTransactionDTOS = concat(latestIncomes.stream().map(income ->
                RecentTransactionDTO.builder()
                        .id(income.getId())
                        .name(income.getName())
                        .icon(income.getIcon())
                        .amount(income.getAmount())
                        .date(income.getDate())
                        .type("income")
                        .createdAt(income.getCreatedAt())
                        .updatedAt(income.getUpdatedAt())
                        .profileId(profile.getId())
                        .build()
        ) , latestExpenses.stream().map(expense ->
                RecentTransactionDTO.builder()
                        .id(expense.getId())
                        .name(expense.getName())
                        .icon(expense.getIcon())
                        .amount(expense.getAmount())
                        .date(expense.getDate())
                        .type("expense")
                        .createdAt(expense.getCreatedAt())
                        .updatedAt(expense.getUpdatedAt())
                        .profileId(profile.getId())
                        .build()
                )).sorted((a , b) -> {
                    int cmp = b.getDate().compareTo(a.getDate());
                    if(cmp == 0 && a.getCreatedAt() != null && b.getCreatedAt() != null){
                        return b.getCreatedAt().compareTo(a.getCreatedAt());
                    }
                    return cmp;
       }).toList();
        for(RecentTransactionDTO recentTransactionDTO : recentTransactionDTOS){
            System.out.println(recentTransactionDTO);
        }
       returnValue.put("totalBalance" , incomeService.getTotalIncomeForCurrentUser()
                .subtract(expenseService.getTotalExpenseForCurrentUser()));

       returnValue.put("totalIncome" , incomeService.getTotalIncomeForCurrentUser());
       returnValue.put("totalExpense" , expenseService.getTotalExpenseForCurrentUser());
       returnValue.put("recent5Expenses", latestExpenses);
       returnValue.put("recent5Incomes", latestIncomes);
       returnValue.put("recentTransactions", recentTransactionDTOS);
       return returnValue;
    }
}
