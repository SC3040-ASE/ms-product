package com.product.repository;

import com.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {

    @Query(value = "SELECT * FROM product_search_range(:query, :startRank, :endRank)", nativeQuery = true)
    List<Object[]> searchProductsRange(@Param("query") String searchQuery, @Param("startRank") int startRank, @Param("endRank") int endRank);

    @Query(value = "SELECT * FROM PRODUCT WHERE owner_id = :ownerId", nativeQuery = true)
    List<Product> findProductsByOwnerId(@Param("ownerId") int ownerId);

    @Query(value = "SELECT * FROM PRODUCT WHERE category_id = :categoryId AND current_quantity >= 1 ORDER BY created_on ASC", nativeQuery = true)
    List<Product> findActiveProductsByCategoryId(@Param("categoryId") int categoryId);
}
