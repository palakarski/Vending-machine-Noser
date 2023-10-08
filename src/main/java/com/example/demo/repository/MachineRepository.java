package com.example.demo.repository;

import com.example.demo.model.entity.MachineEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MachineRepository extends JpaRepository<MachineEntity, Long> {

    Optional<MachineEntity> findFirstById(Long id);

}
