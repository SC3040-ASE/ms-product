package com.product.repository;

import com.product.entity.Category;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Transactional
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    @Query(value = "SELECT c FROM Category c WHERE c.categoryName = :targetCategory AND c.id = :targetId")
    Optional<Category> findByNameAndId(@Param("targetCategory") String targetCategory, @Param("targetId") Integer targetId);

    @Query(value = "SELECT c FROM Category c WHERE c.categoryName = :targetCategory")
    Optional<Category> findByName(@Param("targetCategory") String targetCategory);
}