package com.product.repository;

import com.product.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("SELECT t FROM Tag t WHERE t.tagName IN :tagNames AND t.category.id = :category")
    List<Tag> findTagsByTagNamesAndCategory(@Param("tagNames") List<String> tagNames, @Param("category") Integer category);

}