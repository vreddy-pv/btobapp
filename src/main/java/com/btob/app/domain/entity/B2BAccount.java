package com.btob.app.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "b2b_accounts")
public class B2BAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Column(nullable = false)
    private String companyName;

    private String contactName;
    private String contactEmail;
    private String contactPhone;

    @Column(nullable = false)
    private BigDecimal creditLimit;

    @Column(nullable = false)
    private BigDecimal currentBalance;

    @Column(nullable = false)
    private String tier; // BRONZE, SILVER, GOLD

    protected B2BAccount() {}

    public B2BAccount(String accountNumber, String companyName, String contactName,
                      String contactEmail, String contactPhone, BigDecimal creditLimit,
                      String tier) {
        this.accountNumber = accountNumber;
        this.companyName = companyName;
        this.contactName = contactName;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
        this.creditLimit = creditLimit;
        this.currentBalance = BigDecimal.ZERO;
        this.tier = tier;
    }

    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public String getCompanyName() { return companyName; }
    public String getContactName() { return contactName; }
    public String getContactEmail() { return contactEmail; }
    public String getContactPhone() { return contactPhone; }
    public BigDecimal getCreditLimit() { return creditLimit; }
    public BigDecimal getCurrentBalance() { return currentBalance; }
    public String getTier() { return tier; }

    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }
}
