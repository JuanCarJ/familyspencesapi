package com.familyspencesapi.controller;

import com.familyspencesapi.domain.familia.Familiar;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/familia")
public class FamiliarController {

    // Simulación de una "base de datos en memoria"
    private Map<String, Familiar> familiares = new HashMap<>();

    // === Crear familiar ===
    @PostMapping
    public ResponseEntity<Map<String, Object>> agregarFamiliar(@RequestBody Familiar familiar) {
        String id = UUID.randomUUID().toString();
        familiares.put(id, familiar);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Familiar agregado con éxito");
        response.put("status", 201);
        response.put("id", id);

        return ResponseEntity.status(201).body(response);
    }

    // === Obtener familiar por id ===
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerFamiliar(@PathVariable String id) {
        Familiar familiar = familiares.get(id);
        if (familiar == null) {
            return ResponseEntity.status(404).body(Map.of("message", "Familiar no encontrado"));
        }
        return ResponseEntity.ok(familiar);
    }

    // === Editar familiar ===
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editarFamiliar(@PathVariable String id,
                                                              @RequestBody Familiar familiarActualizado) {
        if (!familiares.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Familiar no encontrado"));
        }
        familiares.put(id, familiarActualizado);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Familiar actualizado con éxito");
        response.put("status", 200);

        return ResponseEntity.ok(response);
    }

    // === Eliminar familiar ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarFamiliar(@PathVariable String id) {
        if (!familiares.containsKey(id)) {
            return ResponseEntity.status(404).body(Map.of("message", "Familiar no encontrado"));
        }
        familiares.remove(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Familiar eliminado con éxito");
        response.put("status", 200);

        return ResponseEntity.ok(response);
    }
}
