package com.familyspencesapi.service;

import com.familyspencesapi.domain.categories.Category;
import com.familyspencesapi.utils.CategoryException;

import java.util.UUID;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[\\p{L}0-9 ]+$");

    private final List<Category> categories = new ArrayList<>();

    public CategoryService() {
        categories.add(new Category(UUID.randomUUID(), "Mercado", com.familyspencesapi.domain.categories.CategoryType.ALIMENTACION, "Gastos mensuales en compra de alimentos y productos del hogar"));
        categories.add(new Category(UUID.randomUUID(), "Pasajes", com.familyspencesapi.domain.categories.CategoryType.TRANSPORTE, "Pago de transporte público para el trabajo y los niños"));
        categories.add(new Category(UUID.randomUUID(), "Colegio de los niños", com.familyspencesapi.domain.categories.CategoryType.EDUCACION, "Pago anual de matrícula y mensualidades escolares"));
    }

    // POST - Create Category
    public Category createCategory(Category category) {
        validateCategory(category);

        category.setId(java.util.UUID.randomUUID());
        categories.add(category);

        return category;
    }

    private void validateCategory(Category category) {
        if (category.getName() == null || category.getName().trim().isEmpty()){
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
        if (category.getCategoryType() == null){
            throw new CategoryException("El tipo de la categoría es inválido");
        }


        if (category.getDescription() != null && category.getDescription().length() > 255) {
            throw new CategoryException("La descripción de la categoría no puede exceder los 255 caracteres");
        }
    }

}

