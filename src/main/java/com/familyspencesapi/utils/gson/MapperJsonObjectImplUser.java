package com.familyspencesapi.utils.gson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MapperJsonObjectImplUser implements MapperJsonObjectUser {

    private final ObjectMapper objectMapper;

    public MapperJsonObjectImplUser() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public Optional<String> execute(Object object) {
        try {
            return Optional.ofNullable(objectMapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    @Override
    public <T> Optional<T> execute(String json, Class<T> claseDestino) {
        try {
            return Optional.ofNullable(objectMapper.readValue(json, claseDestino));
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

}
