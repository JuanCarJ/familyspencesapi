package com.familyspencesapi.domain.product;

import java.util.UUID;

public class ProductDomain {
    private UUID id;
    private String product;
    private int price;
    private String store;

    public ProductDomain() {
        this.id = UUID.randomUUID();
    }

    public ProductDomain(String product, int price, String store) {
        this.id = UUID.randomUUID();
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

