package com.familyspencesapi.controllers.product;

import com.familyspencesapi.service.product.ProductService;
import com.familyspencesapi.domain.product.ProductDomain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping()
    public List<Map<String, Object>> searchProducts(@RequestBody Map<String, String> request) {
        String name = request.get("nombre");
        List<Map<String, Object>> resultados = new ArrayList<>();

        List<ProductDomain> products = productService.searchProductsByName(name);

        for (ProductDomain product : products) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("producto", product.getProduct());
            productMap.put("precio", product.getPrice());
            productMap.put("negocio", product.getStore());
            resultados.add(productMap);
        }

        return resultados;
    }

    @PostMapping("/product")
    public Map<String, Object> addProduct(@RequestBody Map<String, Object> producto) {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            // Extraer datos del Map
            String productName = (String) producto.get("producto");
            Object priceObj = producto.get("precio");
            String store = (String) producto.get("negocio");

            // Convertir precio a int
            int price;
            if (priceObj instanceof Integer) {
                price = (Integer) priceObj;
            } else if (priceObj instanceof String) {
                price = Integer.parseInt((String) priceObj);
            } else {
                respuesta.put("mensaje", "Precio inválido");
                return respuesta;
            }

            // Usar el service para agregar producto
            ProductDomain savedProduct = productService.addProduct(productName, price, store);

            respuesta.put("mensaje", "Producto agregado exitosamente");
            respuesta.put("id", savedProduct.getId().toString());

        } catch (Exception e) {
            respuesta.put("mensaje", "Error al agregar producto");
        }

        return respuesta;
    }
}


