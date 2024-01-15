package com.project.commerce.controller;


import com.project.commerce.dto.ProductDto;
import com.project.commerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/product")
@RequiredArgsConstructor
@RestController
public class ProductController {

    private final ProductService productService;

    // 상품 등록
    @PostMapping("/register/{storeName}")
    public ResponseEntity<?> registerProduct(
            @PathVariable String storeName,
            @RequestBody ProductDto request) {
        ProductDto createdProduct = this.productService.registerProduct(storeName, request);
        log.info("상품 등록 완료");
        return ResponseEntity.ok(createdProduct);
    }

    // 상품 정보 수정
    @PutMapping("/update/{storeName}")
    public ResponseEntity<?> updateStore(
            @PathVariable String storeName,
            @RequestBody ProductDto request) {

        ProductDto updatedProduct = this.productService.updateProduct(storeName, request);
        log.info("상품 정보 수정 완료");
        return ResponseEntity.ok(updatedProduct);
    }

    // 상품 삭제
    @DeleteMapping("/delete/{storeName}/{productName}")
    public ResponseEntity<?> deleteStore(
            @PathVariable String storeName,
            @PathVariable String productName) {

        String deleteProductName = this.productService.deleteProduct(storeName, productName);
        log.info("상품 정보 삭제 완료");
        return ResponseEntity.ok(deleteProductName);
    }

    // 상품 조회
    @GetMapping("/read/{storeName}/{productName}")
    public ResponseEntity<?> readProduct(
            @PathVariable String storeName,
            @PathVariable String productName) {
        ProductDto readProduct = this.productService.readProduct(storeName, productName);
        log.info("상품 정보 조회 완료");
        return ResponseEntity.ok(readProduct);
    }

    // 상품 키워드 검색
    @GetMapping("/search/{storeName}/{keyword}")
    public ResponseEntity<?> searchProduct(
            @PathVariable String storeName,
            @PathVariable String keyword) {

        List<String> products = this.productService.getProductNameByKeyword(storeName,keyword);
        log.info("상품 검색 완료");
        return ResponseEntity.ok(products);
    }
}
