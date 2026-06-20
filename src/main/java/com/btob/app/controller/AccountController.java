package com.btob.app.controller;

import com.btob.app.domain.entity.B2BAccount;
import com.btob.app.domain.repository.B2BAccountRepository;
import com.btob.app.dto.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final B2BAccountRepository accountRepository;

    public AccountController(B2BAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @GetMapping
    public ApiResponse<List<B2BAccount>> listAccounts() {
        return ApiResponse.success(accountRepository.findAll());
    }

    @GetMapping("/{accountNumber}")
    public ApiResponse<B2BAccount> getAccount(@PathVariable String accountNumber) {
        return ApiResponse.success(
                accountRepository.findByAccountNumber(accountNumber).orElse(null));
    }
}
