package com.project.commerce.dto;


import com.project.commerce.domain.Cart;
import com.project.commerce.domain.CartProduct;
import com.project.commerce.domain.Product;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartProductDto {

    private String storeName;

    private String productName;

    private int purchaseAmount;

    public CartProduct CartProductDtoToEntity(Cart cart, Product product) {
        return CartProduct.builder()
                .storeName(this.storeName)
                .productName(this.productName)
                .purchaseAmount(this.purchaseAmount)
                .cart(cart)
                .product(product)
                .build();
    }
}
