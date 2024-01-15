package com.project.commerce.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"store_id", "product_name"}))
@Entity(name = "product")
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_name")
    private String productName;

    private int prize;

    private int amount; // 남은 갯수

    @ManyToOne
    @JoinColumn(name = "store_id", nullable = false)
    @Builder.Default private Store store = new Store();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    @Builder.Default private List<CartProduct> cartProducts = new ArrayList<>();
}
