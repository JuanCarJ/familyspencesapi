package com.uco.demo.controller;

import com.uco.demo.domain.Ingreso;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/ingresos")
public class IngresoController {

    private final List<Ingreso> ingresos = new ArrayList<>();
    private Long idCounter = 1L;

    @GetMapping("/all")
    public List<Ingreso> getAllIngresos() {
        return ingresos;
    }

    @PostMapping
    public String createIngreso(@RequestBody Ingreso ingreso) {
        ingreso.setIdIngreso(idCounter++);
        ingresos.add(ingreso);
        return "Ingreso registrado correctamente con ID: " + ingreso.getIdIngreso();
    }

    @PutMapping("/{id}")
    public String updateIngreso(@PathVariable Long id, @RequestBody Ingreso updatedIngreso) {
        for (Ingreso ingreso : ingresos) {
            if (ingreso.getIdIngreso().equals(id)) {
                ingreso.setResponsable(updatedIngreso.getResponsable());
                ingreso.setTitulo(updatedIngreso.getTitulo());
                ingreso.setDescripcion(updatedIngreso.getDescripcion());
                ingreso.setPeriodo(updatedIngreso.getPeriodo());
                ingreso.setTotal(updatedIngreso.getTotal());
                ingreso.setIdFamily(updatedIngreso.getIdFamily());
                return "Ingreso actualizado correctamente";
            }
        }
        return "Ingreso con ID " + id + " no encontrado";
    }

    @DeleteMapping("/{id}")
    public String deleteIngreso(@PathVariable Long id) {
        boolean removed = ingresos.removeIf(i -> i.getIdIngreso().equals(id));
        return removed ? "Ingreso eliminado correctamente" : "Ingreso no encontrado";
    }
}
