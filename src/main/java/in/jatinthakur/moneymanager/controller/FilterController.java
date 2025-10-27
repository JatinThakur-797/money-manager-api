package in.jatinthakur.moneymanager.controller;

import in.jatinthakur.moneymanager.dto.ExpenseDTO;
import in.jatinthakur.moneymanager.dto.FilterDTO;
import in.jatinthakur.moneymanager.dto.IncomeDTO;
import in.jatinthakur.moneymanager.service.ExpenseService;
import in.jatinthakur.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/filters")
public class FilterController {

    private final ExpenseService expenseService;
    private final IncomeService incomeService;

    @PostMapping
    public ResponseEntity<?> filterTransactions(@RequestBody FilterDTO filterDTO) {
        //Preparing filter parameters
        LocalDate startDate = filterDTO.getStartDate() != null ? filterDTO.getStartDate() : LocalDate.MIN;
        LocalDate endDate = filterDTO.getEndDate() != null ? filterDTO.getEndDate() : LocalDate.now();
        String keyword = filterDTO.getKeyword() != null ? filterDTO.getKeyword() : "";
        String sortField = filterDTO.getSortField() != null ? filterDTO.getSortField() : "date";
        Sort.Direction direction = "desc".equalsIgnoreCase(filterDTO.getSortOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(direction, sortField);

        if("income".equalsIgnoreCase(filterDTO.getType())){
            List<IncomeDTO> incomeDTOS = incomeService.filterIncomes(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(incomeDTOS);
        }
        else if("expense".equalsIgnoreCase(filterDTO.getType())){
            List<ExpenseDTO> expenseDTOS = expenseService.filterExpenses(startDate, endDate, keyword, sort);
            return ResponseEntity.ok(expenseDTOS);
        }
        else{
            return ResponseEntity.badRequest().body("Invalid transaction type. Must be 'income' or 'expense'.");
        }

    }
}
