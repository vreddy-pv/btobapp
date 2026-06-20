package com.btob.app.domain.repository;

import com.btob.app.domain.entity.AutoPart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AutoPartRepository extends JpaRepository<AutoPart, Long> {
    Optional<AutoPart> findBySku(String sku);
    Page<AutoPart> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String name, String description, Pageable pageable);
    Page<AutoPart> findByCategoryIgnoreCase(String category, Pageable pageable);
}
