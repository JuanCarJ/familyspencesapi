package com.familyspencesapi.service.users;

import com.familyspencesapi.domain.users.LoginUser;
import com.familyspencesapi.utils.LoginUserException;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class LoginUserService {
    private static final String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final Pattern emailChecker = Pattern.compile(emailRegex);

    public String authenticate(LoginUser loginRequest) {
        if (loginRequest.getEmail() == null || loginRequest.getPassword() == null ||
                loginRequest.getEmail().trim().isEmpty() || loginRequest.getPassword().trim().isEmpty()) {
            throw new LoginUserException("El correo y la contraseña no pueden estar vacíos.");
        }
        if (!emailChecker.matcher(loginRequest.getEmail()).matches()) {
            throw new LoginUserException("El formato del correo electrónico no es válido.");
        }
        return "b285701f-9955-4331-aaff-0fa3e11dee7b";
    }
}