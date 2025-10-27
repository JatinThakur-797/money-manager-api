package in.jatinthakur.moneymanager.controller;

import in.jatinthakur.moneymanager.dto.CategoryDTO;
import in.jatinthakur.moneymanager.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    @PostMapping
    public ResponseEntity<CategoryDTO> saveCategory(@RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategory = categoryService.saveCategory(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory);
    }
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories(){
       List<CategoryDTO>  categories =  categoryService.getCategoriesForCurrentProfile();
       return  ResponseEntity.status(HttpStatus.OK).body(categories);
    }
    @GetMapping("/{type}")
        public ResponseEntity<?> getCategoriesByTypeForCurrentUser(@PathVariable String type){
        List<CategoryDTO>  categories =  categoryService.getCategoriesByTypeForCurrentUser(type);
        return  ResponseEntity.status(HttpStatus.OK).body(categories);
    }
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryDTO categoryDTO){
        CategoryDTO savedCategory = categoryService.updateCategory(categoryId, categoryDTO);
        return ResponseEntity.status(HttpStatus.OK).body(savedCategory);

    }




}
