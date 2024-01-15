package com.project.commerce.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
@Entity(name = "cart_product")
public class CartProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_name" , nullable = false)
    private String storeName;

    @Column(name = "product_name")
    private String productName;

    @Column(name = "purchase_amount")
    private int purchaseAmount;

    @Column(name = "purchase_prize")
    private int purchasePrize;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @Builder.Default private Cart cart = new Cart();

    @ManyToOne
    @JoinColumn(name = "product_id")
    @Builder.Default private Product product = new Product();

}
