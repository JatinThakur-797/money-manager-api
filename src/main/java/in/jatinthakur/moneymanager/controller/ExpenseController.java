package in.jatinthakur.moneymanager.controller;

import in.jatinthakur.moneymanager.dto.ExpenseDTO;
import in.jatinthakur.moneymanager.service.ExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/expenses")
public class ExpenseController {
    private final ExpenseService expenseService;
    @PostMapping
    public ResponseEntity<?> addExpense(@RequestBody ExpenseDTO dto){
        ExpenseDTO savedExpense = expenseService.addExpense(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedExpense);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseDTO>> getAllExpenses(){
        List<ExpenseDTO> expenseDTOS = expenseService.getCurrentMonthExpensesForCurrentUser();
        return ResponseEntity.ok(expenseDTOS);

    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@PathVariable Long id){
        expenseService.deleteExpenseById(id);
        return ResponseEntity.noContent().build();
    }

}
