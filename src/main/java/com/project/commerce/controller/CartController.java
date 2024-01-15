package com.project.commerce.controller;

import com.project.commerce.domain.CartProduct;
import com.project.commerce.dto.CartProductDto;
import com.project.commerce.dto.CartProductView;
import com.project.commerce.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("/cart")
@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Cart이름(user이름)과 상품 이름과 수량으로 장바구니에 저장.
    @PostMapping("/add")
    public ResponseEntity<?> addCart(
            @RequestBody CartProductDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
            String username = userDetails.getUsername();
        CartProductView newCartProductView = this.cartService.addCart(username, request);
        return ResponseEntity.ok(newCartProductView);
    }

    // 장바구니에서 상품 빼기(수정)
    @PutMapping("/update")
    public ResponseEntity<?> updateCart(
            @RequestBody CartProductDto request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        CartProductView updateCartProductView = this.cartService.updateCart(username, request);
        return ResponseEntity.ok(updateCartProductView);
    }

    // 장바구니 비우기
    @PutMapping("/clear")
    public ResponseEntity<?> deleteCart(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<CartProduct> cartProducts = this.cartService.clearCart(username);
        return ResponseEntity.ok(cartProducts);
    }

    @GetMapping("/read")
    // 장바구니 리스트 조회
    public ResponseEntity<?> readCart(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        List<CartProductView> cartProductViews = this.cartService.readCart(username);
        return ResponseEntity.ok(cartProductViews);
    }

}
