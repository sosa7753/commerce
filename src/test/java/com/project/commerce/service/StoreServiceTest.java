package com.project.commerce.service;

import com.project.commerce.domain.Member;
import com.project.commerce.domain.Store;
import com.project.commerce.dto.StoreDto;
import com.project.commerce.exception.CommerceException;
import com.project.commerce.repository.MemberRepository;
import com.project.commerce.repository.StoreRepository;
import com.project.commerce.type.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
class StoreServiceTest {
    @Mock
    private StoreRepository storeRepository;
    @Mock
    private MemberRepository memberRepository;

    private StoreService storeService;

    @BeforeEach
    void setup() {
        storeService = new StoreService(storeRepository, memberRepository);
    }

    @Test
    @DisplayName("매장 등록 성공")
    void success_register_storeTest() {
        //given
        Member member = new Member();
        member.setUsername("grace");

        StoreDto request = new StoreDto();
        request.setStoreName("돈카츠");
        request.setStoreLocation("경기도 하남시");
        request.setText("하남시에 위치한 식당");

        given(this.memberRepository.findByUsername(member.getUsername()))
                .willReturn(Optional.of(member));

        given(this.storeRepository.existsByStoreName(any()))
                .willReturn(false);

        //when
        StoreDto storeDto = this.storeService.registerStore(member.getUsername(), request);
        //then
        assertEquals(request, storeDto);
    }

    @Test
    @DisplayName("등록 되지 않은 소유주 명")
    void fail_member_register_storeTest() {
        //given
        Member member = new Member();
        member.setUsername("grace");

        StoreDto request = new StoreDto();
        request.setStoreName("돈카츠");
        request.setStoreLocation("경기도 하남시");
        request.setText("하남시에 위치한 식당");

        given(this.memberRepository.findByUsername(member.getUsername()))
                .willThrow(new CommerceException());

        //when
        //then
        assertThrows(CommerceException.class, () -> this.storeService.registerStore(member.getUsername(), request));
    }

    @Test
    @DisplayName("이미 등록된 매장명")
    void fail_store_register_storeTest() {

        //given
        Member member = new Member();
        member.setUsername("grace");

        StoreDto request = new StoreDto();
        request.setStoreName("돈카츠");
        request.setStoreLocation("경기도 하남시");
        request.setText("하남시에 위치한 식당");

        given(this.memberRepository.findByUsername(member.getUsername()))
                .willReturn(Optional.of(member));

        given(this.storeRepository.existsByStoreName(any()))
                .willReturn(true);

        //when
        //then
        assertThrows(CommerceException.class, () -> this.storeService.registerStore(member.getUsername(), request));
    }

    @Test
    @DisplayName("매장 정보 수정 성공")
    void success_update_storeTest() {

        //given
        Store originalStore = new Store();
        originalStore.setStoreName("돈카츠");
        originalStore.setStoreLocation("경기도 하남시");
        originalStore.setText("하남시에 위치한 식당");

        StoreDto newStore = new StoreDto();
        newStore.setStoreName("돈카츠");
        newStore.setStoreLocation("경기도 과천시");
        newStore.setText("과천시로 이전");

        given(this.storeRepository.findByStoreName(any()))
                .willReturn(Optional.of(originalStore));

        originalStore.setStoreLocation(newStore.getStoreLocation());
        originalStore.setText(newStore.getText());
        //when
        StoreDto newStoreDto = this.storeService.updateStore(newStore);
        //then
        assertEquals(newStore.getStoreName(), newStoreDto.getStoreName());
        assertEquals(newStore.getStoreLocation(), newStoreDto.getStoreLocation());
        assertEquals(newStore.getText(), newStoreDto.getText());
    }

    @Test
    @DisplayName("매장 정보 수정 실패 - 매장명 없음")
    void fail_update_storeTest() {
        //given
        StoreDto newStore = new StoreDto();
        newStore.setStoreName("라면");
        newStore.setStoreLocation("경기도 과천시");
        newStore.setText("과천시로 이전");

        given(this.storeRepository.findByStoreName(any()))
                .willThrow(new CommerceException());
        //when
        //then
        assertThrows(CommerceException.class, () -> this.storeService.updateStore(newStore));
    }

