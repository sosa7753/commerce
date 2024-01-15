package com.project.commerce.repository;

import com.project.commerce.domain.Cart;
import com.project.commerce.domain.CartProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartProductRepository extends JpaRepository<CartProduct, Long> {

    boolean existsCartProductByCartAndStoreNameAndProductName(Cart cart, String storeName, String productName);

    Optional<CartProduct> findCartProductByCartAndStoreNameAndProductName(Cart cart, String storeName, String productName);




}
