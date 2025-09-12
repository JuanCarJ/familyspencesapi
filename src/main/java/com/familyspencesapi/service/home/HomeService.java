package com.familyspencesapi.service.home;

import com.familyspencesapi.domain.home.Home;
import com.familyspencesapi.repository.home.HomeRepository;
import org.springframework.stereotype.Service;

@Service
public class HomeService {

    private final HomeRepository homeRepository;
    private final BalanceGeneralService balanceGeneralService;

    public HomeService(HomeRepository homeRepository, BalanceGeneralService balanceGeneralService) {
        this.homeRepository = homeRepository;
        this.balanceGeneralService = balanceGeneralService;
    }

    public Home getHomeData(String userId) {
        Home home = homeRepository.findHomeDataByUser(userId);
        home.setBalanceGeneral(balanceGeneralService.calcularBalanceGeneral(userId));
        return home;
    }
}
