package in.jatinthakur.moneymanager.controller;

import in.jatinthakur.moneymanager.dto.IncomeDTO;
import in.jatinthakur.moneymanager.service.IncomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/incomes")
public class IncomeController {
    private final IncomeService incomeService;
    @PostMapping
    public ResponseEntity<?> addIncome(@RequestBody IncomeDTO dto){
        IncomeDTO savedIncome = incomeService.addIncome(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedIncome);
    }

    @GetMapping
    public ResponseEntity<List<IncomeDTO>> getAllIncomes(){
        List<IncomeDTO> expenseDTOS = incomeService.getAllIncomes();
        return ResponseEntity.ok(expenseDTOS);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(@PathVariable Long id){
        incomeService.deleteIncomeById(id);
        return ResponseEntity.noContent().build();
    }

}
