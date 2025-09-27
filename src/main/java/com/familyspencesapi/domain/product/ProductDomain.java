package com.familyspencesapi.domain.product;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "products")
public class ProductDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "product_name")
    private String product;

    @Column(name = "price")
    private int price;

    @Column(name = "store")
    private String store;

    public ProductDomain() {
        this.id = UUID.randomUUID();
    }

    public ProductDomain(String product, int price, String store) {
        this.product = product;
        this.price = price;
        this.store = store;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}