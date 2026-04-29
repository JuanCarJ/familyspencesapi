
package com.familyspencesapi.service.product;

import com.familyspencesapi.domain.product.ProductDomain;
import com.familyspencesapi.messages.users.MessageSenderBroker;
import com.familyspencesapi.repositories.product.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MessageSenderBroker messageSenderBroker;

    public ProductService(ProductRepository productRepository,
                          MessageSenderBroker messageSenderBroker) {
        this.productRepository = productRepository;
        this.messageSenderBroker = messageSenderBroker;
    }

    public void sendCreateProductToBroker(Map<String, Object> productData) {
        validateProductData(productData);
        messageSenderBroker.send(productData, "product.exchange", "product.create");
    }

    public void sendEditProductToBroker(Map<String, Object> productData) {
        validateProductDataForEdit(productData);
        messageSenderBroker.send(productData, "product.exchange", "product.edit");
    }

    public void sendDeleteProductToBroker(UUID productId) {
        if (!productRepository.existsById(productId)) {
            throw new IllegalArgumentException("No se ha encontrado el producto con el ID proporcionado");
        }
        messageSenderBroker.send(productId, "product.exchange", "product.delete");
    }

    public List<ProductDomain> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll();
        }
        return productRepository.findByProductContainingIgnoreCase(name.trim());
    }

    public ProductDomain saveProduct(Map<String, Object> productData) {
        validateProductData(productData);

        String productName = (String) productData.get("producto");
        String store = (String) productData.get("negocio");
        Object priceObj = productData.get("precio");

        int price = convertAndValidatePrice(priceObj);

        ProductDomain product = new ProductDomain(productName.trim(), price, store.trim());
        return productRepository.save(product);
    }

    // VALIDACIONES (se mantienen iguales)
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

    private void validateProductDataForEdit(Map<String, Object> productData) {
        validateProductData(productData);

        Object idObj = productData.get("id");
        if (!productRepository.existsById((UUID.fromString(idObj.toString())))) {
            throw new IllegalArgumentException("No se ha encontrado el producto con el ID proporcionado");
        } else if (idObj.toString().isEmpty()) {
            throw new IllegalArgumentException("El ID del producto es requerido para edición");
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

    private int convertPriceToInt(Object priceObj) {
        return switch (priceObj) {
            case Integer integer -> integer;
            case String priceStr -> Integer.parseInt(priceStr);
            default -> throw new NumberFormatException("Tipo de precio no válido");
        };
    }
}
