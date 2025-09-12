package com.familyspencesapi.repository.home;

import com.familyspencesapi.domain.home.Gasto;
import com.familyspencesapi.domain.home.Home;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class HomeRepository {

    public Home findHomeDataByUser(String userId) {
        // Datos simulados (esto luego irá a la base de datos)
        List<Gasto> gastos = List.of(
                new Gasto("Compra en supermercado", BigDecimal.valueOf(50), LocalDateTime.now().minusDays(1)),
                new Gasto("Pago de internet", BigDecimal.valueOf(30), LocalDateTime.now().minusDays(3))
        );

        BigDecimal totalGastos = gastos.stream()
                .map(Gasto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new Home(
                userId,
                BigDecimal.valueOf(1000),  // Balance general
                totalGastos,
                BigDecimal.valueOf(200),   // Último ingreso
                BigDecimal.valueOf(5000),  // Total de ingresos históricos
                gastos
        );
    }
}
