package com.product.repository;

import com.product.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    @Query(value = "SELECT t FROM Tag t WHERE t.tagName = :targetTag")
    Optional<Tag> findByName(@Param("targetTag") String targetCategory);

    @Query("SELECT t FROM Tag t WHERE t.tagName IN :tagNames")
    List<Tag> findTagsByTagNames(@Param("tagNames") List<String> tagNames);
}