package com.example.demo.repository;

import com.example.demo.enumeration.CoinValue;
import com.example.demo.model.entity.CoinEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<CoinEntity, Long> {

    Optional<CoinEntity> findFirstByType(CoinValue coinValue);
}
