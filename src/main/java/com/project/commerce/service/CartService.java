package com.project.commerce.service;


import com.project.commerce.domain.*;
import com.project.commerce.dto.CartProductDto;
import com.project.commerce.dto.CartProductView;
import com.project.commerce.exception.CommerceException;
import com.project.commerce.repository.CartProductRepository;
import com.project.commerce.repository.CartRepository;
import com.project.commerce.repository.ProductRepository;
import com.project.commerce.repository.StoreRepository;
import com.project.commerce.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final StoreRepository storeRepository;

    private final ProductRepository productRepository;

    private final CartProductRepository cartProductRepository;

    private final CartRepository cartRepository;

    // 상품 이름과 수량으로 장바구니에 저장.
    @Transactional
    public CartProductView addCart(String username, CartProductDto cartProductDto) {
        Cart cart = this.cartRepository.findByCartName(username)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_USER_NAME));

        Store store = this.storeRepository.findByStoreName(cartProductDto.getStoreName())
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        Product product = this.productRepository.
                findByStoreAndProductName(store, cartProductDto.getProductName())
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_PRODUCT_NAME));

        validatePurchaseAmount(cartProductDto, product);

        product.setAmount(product.getAmount() - cartProductDto.getPurchaseAmount());

        boolean exist = this.cartProductRepository.existsCartProductByCartAndStoreNameAndProductName
                (cart, cartProductDto.getStoreName(), cartProductDto.getProductName());

        CartProduct newCartProduct;
        if (exist) { // 이미 담은 상품이 있다.
            newCartProduct = this.cartProductRepository.findCartProductByCartAndStoreNameAndProductName
                            (cart, cartProductDto.getStoreName(), cartProductDto.getProductName())
                    .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_PRODUCT_NAME));

            newCartProduct.setPurchaseAmount(newCartProduct.getPurchaseAmount() + cartProductDto.getPurchaseAmount());
            newCartProduct.setPurchasePrize(
                    newCartProduct.getPurchasePrize() + cartProductDto.getPurchaseAmount() * product.getPrize());
            this.cartProductRepository.save(newCartProduct);
        } else {
            newCartProduct = cartProductDto.CartProductDtoToEntity(cart, product);
            newCartProduct.setPurchasePrize(cartProductDto.getPurchaseAmount() * product.getPrize());
            this.cartProductRepository.save(newCartProduct);
        }

        cartToMemberAssociationFieldUpdate(exist, newCartProduct);
        productToMemberAssociationFieldUpdate(exist, newCartProduct);

        log.info("Service : 장바구니 추가 완료");

        return new CartProductView(newCartProduct);
    }

    private void validatePurchaseAmount(CartProductDto cartProductDto, Product product) {
        if (cartProductDto.getPurchaseAmount() <= 0) {
            throw new CommerceException(ErrorCode.MUST_INPUT_AT_LEAST_ONE);
        }

        if (product.getAmount() <= 0) {
            throw new CommerceException(ErrorCode.PRODUCT_SOLD_OUT);
        }

        if (cartProductDto.getPurchaseAmount() > product.getAmount()) {
            throw new CommerceException((ErrorCode.MORE_THAN_NOW_PRODUCT_AMOUNT));
        }
    }

    // 장바구니에서 상품 빼기
    @Transactional
    public CartProductView updateCart(String username, CartProductDto cartProductDto) {
        Cart cart = this.cartRepository.findByCartName(username)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_USER_NAME));

        Store store = this.storeRepository.findByStoreName(cartProductDto.getStoreName())
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        Product product = this.productRepository.
                findByStoreAndProductName(store, cartProductDto.getProductName())
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_PRODUCT_NAME));

        CartProduct cartProduct = this.cartProductRepository.findCartProductByCartAndStoreNameAndProductName
                        (cart, cartProductDto.getStoreName(), cartProductDto.getProductName())
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_IN_THE_CART));

        validateCancelAmount(cartProductDto, cartProduct);

        cartProduct.setPurchaseAmount(cartProduct.getPurchaseAmount() - cartProductDto.getPurchaseAmount());
        cartProduct.setPurchasePrize(cartProduct.getPurchasePrize() -
                cartProductDto.getPurchaseAmount() * product.getPrize());

        product.setAmount(product.getAmount() + cartProductDto.getPurchaseAmount());

        if (cartProduct.getPurchaseAmount() == 0) {

            this.cartProductRepository.delete(cartProduct);

            cartToMemberAssociationFieldDelete(cartProduct);
            productToMemberAssociationFieldDelete(cartProduct);
        }

        cartToMemberAssociationFieldUpdate(true, cartProduct);
        productToMemberAssociationFieldUpdate(true, cartProduct);

        log.info("Service : 장바구니 정보 수정 완료");

        return new CartProductView(cartProduct);
    }

    private void validateCancelAmount(CartProductDto cartProductDto, CartProduct cartProduct) {
        if (cartProductDto.getPurchaseAmount() <= 0) {
            throw new CommerceException(ErrorCode.MUST_INPUT_AT_LEAST_ONE);
        }

        if (cartProductDto.getPurchaseAmount() > cartProduct.getPurchaseAmount()) {
            throw new CommerceException((ErrorCode.MORE_THAN_NOW_PRODUCT_AMOUNT));
        }
    }

    // 장바구니 비우기
    @Transactional
    public List<CartProduct> clearCart(String username) {
        Cart cart = this.cartRepository.findByCartName(username)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_USER_NAME));

        List<CartProduct> cartProducts = cart.getCartProducts();
        Iterator<CartProduct> iterator = cartProducts.iterator();

        // 상위 컬렉션 전파 수정
        while (iterator.hasNext()) {
            CartProduct cartProduct = iterator.next();
            iterator.remove();
            this.cartProductRepository.delete(cartProduct);
            cartToMemberAssociationFieldDelete(cartProduct);
            productToMemberAssociationFieldDelete(cartProduct);
        }

        if (cartProducts.isEmpty()) {
            log.info("Service : 장바구니 비움");
        }
        return cartProducts;
    }

    // 장바구니 리스트 조회
    @Transactional
    public List<CartProductView> readCart(String username) {
        Cart cart = this.cartRepository.findByCartName(username)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_USER_NAME));

        List<CartProduct> cartProducts = cart.getCartProducts();

        return cartProducts.stream()
                .map(CartProductView::new)
                .collect(Collectors.toList());
    }

    private void cartToMemberAssociationFieldUpdate(boolean exist, CartProduct cartProduct) {
        /**
         * 이미 담긴 상품이 있을 떄 : exist = true
         * Cart 방향 연관 엔티티 수정
         * 1. Cart의 관계형 필드 수정
         * 2. Member의 관계형 필드 수정
         *
         * 처음 담는 상품 일때 : exist = false
         * Cart 방향 연관 엔티티 추가
         * 1. Cart의 관계형 필드 추가
         * 2. Member의 관계형 필드 수정
         */

        Cart cart = cartProduct.getCart();
        Member member = cart.getMember();

        if(exist){
            Iterator<CartProduct> iterator = cart.getCartProducts().iterator();
            while (iterator.hasNext()) {
                CartProduct originalCartProduct = iterator.next();
                if (originalCartProduct.getId().equals(cartProduct.getId())) {
                    iterator.remove();
                    cart.getCartProducts().add(cartProduct);
                    break;
                }
            }
        }else{
            cart.getCartProducts().add(cartProduct);
        }

        member.setCart(cart);
    }

    private void cartToMemberAssociationFieldDelete(CartProduct cartProduct) {
        /**
         * Cart 방향 연관 엔티티 해당 정보 삭제
         * 1. Cart의 관계형 필드 해당 정보 삭제
         * 2. Member의 관계형 필드 해당 정보 수정
         */
        Cart cart = cartProduct.getCart();
        Member member = cart.getMember();

        cart.getCartProducts().remove(cartProduct);
        member.setCart(cart);
    }

    private void productToMemberAssociationFieldUpdate(boolean exist, CartProduct cartProduct) {
        /**
         * 이미 담긴 상품이 있을 떄 : exist = true
         * Product 방향 관계형 필드 수정
         * 1. Product의 관계형 필드 수정
         * 2. Store의 관계형 필드 수정
         * 3. Member의 관계형 필드 수정
         *
         * 처음 담는 상품일 때 : exist = false
         * Product 방향 관계형 필드 추가
         * 1. Product의 관계형 필드 추가
         * 2. Store의 관계형 필드 수정
         * 3. Member의 관계형 필드 수정
         */

        Product changeProduct = cartProduct.getProduct();

        if (exist) {
            Iterator<CartProduct> iterator = changeProduct.getCartProducts().iterator();
            while (iterator.hasNext()) {
                CartProduct originalCartProduct = iterator.next();
                if (originalCartProduct.getId().equals(cartProduct.getId())) {
                    iterator.remove();
                    changeProduct.getCartProducts().add(cartProduct);
                    break;
                }
            }
        } else {
            changeProduct.getCartProducts().add(cartProduct);
        }

        Store changeStore = changeProduct.getStore();
        Iterator<Product> productIterator = changeStore.getProducts().iterator();
        while (productIterator.hasNext()) {
            Product originalProduct = productIterator.next();
            if (originalProduct.getId().equals(changeProduct.getId())) {
                productIterator.remove();
                changeStore.getProducts().add(changeProduct);
                break;
            }
        }

        Member member = changeStore.getMember();
        Iterator<Store> storeIterator = member.getStores().iterator();
        while (storeIterator.hasNext()) {
            Store originalStore = storeIterator.next();
            if (originalStore.getId().equals(changeStore.getId())) {
                storeIterator.remove();
                member.getStores().add(changeStore);
                break;
            }
        }
    }

    private void productToMemberAssociationFieldDelete(CartProduct cartProduct) {
        /**
         * Product 방향 관계형 필드 해당 정보 삭제
         * 1. Product의 관계형 필드 해당 정보 삭제
         * 2. Store의 관계형 필드 해당 정보 수정
         * 3. Member의 관계형 필드 해당 정보 수정
         */

        Product changeProduct = cartProduct.getProduct();
        changeProduct.getCartProducts().remove(cartProduct);

        Store changeStore = changeProduct.getStore();
        Iterator<Product> productIterator = changeStore.getProducts().iterator();
        while (productIterator.hasNext()) {
            Product originalProduct = productIterator.next();
            if (originalProduct.getId().equals(changeProduct.getId())) {
                productIterator.remove();
                changeStore.getProducts().add(changeProduct);
                break;
            }
        }

        Member member = changeStore.getMember();
        Iterator<Store> storeIterator = member.getStores().iterator();
        while (storeIterator.hasNext()) {
            Store originalStore = storeIterator.next();
            if (originalStore.getId().equals(changeStore.getId())) {
                storeIterator.remove();
                member.getStores().add(changeStore);
                break;
            }
        }
    }
}
