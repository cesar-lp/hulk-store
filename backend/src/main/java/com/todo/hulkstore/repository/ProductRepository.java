package com.todo.hulkstore.repository;

import com.todo.hulkstore.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByIdIn(List<Long> ids);

    @Query("SELECT p from Product p " +
            "WHERE (true = :inStock and p.stock > 0) " +
            "OR (false = :inStock and p.stock = 0)")
    List<Product> retrieveProductsByStockCondition(@Param("inStock") boolean inStock);
}
