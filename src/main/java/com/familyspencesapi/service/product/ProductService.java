package com.familyspencesapi.service.product;

import com.familyspencesapi.domain.product.ProductDomain;
import com.familyspencesapi.repositories.product.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service//indica que es un servicio de negocio(logica de negocio)
public class ProductService {

    private final ProductRepository productRepository;

    // se agrega contructor para que funcione en postman
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    //metodo para buscar productos por nombre
    public List<ProductDomain> searchProductsByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return productRepository.findAll();
        }
        //busca productos que contengan el nombre ignorando mayusculas y minusculas
        return productRepository.findByProductContainingIgnoreCase(name.trim());
    }
    //metodo para agregar un producto
    public ProductDomain addProduct(String productName, int price, String store) {
        ProductDomain product = new ProductDomain(productName, price, store);
        return productRepository.save(product);
    }
}