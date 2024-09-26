package com.product.repository;

import com.product.entity.Tag;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("SELECT t FROM Tag t WHERE t.tagName IN :tagNames AND t.category.id = :category")
    List<Tag> findTagsByTagNamesAndCategory(@Param("tagNames") List<String> tagNames, @Param("category") Integer category);

    @Query("SELECT t FROM Tag t WHERE t.tagName = :tagName AND t.category.id = :categoryId")
    Optional<Tag> findByTagNameAndCategoryName(@Param("tagName") String tagName, @Param("categoryId") Integer categoryId);

    @Query("SELECT t FROM Tag t WHERE t.id = :tagId AND t.tagName = :tagName AND t.category.id = :categoryId")
    Optional<Tag> findTagByAllParams(@Param("tagId") Integer id, @Param("tagName") String tagName, @Param("categoryId") Integer categoryId);

    @Query("SELECT t FROM Tag t WHERE t.id = :tagId AND t.tagName = :tagName")
    Optional<Tag> findByIdAndTagName(@Param("tagId") Integer id, @Param("tagName") String tagName);

    @Query(value = "SELECT * FROM tag_search(:query, :numberOfResults)", nativeQuery = true)
    List<Object[]> searchTag(@Param("query") String searchQuery, @Param("numberOfResults") Integer numResults);
}