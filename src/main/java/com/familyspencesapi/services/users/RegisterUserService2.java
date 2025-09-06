package com.familyspencesapi.services.users;

import com.familyspencesapi.domain.users.RegisterUser;
import com.familyspencesapi.repositories.users.RegisterUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RegisterUserService2 {

    private final RegisterUserRepository repository;

    public RegisterUserService2(RegisterUserRepository repository) {
        this.repository = repository;
    }


    public RegisterUser createUser(RegisterUser user) {
        if (repository.existsById(user.getId())) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        user.setId(UUID.randomUUID());
        user.setfamilyId(UUID.randomUUID());
        return repository.save(user);
    }

    public List<RegisterUser> getAllUsers() {
        return repository.findAll();
    }

    public RegisterUser getUserById(UUID id) {
        return repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("Usuario no encontrado con id " + id));
    }

    public void deleteUser(UUID id) {
        repository.deleteById(id);
    }

}
