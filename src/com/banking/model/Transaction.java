package com.banking.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Transaction 
{

    private String username;
    private long accountNumber;
    private String type;
    private BigDecimal amount;
    private LocalDateTime dateTime;

    public Transaction(
            String username,
            long accountNumber,
            String type,
            BigDecimal amount,
            LocalDateTime dateTime) 
    {
        this.username = username;
        this.accountNumber = accountNumber;
        this.type = type;
        this.amount = amount;
        this.dateTime = dateTime;
    }

    public String getUsername() 
    {
        return username;
    }

    public long getAccountNumber() 
    {
        return accountNumber;
    }

    public String getType() 
    {
        return type;
    }

    public BigDecimal getAmount() 
    {
        return amount;
    }

    public LocalDateTime getDateTime() 
    {
        return dateTime;
    }

    public String getFormattedDateTime() 
    {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        return dateTime.format(formatter);
    }
}