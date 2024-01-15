package com.project.commerce.controller;

import com.project.commerce.dto.StoreDto;
import com.project.commerce.service.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/store")
@RequiredArgsConstructor
@RestController
public class StoreController {

    public final StoreService storeService;

    // 매장 등록
    @PostMapping("/register")
    public ResponseEntity<?> registerStore(
            @RequestBody StoreDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        StoreDto createdStore = this.storeService.registerStore(username, request);
        log.info("매장 등록 완료");
        return ResponseEntity.ok(createdStore);
    }
    // 매장 정보 수정
    @PutMapping("/update")
    public ResponseEntity<?> updateStore(@RequestBody StoreDto request) {
        StoreDto updatedStore = this.storeService.updateStore(request);
        log.info("매장 정보 수정 완료");
        return ResponseEntity.ok(updatedStore);
    }

    // 매장 삭제
    @DeleteMapping("/delete/{name}")
    public ResponseEntity<?> deleteStore(@PathVariable String name) {
        StoreDto deleteStore = this.storeService.deleteStore(name);
        log.info("매장 정보 삭제 완료");
        return ResponseEntity.ok(deleteStore);
    }

    // 매장 조회
    @GetMapping("/read/{name}")
    public ResponseEntity<?> readStore(@PathVariable String name) {
        StoreDto readStore = this.storeService.readStore(name);
        log.info("매장 정보 조회 완료");
        return ResponseEntity.ok(readStore);
    }

    // 매장 키워드 검색
    @GetMapping("/search/{keyword}")
    public ResponseEntity<?> searchStore(@PathVariable String keyword) {
        List<String> stores = this.storeService.getStoreNameByKeyword(keyword);
        log.info("매장 검색 완료");
        return ResponseEntity.ok(stores);
    }
}
