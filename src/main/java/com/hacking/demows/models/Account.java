package com.hacking.demows.models;

public class Account {
    protected long id;
    protected int type;

    protected double balance;
    protected String name;
    protected String number;

    public final static int TYPE_DEBIT = 0;
    public final static int TYPE_CREDIT = 1;

    public Account(long id){
        this.id = id;
    }
    
    public Account(long id, double balance, String name, String number) {
        this.id = id;
        this.balance = balance;
        this.name = name;
        this.number = number;
    }
    
    public Account(int type, double balance, String name, String number) {
        this.type = type;
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

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}