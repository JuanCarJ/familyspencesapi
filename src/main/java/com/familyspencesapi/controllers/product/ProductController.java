package com.familyspencesapi.controllers.product;

import com.familyspencesapi.messages.users.MessageSenderBroker;
import com.familyspencesapi.service.product.ProductService;
import com.familyspencesapi.domain.product.ProductDomain;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;
    private final MessageSenderBroker messageSenderBroker;

    public ProductController(ProductService productService, MessageSenderBroker messageSenderBroker) {
        this.productService = productService;
        this.messageSenderBroker = messageSenderBroker;
    }

    private static final String MENSAJE = "mensaje";
    private static final String PRODUCTO_KEY = "producto";
    private static final String PRECIO_KEY = "precio";
    private static final String NEGOCIO_KEY = "negocio";
    private static final String ID_KEY = "id";

    @GetMapping()
    public ResponseEntity<List<Map<String, Object>>> searchProducts(
            @RequestParam(required = false) String nombre) {

        List<Map<String, Object>> resultados = new ArrayList<>();
        List<ProductDomain> products = productService.searchProductsByName(nombre);

        for (ProductDomain product : products) {
            Map<String, Object> productMap = new HashMap<>();
            productMap.put(PRODUCTO_KEY, product.getProduct());
            productMap.put(PRECIO_KEY, product.getPrice());
            productMap.put(NEGOCIO_KEY, product.getStore());
            productMap.put(ID_KEY, product.getId());
            resultados.add(productMap);
        }

        return ResponseEntity.ok(resultados);
    }

    @PostMapping("/product")
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody(required = false) Map<String, Object> producto) {
        Map<String, Object> respuesta = new HashMap<>();

        try {
            messageSenderBroker.send(producto, "product.exchange", "product.create");

            respuesta.put(MENSAJE, "Solicitud de creación enviada a processor");
            respuesta.put("producto", producto);
            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            /*ProductDomain savedProduct = productService.addProduct(producto);

            respuesta.put(MENSAJE, "Producto agregado exitosamente");
            respuesta.put(ID_KEY, savedProduct.getId().toString());
            respuesta.put(PRODUCTO_KEY, savedProduct);

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);*/

        } catch (IllegalArgumentException e) {
            respuesta.put(MENSAJE, e.getMessage());
            return ResponseEntity.badRequest().body(respuesta);
        } catch (Exception e) {
            respuesta.put(MENSAJE, "Error interno del servidor al agregar producto");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }
}