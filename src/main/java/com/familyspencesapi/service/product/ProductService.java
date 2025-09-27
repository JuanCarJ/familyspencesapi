
package com.familyspencesapi.service.product;

import com.familyspencesapi.domain.product.ProductDomain;
import com.familyspencesapi.repositories.product.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDomain> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByProductContainingIgnoreCase(name.trim());
    }

    public ProductDomain addProduct(Map<String, Object> productData) {
        validateProductData(productData);

        String productName = (String) productData.get("producto");
        String store = (String) productData.get("negocio");
        Object priceObj = productData.get("precio");

        int price = convertAndValidatePrice(priceObj);

        ProductDomain product = new ProductDomain(productName.trim(), price, store.trim());
        return productRepository.save(product);
    }

    private void validateProductData(Map<String, Object> productData) {
        if (productData == null || productData.isEmpty()) {
            throw new IllegalArgumentException("El cuerpo de la petición está vacío o es inválido");
        }

        String productName = (String) productData.get("producto");
        if (productName == null || productName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del producto es requerido");
        }

        String store = (String) productData.get("negocio");
        if (store == null || store.trim().isEmpty()) {
            throw new IllegalArgumentException("El negocio es requerido");
        }

        Object priceObj = productData.get("precio");
        if (priceObj == null) {
            throw new IllegalArgumentException("El precio es requerido");
        }
    }

    private int convertAndValidatePrice(Object priceObj) {
        try {
            int price = convertPriceToInt(priceObj);

            if (price < 0) {
                throw new IllegalArgumentException("El precio no puede ser negativo");
            }

            return price;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Precio inválido: debe ser un número entero");
        }
    }

    private int convertPriceToInt(Object priceObj) throws NumberFormatException {
        return switch (priceObj) {
            case Integer integer -> integer;
            case String priceStr -> Integer.parseInt(priceStr);
            default -> throw new NumberFormatException("Tipo de precio no válido: " + priceObj.getClass().getSimpleName());
        };
    }
}