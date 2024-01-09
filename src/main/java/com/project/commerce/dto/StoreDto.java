package com.project.commerce.dto;

import com.project.commerce.domain.Member;
import com.project.commerce.domain.Store;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreDto {

    private String storeName;

    private String storeLocation;

    private String text;

    public StoreDto(Store store) {
        this.storeName = store.getStoreName();
        this.storeLocation = store.getStoreLocation();
        this.text = store.getText();
    }

    public Store StoreDtoToEntity(Member member) {
        return Store.builder()
                .storeName(this.storeName)
                .storeLocation(this.storeLocation)
                .text(this.text)
                .member(member)
                .build();
    }


}
