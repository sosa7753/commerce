package com.project.commerce.service;

import com.project.commerce.domain.Member;
import com.project.commerce.domain.Store;
import com.project.commerce.dto.StoreDto;
import com.project.commerce.exception.CommerceException;
import com.project.commerce.repository.MemberRepository;
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
public class StoreService {

    private final StoreRepository storeRepository;

    private final MemberRepository memberRepository;

    // 매장 등록
    @Transactional
    public StoreDto registerStore(String username, StoreDto request) {
        Member member = this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_OWNER_NAME));

        String storeName = request.getStoreName();
        boolean exist = this.storeRepository.existsByStoreName(storeName);
        if(exist) {
            throw new CommerceException(ErrorCode.ALREADY_REGISTER_STORE_NAME);
        }

        Store createdStore = request.StoreDtoToEntity(member);
        this.storeRepository.save(createdStore);

        member.getStores().add(createdStore); // member 에 store 추가
        log.info("Service : 매장 등록 완료");
        return request;
    }

    // 매장 정보 수정
    @Transactional
    public StoreDto updateStore(StoreDto request) {
        Store store = this.storeRepository.findByStoreName(request.getStoreName())
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        Member member = store.getMember();

        Store updatedStore = changeStore(store, request);

        member.getStores().remove(store);
        member.getStores().add(updatedStore);
        log.info("Service : 매장 정보 수정 완료");
        return request;
    }

    private Store changeStore(Store store, StoreDto request) {
        if(request.getStoreLocation() != null) {
            store.setStoreLocation(request.getStoreLocation());
        }

        if(request.getText() !=null) {
            store.setText(request.getText());
        }
        return store;
    }

    // 매장 삭제
    @Transactional
    public StoreDto deleteStore(String name) {
        Store store = this.storeRepository.findByStoreName(name)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        Member member = store.getMember();

        StoreDto storeDto = new StoreDto(store);
        this.storeRepository.delete(store);

        member.getStores().remove(store);
        log.info("Service : 매장 정보 삭제 완료");

        return storeDto;
    }

    // 매장 조회
    public StoreDto readStore(String name) {
        Store store = this.storeRepository.findByStoreName(name)
                .orElseThrow(() -> new CommerceException(ErrorCode.NOT_EXIST_STORE_NAME));

        return new StoreDto(store);
    }

    // 매장 키워드 검색
    public List<String> getStoreNameByKeyword(String keyword) {
        List<Store> stores = this.storeRepository.findAllByStoreNameContaining(keyword);

        if (stores.isEmpty()) {
            log.info("Service : 조건에 맞는 매장 X");
            throw new CommerceException(ErrorCode.NOT_EXIST_STORE_BY_KEYWORD);
        }

        return stores.stream()
                .map(Store::getStoreName)
                .collect(Collectors.toList());
    }
}
