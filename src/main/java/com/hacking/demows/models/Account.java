package com.hacking.demows.models;

public class Account {
    protected long id;
    protected double balance;
    protected String name;
    protected String number;
    
    public Account(long id, double balance, String name, String number) {
        this.id = id;
        this.balance = balance;
        this.name = name;
        this.number = number;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }    
}