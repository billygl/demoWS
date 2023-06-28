package com.hacking.demows.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Movement {
    protected long id;

    @JsonIgnore
    protected double amount;

    @JsonIgnore
    protected String accountFrom;

    @JsonIgnore
    protected String accountTo;
    
    @JsonIgnore
    protected Date createdAt;
    
    public Movement(long id) {
        this.id = id;
    }
    
    public Movement(long id, double amount, String accountFrom, String accountTo, 
        Date createdAt) {
        this.id = id;
        this.amount = amount;
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.createdAt = createdAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}