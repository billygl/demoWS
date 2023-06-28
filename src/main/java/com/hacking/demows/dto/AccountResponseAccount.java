package com.hacking.demows.dto;

public class AccountResponseAccount {
    protected long id;
    
    public AccountResponseAccount(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
