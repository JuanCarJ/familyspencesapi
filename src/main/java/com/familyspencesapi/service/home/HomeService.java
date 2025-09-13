package com.familyspencesapi.service.home;

import com.familyspencesapi.domain.home.HomeSummary;
import com.familyspencesapi.repository.home.HomeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class HomeService {

    private final HomeRepository homeRepository;

    public HomeService(HomeRepository homeRepository) {
        this.homeRepository = homeRepository;
    }

    public HomeSummary getHomeSummary(String userId) {
        BigDecimal totalIncomes = homeRepository.getTotalIncomes(userId);
        BigDecimal totalExpenses = homeRepository.getTotalExpenses(userId);
        BigDecimal lastIncome = homeRepository.getLastIncome(userId);

        if (totalIncomes == null) totalIncomes = BigDecimal.ZERO;
        if (totalExpenses == null) totalExpenses = BigDecimal.ZERO;
        if (lastIncome == null) lastIncome = BigDecimal.ZERO;

        BigDecimal balance = totalIncomes.subtract(totalExpenses);

        return new HomeSummary(totalIncomes, totalExpenses, lastIncome, balance);
    }
}
