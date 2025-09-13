package com.familyspencesapi.service.product;

import com.familyspencesapi.domain.product.ProductDomain;
import com.familyspencesapi.repositories.product.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDomain> searchProductsByName(String name) {
        if (name == null) {
            return new ArrayList<>();
        }

        return productRepository.findByProductContainingIgnoreCase(name);
    }

    public ProductDomain addProduct(String productName, int price, String store) {
        ProductDomain product = new ProductDomain(productName, price, store);
        return productRepository.save(product);
    }
}