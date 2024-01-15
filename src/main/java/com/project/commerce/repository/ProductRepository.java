package com.project.commerce.repository;

import com.project.commerce.domain.Product;
import com.project.commerce.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

     boolean existsByStoreAndProductName(Store store, String productName);

     Optional<Product> findByStoreAndProductName(Store store, String productName);

     List<Product> findAllByStoreAndProductNameContaining(Store store, String keyword);

}
