package com.familyspencesapi.service.product;

import com.familyspencesapi.domain.product.ProductDomain;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    private final List<ProductDomain> products = new ArrayList<>();

    public ProductService() {
        products.add(new ProductDomain("Leche Entera 1L", 3500, "Supermercado ABC"));
        products.add(new ProductDomain("Leche Deslactosada 1L", 3700, "Tienda XYZ"));
        products.add(new ProductDomain("Pan Integral 500g", 4200, "Panadería 123"));
    }

    public List<ProductDomain> searchProductsByName(String name) {
        List<ProductDomain> results = new ArrayList<>();

        if (name == null) {
            return results;
        }

        for (ProductDomain product : products) {
            if (product.getProduct().toLowerCase().contains(name.toLowerCase())) {
                results.add(product);
            }
        }

        return results;
    }

    public ProductDomain addProduct(String productName, int price, String store) {
        ProductDomain product = new ProductDomain(productName, price, store);
        products.add(product);
        return product;
    }
}