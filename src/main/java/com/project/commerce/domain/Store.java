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
@Entity(name = "store")
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, name = "store_name")
    private String storeName;

    @Column(name = "store_location")
    private String storeLocation;

    private String text; // 상점 설명

    @ManyToOne
    @JoinColumn(name = "member_id")
    @Builder.Default private Member member = new Member();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default private List<Product> products = new ArrayList<>();
}
