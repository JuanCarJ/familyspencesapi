package com.familyspencesapi.service.category;

import com.familyspencesapi.domain.categories.BudgetPeriod;
import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.domain.categories.CategoryType;
import com.familyspencesapi.repositories.categories.CategoryRepository;
import com.familyspencesapi.utils.CategoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class CategoryService {

    private static final Logger log = LoggerFactory.getLogger(CategoryService.class);
    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}0-9 ]+$");
    private static final String CATEGORY_NOT_FOUND = "Categoría no encontrada";

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    public Category createCategory(Category category) {
        log.info(" Validando y creando categoría para familia: {}", category.getFamilyId());

        if (category.getFamilyId() == null) {
            throw new CategoryException("El ID de la familia es obligatorio para crear una categoría");
        }

        validateCategory(category);
        return categoryRepository.save(category);
    }

    public List<Category> getCategoriesForFamily(UUID familyId) {
        return categoryRepository.findByFamilyId(familyId);
    }

    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
    }

    // Filtros adaptados solo a familia
    public List<Category> getFilteredForFamily(UUID familyId, CategoryType type, BudgetPeriod period) {
        // Este método asumo que ya existe en tu Repo o lo ajustaremos
        return categoryRepository.findFilteredForFamily(familyId, type, period);
    }

    @Transactional
    public Category updateCategory(UUID id, Category updates) {
        log.info(" Actualizando categoría: {}", id);

        Category existing = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryException(CATEGORY_NOT_FOUND));
        if (updates.getFamilyId() != null && !updates.getFamilyId().equals(existing.getFamilyId())) {
            throw new CategoryException("No se puede cambiar la categoría de familia");
        }

        validateCategoryUpdate(updates, existing);

        if (updates.getName() != null) existing.setName(updates.getName());
        if (updates.getCategoryType() != null) existing.setCategoryType(updates.getCategoryType());
        if (updates.getDescription() != null) existing.setDescription(updates.getDescription());
        if (updates.getAllocatedBudget() != null) existing.setAllocatedBudget(updates.getAllocatedBudget());
        if (updates.getBudgetPeriod() != null) existing.setBudgetPeriod(updates.getBudgetPeriod());

        return categoryRepository.save(existing);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        log.info(" Eliminando categoría: {}", id);

        if (!categoryRepository.existsById(id)) {
            throw new CategoryException(CATEGORY_NOT_FOUND);
        }
        categoryRepository.deleteById(id);
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
        if (categoryRepository.existsByNameIgnoreCaseAndFamilyId(name, familyId)) {
            throw new CategoryException("Ya existe una categoría con el nombre '" + name + "' en esta familia");
        }
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new CategoryException("El nombre de la categoria es obligatorio");
        }
        if (!NAME_PATTERN.matcher(name).matches()) {
            throw new CategoryException("El nombre de la categoría contiene caracteres inválidos");
        }
        if (name.length() < 3 || name.length() > 50) {
            throw new CategoryException("El nombre debe tener entre 3 y 50 caracteres");
        }
    }

    private void validateCategoryType(CategoryType type) {
        if (type == null) throw new CategoryException("El tipo de categoría es obligatorio");
    }

    private void validateDescription(String description) {
        if (description != null && description.length() > 255) {
            throw new CategoryException("La descripción no puede exceder los 255 caracteres");
        }
    }

    private void validateBudget(BigDecimal budget) {
        if (budget == null) throw new CategoryException("El presupuesto es obligatorio");
        if (budget.compareTo(BigDecimal.ZERO) <= 0) throw new CategoryException("El presupuesto debe ser mayor a 0");
        if (budget.compareTo(BigDecimal.valueOf(100000000)) > 0) throw new CategoryException("El presupuesto excede el límite permitido");
    }

    private void validatePeriod(BudgetPeriod period) {
        if (period == null) throw new CategoryException("El periodo es obligatorio");
    }

    private void validateCategoryUpdate(Category updates, Category existing) {
        if (updates.getName() != null) {
            validateName(updates.getName());
            if (!updates.getName().equalsIgnoreCase(existing.getName())) {
                validateUniqueName(updates.getName(), existing.getFamilyId());
            }
        }
        if (updates.getDescription() != null) validateDescription(updates.getDescription());
        if (updates.getAllocatedBudget() != null) validateBudget(updates.getAllocatedBudget());
    }
}