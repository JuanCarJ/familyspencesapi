package com.familyspencesapi.controllers.home;

import com.familyspencesapi.domain.home.Home;
import com.familyspencesapi.service.home.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/agitpi/home") //Actualizar directorio de acuerdo a como esté en el proyecto.
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping(value = "/{userId}", produces = "application/json")
    public ResponseEntity<Home> getHome(@PathVariable String userId) {
        Home home = homeService.getHomeData(userId);
        return ResponseEntity.ok(home);
    }
}

