package com.familyspencesapi.service.category;

import com.familyspencesapi.domain.categories.BudgetPeriod;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.domain.categories.CategorySummary;
import com.familyspencesapi.domain.categories.CategoryType;
import com.familyspencesapi.repositories.categories.CategoryRepository;
import com.familyspencesapi.utils.CategoryException;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}0-9 ]+$");

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }


    public Category createCategory(Category category) {
        validateCategory(category);
        return categoryRepository.save(category);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getGlobalCategories() {
        return categoryRepository.findByFamilyIdIsNull();
    }

    public List<Category> getFamilyCategories(UUID familyId) {
        return categoryRepository.findByFamilyId(familyId);
    }

    public List<Category> getCategoriesForFamily(UUID familyId) {
        return categoryRepository.findGlobalAndFamilyCategories(familyId);
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Categoría no encontrada"));
    }

    public Category updateCategory(UUID id, Category updates) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Categoría no encontrada"));

        validateCategoryUpdate(updates, existing);

        if (updates.getName() != null) {
            existing.setName(updates.getName());
        }
        if (updates.getCategoryType() != null) {
            existing.setCategoryType(updates.getCategoryType());
        }
        if (updates.getDescription() != null) {
            existing.setDescription(updates.getDescription());
        }
        if (updates.getAllocatedBudget() != null) {
            existing.setAllocatedBudget(updates.getAllocatedBudget());
        }
        if (updates.getBudgetPeriod() != null) {
            existing.setBudgetPeriod(updates.getBudgetPeriod());
        }
        if (updates.getFamilyId() != null) {
            existing.setFamilyId(updates.getFamilyId());
        }

        return categoryRepository.save(existing);
    }

    public void deleteCategory(UUID id) {
        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException("Categoría no encontrada"));
        if (categoryRepository.isUsedByGoal(id)) {
            throw new CategoryException(
                    "No se puede eliminar la categoría \"" + existing.getName() +
                            "\" porque está asociada a uno o más objetivos (Goals)"
            );
        }
        categoryRepository.delete(existing);
    }

    public List<Category> getCategoriesByType(CategoryType type) {
        return categoryRepository.findByCategoryType(type);
    }

    public List<Category> getCategoriesByPeriod(BudgetPeriod period) {
        return categoryRepository.findByBudgetPeriod(period);
    }

    public List<Category> getFiltered(CategoryType type, BudgetPeriod period) {
        if (type != null && period != null) {
            return categoryRepository.findByCategoryTypeAndBudgetPeriod(type, period);
        } else if (type != null) {
            return categoryRepository.findByCategoryType(type);
        } else if (period != null) {
            return categoryRepository.findByBudgetPeriod(period);
        } else {
            return categoryRepository.findAll();
        }
    }

    public List<Category> getFilteredForFamily(UUID familyId, CategoryType type, BudgetPeriod period) {
        return categoryRepository.findFilteredForFamily(familyId, type, period);
    }

    public List<CategorySummary> getCategoryIds() {
        return categoryRepository.findAll().stream()
                .map(c -> new CategorySummary(c.getId(), c.getName()))
                .toList();
    }

    public List<CategorySummary> getCategoryIdsForFamily(UUID familyId) {
        return categoryRepository.findGlobalAndFamilyCategories(familyId).stream()
                .map(c -> new CategorySummary(c.getId(), c.getName()))
                .toList();
    }

    private void validateCategory(Category category) {
        validateName(category.getName());
        validateCategoryType(category.getCategoryType());
        validateDescription(category.getDescription());
        validateBudget(category.getAllocatedBudget());
        validatePeriod(category.getBudgetPeriod());

        validateUniqueName(category.getName(), category.getFamilyId());
    }

    private void validateUniqueName(String name, UUID familyId) {
        if (familyId == null) {
            if (categoryRepository.existsByNameIgnoreCaseAndFamilyIdIsNull(name)) {
                throw new CategoryException("Ya existe una categoría global con el nombre " + name);
            }
        } else {
            if (categoryRepository.existsByNameInContext(name, familyId)) {
                throw new CategoryException(
                        "Ya existe una categoría con el nombre " + name +
                                " (global o de esta familia)"
                );
            }
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CategoryException("El nombre de la categoria es obligatorio y no puede estar vacío");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new CategoryException("El nombre de la categoría es inválido");
        }
        if (name.length() < 3 || name.length() > 50) {
            throw new CategoryException("El nombre de la categoría debe tener entre 3 y 50 caracteres");
        }
    }

    private void validateCategoryType(CategoryType type) {
        if (type == null || type.toString().trim().isEmpty()) {
            throw new CategoryException("El tipo de la categoría es obligatorio y no puede estar vacío");
        }
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 255) {
            throw new CategoryException("La descripción de la categoría no puede exceder los 255 caracteres");
        }
    }

    private void validateBudget(BigDecimal budget) {
        if (budget == null) {
            throw new CategoryException("El presupuesto destinado es obligatorio y no puede estar vacío");
        }
        if (budget.compareTo(BigDecimal.ZERO) <= 0) {
            throw new CategoryException("El presupuesto destinado no puede ser negativo o cero");
        }
        if (budget.compareTo(BigDecimal.valueOf(100000000)) > 0) {
            throw new CategoryException("El presupuesto destinado no puede exceder los 100.000.000 de pesos");
        }
    }

    private void validatePeriod(BudgetPeriod period) {
        if (period == null || period.toString().trim().isEmpty()) {
            throw new CategoryException("El periodo del presupuesto es obligatorio y no puede estar vacío");
        }
    }

    private void validateCategoryUpdate(Category updates, Category existing) {
        validateUpdateName(updates.getName(), existing.getName());
        validateUpdateCategoryType(updates.getCategoryType(), existing.getCategoryType());
        validateUpdateDescription(updates.getDescription());
        validateUpdateBudget(updates.getAllocatedBudget(), existing.getAllocatedBudget());
        validateUpdatePeriod(updates.getBudgetPeriod(), existing.getBudgetPeriod());
    }

    private void validateUpdateName(String newName, String existingName) {
        if (newName != null) {
            validateName(newName);
        } else if (existingName == null) {
            throw new CategoryException("El nombre de la categoría es obligatorio");
        }
    }

    private void validateUpdateCategoryType(CategoryType newType, CategoryType existingType) {
        if (newType == null && existingType == null) {
            throw new CategoryException("El tipo de la categoría es obligatorio");
        }
    }

    private void validateUpdateDescription(String description) {
        validateDescription(description);
    }

    private void validateUpdateBudget(BigDecimal newBudget, BigDecimal existingBudget) {
        if (newBudget != null) {
            validateBudget(newBudget);
        } else if (existingBudget == null) {
            throw new CategoryException("El presupuesto destinado es obligatorio");
        }
    }

    private void validateUpdatePeriod(BudgetPeriod newPeriod, BudgetPeriod existingPeriod) {
        if (newPeriod == null && existingPeriod == null) {
            throw new CategoryException("El período del presupuesto es obligatorio");
        }
    }
}