package com.example.demo.repository;

import com.example.demo.enumeration.BankNoteValue;
import com.example.demo.model.entity.BankNoteEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankNoteRepository extends JpaRepository<BankNoteEntity, Integer> {

    Optional<BankNoteEntity> findFirstByType(BankNoteValue value);
}
