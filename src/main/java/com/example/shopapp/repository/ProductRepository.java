package com.example.shopapp.repository;

import com.example.shopapp.entity.Category;
import com.example.shopapp.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByActiveTrue();

    Page<Product> findAllByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
            "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :category, '%')))")
    List<Product> searchByNameAndCategory(@Param("name") String name, @Param("category") String category);

    @Query("SELECT DISTINCT p.category.name FROM Product p")
    List<String> findDistinctCategoryNames();

}
