package com.btob.app.domain.repository;

import com.btob.app.domain.entity.SalesOrder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, Long> {

    @EntityGraph(attributePaths = {"lineItems", "lineItems.part"})
    Optional<SalesOrder> findByOrderNumber(String orderNumber);

    @EntityGraph(attributePaths = {"lineItems", "lineItems.part"})
    Page<SalesOrder> findByAccountIdOrderByOrderDateDesc(Long accountId, Pageable pageable);

    @EntityGraph(attributePaths = {"lineItems", "lineItems.part"})
    Page<SalesOrder> findAll(Pageable pageable);
}
