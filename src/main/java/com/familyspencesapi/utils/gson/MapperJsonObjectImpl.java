package com.familyspencesapi.utils.gson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MapperJsonObjectImpl implements MapperJsonObject {

    @Override
    public Optional<String> execute(Object object) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
<<<<<<< HEAD
            objectMapper.findAndRegisterModules();

=======
>>>>>>> 454fc1c4b2e0924f4cb490800c5f1c691077cca2
            return Optional.ofNullable(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> execute(String json, Class<T> claseDestino) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.findAndRegisterModules();  // ✅ Agregar esta línea

            return Optional.ofNullable(objectMapper.readValue(json, claseDestino));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
<<<<<<< HEAD
=======

>>>>>>> 454fc1c4b2e0924f4cb490800c5f1c691077cca2
}