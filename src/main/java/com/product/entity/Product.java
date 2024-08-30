package com.product.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "PRODUCT")

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "owner_id", nullable = false)
    private Integer ownerId;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "PRODUCT_TAG",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;

    @Column(name = "condition", nullable = false)
    private String condition;

    @CreationTimestamp
    @Column(name = "created_on", nullable = false, updatable = false)
    private Timestamp createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on", nullable = false)
    private Timestamp updatedOn;

    @Column(name = "product_image")
    private String productImage;

    @Column(name = "total_quantity", nullable = false)
    private Integer totalQuantity;

    @Column(name = "current_quantity", nullable = false)
    private Integer currentQuantity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
