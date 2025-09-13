package com.familyspencesapi.service.category;

import com.familyspencesapi.domain.categories.Category;
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

    // POST - Crear Categoria
    public Category createCategory(Category category) {
        validateCategory(category);

        return categoryRepository.save(category);
    }

    // GET ALL - Consulta todas las categorias
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    // GET BY ID - Retrieve a category by its ID
    public Category getCategoryById(UUID id) {
        return categoryRepository.findById(id).
                orElseThrow(() -> new CategoryException("Categoría no encontrada"));
    }

    // PUT - Actualiza una categoria existente
    public Category updateCategory(UUID id, Category updates) {
        Category existing = categoryRepository.findById(id).
                orElseThrow(() -> new CategoryException("Categoría no encontrada"));

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

        return categoryRepository.save(existing);
    }

    public void deleteCategory(UUID id) {
        Category existing = categoryRepository.findById(id).
                orElseThrow(() -> new CategoryException("Categoria no encntrada"));// lanza excepción si no existe

        categoryRepository.delete(existing);
    }


    private void validateCategory(Category category) {

        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new CategoryException("El nombre de la categoria es obligatorio y no puede estar vacío");
        }
        if (!NAME_PATTERN.matcher(category.getName()).matches()) {
            throw new CategoryException("El nombre de la categoría es inválido");
        }
        if (category.getName().length() < 3 || category.getName().length() > 50) {
            throw new CategoryException("El nombre de la categoría debe tener entre 3 y 50 caracteres");
        }


        if (category.getCategoryType() == null || category.getCategoryType().toString().trim().isEmpty()) {
            throw new CategoryException("El tipo de la categoría es obligatorio y no puede estar vacío");
        }
        if (category.getCategoryType() == null) {
            throw new CategoryException("El tipo de la categoría es inválido");
        }


        if (category.getDescription() != null && category.getDescription().length() > 255) {
            throw new CategoryException("La descripción de la categoría no puede exceder los 255 caracteres");
        }


        if (category.getAllocatedBudget() == null) {
            throw new CategoryException("El presupuesto destinado es obligatorio y no puede estar vacío");
        }
        if (category.getAllocatedBudget() != null && category.getAllocatedBudget().compareTo(BigDecimal.ZERO) <= 0) {
            throw new CategoryException("El presupuesto destinado no puede ser negativo o cero");
        }
        if (category.getAllocatedBudget() != null && category.getAllocatedBudget().compareTo(BigDecimal.valueOf(100000000)) > 0) {
            throw new CategoryException("El presupuesto destinado no puede exceder los 100.000.000 de pesos");
        }


        if (category.getBudgetPeriod() == null || category.getBudgetPeriod().toString().trim().isEmpty()) {
            throw new CategoryException("El periodo del presupuesto es obligatorio y no puede estar vacío");
        }
    }


    private void validateCategoryUpdate (Category updates, Category existing){

        if (updates.getName() != null) {
            if (updates.getName().trim().isEmpty()) {
                throw new CategoryException("El nombre de la categoría es obligatorio y no puede estar vacío");
            }
            if (!NAME_PATTERN.matcher(updates.getName()).matches()) {
                throw new CategoryException("El nombre de la categoría es inválido");
            }
            if (updates.getName().length() < 3 || updates.getName().length() > 50) {
                throw new CategoryException("El nombre de la categoría debe tener entre 3 y 50 caracteres");
            }
        } else if (existing.getName() == null) {
            throw new CategoryException("El nombre de la categoría es obligatorio");
        }


        if (updates.getCategoryType() != null) {
            // válido porque es un enum
        } else if (existing.getCategoryType() == null) {
            throw new CategoryException("El tipo de la categoría es obligatorio");
        }


        if (updates.getDescription() != null && updates.getDescription().length() > 255) {
            throw new CategoryException("La descripción de la categoría no puede exceder los 255 caracteres");
        }


        if (updates.getAllocatedBudget() != null) {
            if (updates.getAllocatedBudget().compareTo(BigDecimal.ZERO) <= 0) {
                throw new CategoryException("El presupuesto destinado no puede ser negativo o cero");
            }
            if (updates.getAllocatedBudget().compareTo(BigDecimal.valueOf(100000000)) > 0) {
                throw new CategoryException("El presupuesto destinado no puede superar los 100,000,000 de pesos");
            }
        } else if (existing.getAllocatedBudget() == null) {
            throw new CategoryException("El presupuesto destinado es obligatorio");
        }


        if (updates.getBudgetPeriod() != null) {
            // válido porque es un enum
        } else if (existing.getBudgetPeriod() == null) {
            throw new CategoryException("El período del presupuesto es obligatorio");
        }
    }

}



