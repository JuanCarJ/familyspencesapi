package com.familyspencesapi.controllers.product;

import com.familyspencesapi.service.product.ProductService;
import com.familyspencesapi.domain.product.ProductDomain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/product")//ruta base de los endpoints
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }//inyección de dependencia para logica de negocio

    // Constantes para evitar duplicación de literales
    private static final String MENSAJE = "mensaje";
    private static final String PRODUCTO_KEY = "producto";
    private static final String PRECIO_KEY = "precio";
    private static final String NEGOCIO_KEY = "negocio";
    private static final String ID_KEY = "id";
    // se definen constantes para evitar repetición de literales en el código

    @GetMapping()//endpoint para buscar productos
    public ResponseEntity<List<Map<String, Object>>> searchProducts(
            @RequestParam(required = false) String nombre) {

        List<Map<String, Object>> resultados = new ArrayList<>();
        List<ProductDomain> products = productService.searchProductsByName(nombre);
        //llama al servicio para buscar productos por nombre

        for (ProductDomain product : products) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put(PRODUCTO_KEY, product.getProduct());
            productMap.put(PRECIO_KEY, product.getPrice());
            productMap.put(NEGOCIO_KEY, product.getStore());
            productMap.put(ID_KEY, product.getId());
            resultados.add(productMap);
        }//convierte cada producto a un mapa para la respuesta

        return ResponseEntity.ok(resultados);
    }

    @PostMapping("/product")//endpoint para agregar productos
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody(required = false) Map<String, Object> producto) {
        Map<String, Object> respuesta = new HashMap<>();
        //mapa para la respuesta

        try {
            // Validar que el body no sea nulo
            if (producto == null || producto.isEmpty()) {
                respuesta.put(MENSAJE, "El cuerpo de la petición está vacío o es inválido");
                return ResponseEntity.badRequest().body(respuesta);
            }

            // Validar datos requeridos
            String productName = (String) producto.get(PRODUCTO_KEY);
            String store = (String) producto.get(NEGOCIO_KEY);

            if (productName == null || productName.trim().isEmpty()) {
                respuesta.put(MENSAJE, "El nombre del producto es requerido");
                return ResponseEntity.badRequest().body(respuesta);
            }

            if (store == null || store.trim().isEmpty()) {
                respuesta.put(MENSAJE, "El negocio es requerido");
                return ResponseEntity.badRequest().body(respuesta);
            }

            // Convertir precio a int
            Object priceObj = producto.get(PRECIO_KEY);
            if (priceObj == null) {
                respuesta.put(MENSAJE, "El precio es requerido");
                return ResponseEntity.badRequest().body(respuesta);
            }

            ResponseEntity<Map<String, Object>> priceValidation = validateAndConvertPrice(priceObj, respuesta);
            if (priceValidation != null) {
                return priceValidation;
            }

            int price = convertPriceToInt(priceObj);

            // Usar el service para agregar producto
            ProductDomain savedProduct = productService.addProduct(productName.trim(), price, store.trim());//se llama al servicio para agregar el producto

            respuesta.put(MENSAJE, "Producto agregado exitosamente");
            respuesta.put(ID_KEY, savedProduct.getId().toString());
            respuesta.put(PRODUCTO_KEY, savedProduct);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);//retorna 201 con el id del nuevo producto

        } catch (Exception e) {
            respuesta.put(MENSAJE, "Error interno del servidor al agregar producto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    private ResponseEntity<Map<String, Object>> validateAndConvertPrice(Object priceObj, Map<String, Object> respuesta) {
        try {
            int price = convertPriceToInt(priceObj);

            if (price < 0) {
                respuesta.put(MENSAJE, "El precio no puede ser negativo");
                return ResponseEntity.badRequest().body(respuesta);
            }

            return null; // Validación exitosa
        } catch (NumberFormatException e) {
            respuesta.put(MENSAJE, "Precio inválido: debe ser un número entero");
            return ResponseEntity.badRequest().body(respuesta);
        }
    }

    private int convertPriceToInt(Object priceObj) throws NumberFormatException {// Convierte el precio a int, manejando diferentes tipos de entrada
        return switch (priceObj) {
            case Integer integer -> integer;
            case String priceStr -> Integer.parseInt(priceStr);
            default -> throw new NumberFormatException("Tipo de precio no válido: " + priceObj.getClass().getSimpleName());
        };
    }
}