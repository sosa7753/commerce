package com.project.commerce.service;


import com.project.commerce.domain.Member;
import com.project.commerce.domain.Product;
import com.project.commerce.domain.Store;
import com.project.commerce.dto.ProductDto;
import com.project.commerce.exception.CommerceException;
import com.project.commerce.repository.ProductRepository;
import com.project.commerce.repository.StoreRepository;
import com.project.commerce.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final StoreRepository storeRepository;

    private final ProductRepository productRepository;

    // 상품 등록
    @Transactional
    public ProductDto registerProduct(String storeName, ProductDto request) {

        if(!validateProduct(request)) {
            throw new CommerceException(ErrorCode.BAD_INPUT_OF_AMOUNT_OR_PRIZE);
        }

        Store store = this.storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        boolean exist = this.productRepository.existsByStoreAndProductName(
                store, request.getProductName());
        if (exist) {
            throw new CommerceException(ErrorCode.ALREADY_REGISTER_PRODUCT_NAME);
        }

        Product createdProduct = request.ProductDtoToEntity(store);
        this.productRepository.save(createdProduct);

        store.getProducts().add(createdProduct);
        updateAssociationField(store);

        log.info("Service : 상품 등록 완료");
        return request;
    }

    private boolean validateProduct(ProductDto productDto) {
        if(productDto.getAmount() < 0) {
            log.info("수량이 0개 미만");
            return false;
        }

        if(productDto.getPrize() <= 0) {
            log.info("상품 가격이 0 이하");
            return false;
        }
        return true;
    }

    // 상품 수정
    @Transactional
    public ProductDto updateProduct(String storeName, ProductDto request) {
        Store store = this.storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        Product product = this.productRepository.findByStoreAndProductName(
                        store, request.getProductName())
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_PRODUCT_NAME));

        Product updatedProduct = changeProduct(product, request);

        store.getProducts().remove(product);
        store.getProducts().add(updatedProduct);

        updateAssociationField(store);

        log.info("Service : 상품 정보 수정 완료");
        return new ProductDto(updatedProduct);
    }

    private Product changeProduct(Product product, ProductDto request) {
        if (request.getPrize() != null && request.getPrize() > 0) {
            product.setPrize(request.getPrize());
        }

        if (request.getAmount() != null && request.getAmount() >= 0) {
            product.setAmount(request.getAmount());
        }
        return product;
    }

    // 상품 삭제
    @Transactional
    public String deleteProduct(String storeName, String productName) {
        Store store = this.storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        Product originalProduct = this.productRepository.findByStoreAndProductName(
                        store, productName)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_PRODUCT_NAME));

        List<Product> products = store.getProducts();

        this.productRepository.delete(originalProduct);

        products.remove(originalProduct);
        updateAssociationField(store);

        log.info("Service : 상품 정보 삭제 완료");

        return productName;
    }

    // 상품 조회
    public ProductDto readProduct(String storeName, String productName) {
        Store store = this.storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        Product product = this.productRepository.findByStoreAndProductName(
                        store, productName)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_PRODUCT_NAME));

        return new ProductDto(product);
    }

    // 상품 키워드 검색
    public List<String> getProductNameByKeyword(String storeName, String keyword) {
        Store store = this.storeRepository.findByStoreName(storeName)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        List<Product> products = this.productRepository.
                findAllByStoreAndProductNameContaining(store, keyword);

        if (products.isEmpty()) {
            log.info("Service : 조건에 맞는 상품 X");
            throw new CommerceException(ErrorCode.NOT_EXIST_PRODUCT_BY_KEYWORD);
        }

        return products.stream()
                .map(Product::getProductName)
                .collect(Collectors.toList());
    }

    private void updateAssociationField(Store store) {
        Member member = store.getMember();
        List<Store> stores = member.getStores();
        for(Store originStore : stores) {
            if(originStore.getId().equals(store.getId())) {
                stores.set(stores.indexOf(originStore), store);
            }
        }
    }
}