    @Test
    @DisplayName("매장 삭제 성공")
    void success_delete_storeTest() {

        //given
        StoreDto deleteStoreDto = new StoreDto();
        deleteStoreDto.setStoreName("돈카츠");

        Store deleteStore = new Store();
        deleteStore.setStoreName("돈카츠");
        deleteStore.setStoreLocation("경기도 과천시");
        deleteStore.setText("과천시로 이전");

        given(this.storeRepository.findByStoreName(any()))
                .willReturn(Optional.of(deleteStore));

        ArgumentCaptor<Store> captor = ArgumentCaptor.forClass(Store.class);

        //when
        StoreDto deletedStore = this.storeService.deleteStore(deleteStoreDto.getStoreName());

        //then
        verify(storeRepository, times(1)).delete(captor.capture());
        assertEquals(deletedStore.getStoreName(), captor.getValue().getStoreName());
        assertEquals(deletedStore.getStoreLocation(),captor.getValue().getStoreLocation());
        assertEquals(deletedStore.getText(), captor.getValue().getText());
    }

    @Test
    @DisplayName("매장 삭제 실패 - 매장명 없음")
    void fail_delete_storeTest() {

        //given
        StoreDto deleteStoreDto = new StoreDto();
        deleteStoreDto.setStoreName("라면");

        given(this.storeRepository.findByStoreName(any()))
                .willReturn(Optional.empty());

        //when
        CommerceException exception = assertThrows(CommerceException.class,
                () -> storeService.deleteStore(deleteStoreDto.getStoreName()));

        //then
        assertEquals(ErrorCode.NOT_EXIST_STORE_NAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("매장 조회 성공")
    void success_read_storeTest() {

        //given
        Store readStore = new Store();
        readStore.setStoreName("돈카츠");
        readStore.setStoreLocation("경기도 하남시");
        readStore.setText("하남시에 위치한 회사");

        StoreDto readStoreDto = new StoreDto(readStore);

        given(this.storeRepository.findByStoreName(anyString()))
                .willReturn(Optional.of(readStore));

        //when
        StoreDto Store = this.storeService.readStore(readStore.getStoreName());

        //then
        assertEquals(Store.getStoreName(), readStoreDto.getStoreName());
        assertEquals(Store.getStoreLocation(), readStoreDto.getStoreLocation());
        assertEquals(Store.getText(), readStoreDto.getText());
    }

    @Test
    @DisplayName("매장 조회 실패")
    void fail_read_storeTest() {

        //given
        Store readStore = new Store();
        readStore.setStoreName("돈카츠");
        readStore.setStoreLocation("경기도 하남시");
        readStore.setText("하남시에 위치한 회사");

        given(this.storeRepository.findByStoreName(anyString()))
                .willReturn(Optional.empty());

        CommerceException exception = assertThrows(CommerceException.class,
                () -> storeService.readStore(readStore.getStoreName()));

        //then
        assertEquals(ErrorCode.NOT_EXIST_STORE_NAME, exception.getErrorCode());
    }

    @Test
    @DisplayName("매장 검색 성공")
    void success_search_storeTest() {

        //given
        Store oneStore = new Store();
        oneStore.setStoreName("홍익 돈카츠");
        oneStore.setStoreLocation("경기도 하남시");
        oneStore.setText("하남시에 위치한 회사");

        Store twoStore = new Store();
        twoStore.setStoreName("브라운 돈카츠");
        twoStore.setStoreLocation("경기도 과천시");
        twoStore.setText("과천시에 위치한 회사");

        List<Store> storeList = new ArrayList<>();
        storeList.add(oneStore);
        storeList.add(twoStore);

        List<String> storeNames = storeList.stream()
                .map(Store::getStoreName)
                .toList();

        given(this.storeRepository.findAllByStoreNameContaining(anyString()))
                .willReturn(storeList);

        //when
        List<String> storeNameList = this.storeService.getStoreNameByKeyword("돈카츠");

        //then
        assertEquals(storeNames, storeNameList);
    }

    @Test
    @DisplayName("매장 검색 실패")
    void fail_search_storeTest() {

        //given
        List<Store> storeList = new ArrayList<>();

        given(this.storeRepository.findAllByStoreNameContaining(anyString()))
                .willReturn(storeList);

        //when
        CommerceException exception = assertThrows(CommerceException.class,
                () -> storeService.getStoreNameByKeyword("돈카츠"));

        //then
        assertEquals(ErrorCode.NOT_EXIST_STORE_BY_KEYWORD, exception.getErrorCode());
    }
}