package com.familyspencesapi.repositories.product;

import com.familyspencesapi.domain.product.ProductDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository//indica que es un repositorio de datos
public interface ProductRepository extends JpaRepository<ProductDomain, UUID> {//extiende JpaRepository para operaciones CRUD
    //metodo para buscar productos que contengan una cadena ignorando mayusculas y minusculas
    List<ProductDomain> findByProductContainingIgnoreCase(String product);
}
