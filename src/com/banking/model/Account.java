package com.banking.model;

import java.math.BigDecimal;

public class Account 
{
    private String username;
    private long accountNumber;
    private String holderName;
    private String pin;
    private BigDecimal balance;

    public Account(
            String username,
            long accountNumber,
            String holderName,
            String pin,
            BigDecimal balance) 
    {
        this.username = username;
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.pin = pin;
        this.balance = balance;
    }

    public String getUsername() 
    {
        return username;
    }

    public long getAccountNumber() 
    {
        return accountNumber;
    }

    public String getHolderName() 
    {
        return holderName;
    }

    public String getPin() 
    {
        return pin;
    }

    public BigDecimal getBalance() 
    {
        return balance;
    }
}