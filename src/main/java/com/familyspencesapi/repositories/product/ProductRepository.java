package com.familyspencesapi.repositories.product;

import com.familyspencesapi.domain.product.ProductDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductDomain, UUID> {

    List<ProductDomain> findByProductContainingIgnoreCase(String product);
}
