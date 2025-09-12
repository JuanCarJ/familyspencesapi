package com.familyspencesapi.service.home;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BalanceGeneralService {

    public BigDecimal calcularBalanceGeneral(String userId) {
        // Lógica de negocio para calcular el balance
        // (por ejemplo: ingresos totales - gastos totales)
        return BigDecimal.valueOf(1200);
    }
}
