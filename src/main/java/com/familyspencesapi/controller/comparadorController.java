package com.familyspencesapi.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/comparador")
public class comparadorController {

    @PostMapping("/buscar")
    public List<Map<String, Object>> buscarProductos(@RequestBody Map<String, String> request) {
        String nombre = request.get("nombre");

        List<Map<String, Object>> resultados = new ArrayList<>();

        if ("leche".equalsIgnoreCase(nombre)) {
            Map<String, Object> producto1 = new HashMap<>();
            producto1.put("producto", "Leche Entera 1L");
            producto1.put("precio", 3500);
            producto1.put("negocio", "Supermercado ABC");

            Map<String, Object> producto2 = new HashMap<>();
            producto2.put("producto", "Leche Deslactosada 1L");
            producto2.put("precio", 3700);
            producto2.put("negocio", "Tienda XYZ");

            resultados.add(producto1);
            resultados.add(producto2);
        }

        return resultados;
    }

    @PostMapping("/producto")
    public Map<String, Object> agregarProducto(@RequestBody Map<String, Object> producto) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Producto agregado exitosamente");
        respuesta.put("id", UUID.randomUUID().toString()); // ID como UUID

        return respuesta;
    }
}


