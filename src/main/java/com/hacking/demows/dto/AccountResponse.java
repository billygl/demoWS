package com.hacking.demows.dto;

public class AccountResponse {
    boolean success;
    AccountResponseAccount account;

    public AccountResponse() {

    }
    
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public AccountResponseAccount getAccount() {
        return account;
    }

    public void setAccount(AccountResponseAccount account) {
        this.account = account;
    }
}
