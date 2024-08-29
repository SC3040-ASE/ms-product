package com.product.repository;

import com.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = "SELECT * FROM product_search(:query, :numberOfResults)", nativeQuery = true)
    List<Object[]> searchProducts(@Param("query") String searchQuery, @Param("numberOfResults") int numResults);

}
