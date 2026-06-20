package com.btob.app.domain.repository;

import com.btob.app.domain.entity.B2BAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface B2BAccountRepository extends JpaRepository<B2BAccount, Long> {
    Optional<B2BAccount> findByAccountNumber(String accountNumber);
}
