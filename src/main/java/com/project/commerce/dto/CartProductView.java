package com.project.commerce.dto;

import com.project.commerce.domain.CartProduct;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CartProductView {

    private String storeName;

    private String productName;

    private int purchaseAmount;

    private int purchasePrize;

    private LocalDateTime updatedAt;

    public CartProductView(CartProduct cartProduct) {
        this.storeName = cartProduct.getStoreName();
        this.productName = cartProduct.getProductName();
        this.purchaseAmount = cartProduct.getPurchaseAmount();
        this.purchasePrize = cartProduct.getPurchasePrize();
        this.updatedAt = cartProduct.getUpdatedAt();

    }
}
