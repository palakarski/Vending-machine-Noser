package com.example.demo.repository;

import com.example.demo.enumeration.ProductSlotType;
import com.example.demo.model.entity.ProductEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.relational.core.sql.In;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    Optional<ProductEntity> findFirstByProductSlot(ProductSlotType slotType);
}
