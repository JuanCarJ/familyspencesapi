package com.familyspencesapi.service.users;

import com.familyspencesapi.domain.users.LoginUser;
import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import com.familyspencesapi.service.jwt.JwtService; // ¡Importar!
import com.familyspencesapi.utils.LoginUserException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class LoginUserService {
    private static final String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern emailChecker = Pattern.compile(emailRegex);

    private final RegisterUserRepository registerUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginUserService(RegisterUserRepository registerUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.registerUserRepository = registerUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public String authenticate(LoginUser loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null ||
                loginRequest.getEmail().trim().isEmpty() || loginRequest.getPassword().trim().isEmpty()) {
            throw new LoginUserException("El correo y la contraseña no pueden estar vacíos.");
        }
        if (!emailChecker.matcher(loginRequest.getEmail()).matches()) {
            throw new LoginUserException("El formato del correo electrónico no es válido.");
        }

        Optional<RegisterUser> userOptional = registerUserRepository.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            throw new LoginUserException("Usuario no encontrado.");
        }

        RegisterUser user = userOptional.get();

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new LoginUserException("Contraseña incorrecta.");
        }

        Map<String, String> claims = new HashMap<>();
        claims.put("idFamily", user.getFamilyId().toString());
        claims.put("idUser", user.getId().toString());

        return jwtService.generateToken(claims);
    }
}