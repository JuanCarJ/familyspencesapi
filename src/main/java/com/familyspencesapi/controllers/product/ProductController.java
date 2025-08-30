package com.familyspencesapi.controllers.product;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @GetMapping()//
    public List<Map<String, Object>> searchProducts(@RequestBody Map<String, String> request) {
        String name = request.get("nombre");
        List<Map<String, Object>> resultados = new ArrayList<>();

        if ("leche".equalsIgnoreCase(name)) {
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
        } else if ("pan".equalsIgnoreCase(name)) {
            Map<String, Object> producto1 = new HashMap<>();
            producto1.put("producto", "Pan Integral 500g");
            producto1.put("precio", 4200);
            producto1.put("negocio", "Panadería 123");

            resultados.add(producto1);
        }
        return resultados;
    }

    @PostMapping("/product")
    public Map<String, Object> addProduct(@RequestBody Map<String, Object> producto) {
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("mensaje", "Producto agregado exitosamente");
        respuesta.put("id", UUID.randomUUID().toString()); // ID como UUID

        return respuesta;
    }
}


