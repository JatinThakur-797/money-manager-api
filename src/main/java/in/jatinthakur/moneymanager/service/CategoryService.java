package in.jatinthakur.moneymanager.service;

import in.jatinthakur.moneymanager.dto.CategoryDTO;
import in.jatinthakur.moneymanager.entity.CategoryEntity;
import in.jatinthakur.moneymanager.entity.ProfileEntity;
import in.jatinthakur.moneymanager.repository.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepo categoryRepo;

    //Save Category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        if(categoryRepo.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())){
            throw new RuntimeException("Category with name "+ categoryDTO.getName() + " already exists");
        }else{
        CategoryEntity categoryEntity = toEnity(categoryDTO , profile);
        categoryEntity = categoryRepo.save(categoryEntity);
        return toDto(categoryEntity);
        }
    }

    public void deleteCategory(Long id) {
        categoryRepo.deleteById(id);
    }


//helper methods
private CategoryEntity toEnity(CategoryDTO categoryDTO , ProfileEntity profile) {
    return CategoryEntity.builder()
            .id(categoryDTO.getId())
            .profile(profile)
            .name(categoryDTO.getName())
            .icon(categoryDTO.getIcon())
            .type(categoryDTO.getType())
            .build();
}
private CategoryDTO toDto(CategoryEntity categoryEntity) {
    return CategoryDTO.builder()
            .id(categoryEntity.getId())
            .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId() : null)
            .name(categoryEntity.getName())
            .icon(categoryEntity.getIcon())
            .type(categoryEntity.getType())
            .build();
}
//Get Category for current user
public List<CategoryDTO> getCategoriesForCurrentProfile() {
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categoryEntities = categoryRepo.findByProfileId(profile.getId());
        return categoryEntities.stream().map(this::toDto).toList();
}

public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type) {
    ProfileEntity profile = profileService.getCurrentProfile();
    List<CategoryEntity> categoryEntities = categoryRepo.findByTypeAndProfileId(type, profile.getId());
    return categoryEntities.stream().map(this::toDto).toList();
}
public CategoryDTO updateCategory(Long categoryId , CategoryDTO categoryDTO) {
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepo.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category does not exist or Not accessible"));
        existingCategory.setName(categoryDTO.getName());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory.setType(categoryDTO.getType());
        existingCategory = categoryRepo.save(existingCategory);
        return toDto(existingCategory);

    }
}
