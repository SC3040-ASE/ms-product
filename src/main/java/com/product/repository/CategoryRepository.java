package com.product.repository;

import com.product.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Transactional
    @Query(value = "SELECT c FROM Category c WHERE c.categoryName = :targetCategory AND c.Id = :targetId")
    Optional<Category> findByNameAndId(@Param("targetCategory") String targetCategory, @Param("targetId") Integer targetId);

    @Transactional
    @Query(value = "SELECT c FROM Category c WHERE c.categoryName = :targetCategory")
    Optional<Category> findByName(@Param("targetCategory") String targetCategory);

    @Query(value = "SELECT * FROM category_search(:query, :numberOfResults)", nativeQuery = true)
    List<Object[]> searchCategories(@Param("query") String searchQuery, @Param("numberOfResults") int numResults);
}
