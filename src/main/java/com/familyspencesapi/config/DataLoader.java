package com.familyspencesapi.config;

import com.familyspencesapi.domain.expense.Expense;
import com.familyspencesapi.domain.expense.Expense.ExpenseCategory;
import com.familyspencesapi.domain.family.FamilyMember;
import com.familyspencesapi.repositories.expense.ExpenseRepository;
import com.familyspencesapi.repositories.family.FamilyMemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

/**
 * Carga datos iniciales en la base de datos cuando la aplicación inicia.
 * Solo se ejecuta si las tablas están vacías.
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private FamilyMemberRepository familyMemberRepository;

    @Autowired
    private ExpenseRepository expenseRepository;

    @Override
    public void run(String... args) throws Exception {

        // Solo cargar datos si no existen
        if (familyMemberRepository.count() == 0) {
            loadInitialData();
        }
    }

    private void loadInitialData() {
        System.out.println("Cargando datos iniciales...");

        // Crear miembros de familia
        FamilyMember juan = new FamilyMember("Juan Pérez", "juan.perez@email.com", "Padre");
        FamilyMember maria = new FamilyMember("María García", "maria.garcia@email.com", "Madre");
        FamilyMember ana = new FamilyMember("Ana Pérez", "ana.perez@email.com", "Hija");
        FamilyMember carlos = new FamilyMember("Carlos Pérez", "carlos.perez@email.com", "Hijo");

        // Guardar miembros
        List<FamilyMember> members = Arrays.asList(juan, maria, ana, carlos);
        familyMemberRepository.saveAll(members);
        System.out.println("Miembros de familia creados: " + members.size());

        // Crear gastos de ejemplo
        List<Expense> sampleExpenses = Arrays.asList(
                // Gastos de Juan
                new Expense("Supermercado Éxito", "Compras mensuales del hogar", "enero",
                        juan, new BigDecimal("450.75"), ExpenseCategory.ALIMENTACION),

                new Expense("Gasolina", "Combustible para el vehículo", "enero",
                        juan, new BigDecimal("180.00"), ExpenseCategory.TRANSPORTE),

                new Expense("Seguro del carro", "Pago trimestral del seguro", "enero",
                        juan, new BigDecimal("750.00"), ExpenseCategory.TRANSPORTE),

                // Gastos de María
                new Expense("Material escolar", "Útiles para el colegio de los niños", "enero",
                        maria, new BigDecimal("120.50"), ExpenseCategory.EDUCACION),

                new Expense("Medicamentos", "Vitaminas y medicamentos familiares", "enero",
                        maria, new BigDecimal("85.30"), ExpenseCategory.SALUD),

                new Expense("Ropa de invierno", "Abrigos para la temporada", "enero",
                        maria, new BigDecimal("300.00"), ExpenseCategory.ROPA),

                // Gastos de Ana
                new Expense("Libros universitarios", "Textos para el semestre", "febrero",
                        ana, new BigDecimal("250.00"), ExpenseCategory.EDUCACION),

                new Expense("Transporte público", "Pasajes del mes", "febrero",
                        ana, new BigDecimal("45.00"), ExpenseCategory.TRANSPORTE),

                // Gastos de Carlos
                new Expense("Clases de música", "Lecciones de guitarra", "febrero",
                        carlos, new BigDecimal("80.00"), ExpenseCategory.ENTRETENIMIENTO),

                // Gastos comunes del hogar
                new Expense("Servicios públicos", "Luz, agua, gas", "febrero",
                        juan, new BigDecimal("320.00"), ExpenseCategory.SERVICIOS),

                new Expense("Internet y TV", "Plan mensual de comunicaciones", "febrero",
                        juan, new BigDecimal("95.00"), ExpenseCategory.SERVICIOS),

                new Expense("Limpieza del hogar", "Productos de aseo", "febrero",
                        maria, new BigDecimal("45.80"), ExpenseCategory.HOGAR),

                new Expense("Cena familiar", "Restaurante fin de semana", "febrero",
                        juan, new BigDecimal("150.00"), ExpenseCategory.ENTRETENIMIENTO),

                // Gastos de marzo
                new Expense("Revisión médica", "Chequeo anual de la familia", "marzo",
                        maria, new BigDecimal("400.00"), ExpenseCategory.SALUD),

                new Expense("Reparación electrodoméstico", "Arreglo de la lavadora", "marzo",
                        juan, new BigDecimal("180.00"), ExpenseCategory.HOGAR)
        );

        // Guardar gastos
        expenseRepository.saveAll(sampleExpenses);
        System.out.println("Gastos de ejemplo creados: " + sampleExpenses.size());

        // Mostrar resumen
        System.out.println("=== DATOS INICIALES CARGADOS ===");
        System.out.println("Miembros de familia: " + familyMemberRepository.count());
        System.out.println("Gastos registrados: " + expenseRepository.count());

        // Mostrar totales por categoría
        Arrays.stream(ExpenseCategory.values()).forEach(category -> {
            BigDecimal total = expenseRepository.calculateTotalByCategory(category);
            if (total.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println(category.getDisplayName() + ": $" + total);
            }
        });

        System.out.println("================================");
    }
}