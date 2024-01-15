package com.project.commerce.dto;

import com.project.commerce.domain.Product;
import com.project.commerce.domain.Store;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {

    private String productName;

    private Integer prize;

    private Integer amount;

    public ProductDto(Product product) {
        this.productName = product.getProductName();
        this.prize = product.getPrize();
        this.amount = product.getAmount();
    }

    public Product ProductDtoToEntity(Store store) {
        return Product.builder()
                .productName(this.productName)
                .prize(this.prize)
                .amount(this.amount)
                .store(store)
                .build();
    }
}
