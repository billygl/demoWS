package com.hacking.demows.controllers;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import com.hacking.demows.dao.AccountDAO;
import com.hacking.demows.dao.UserDAO;
import com.hacking.demows.dto.DepositRequest;
import com.hacking.demows.dto.TransferRequest;
import com.hacking.demows.dto.WithdrawRequest;
import com.hacking.demows.models.Account;
import com.hacking.demows.models.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("rest")
public class BankController {
    private UserDAO userDAO;
    private AccountDAO accountDAO;

    @Value( "${hacking.datasource.url}" )
    private String jdbcURL;
    @Value( "${hacking.datasource.username}" )
    private String jdbcUsername;
    @Value( "${hacking.datasource.password}" )
    private String jdbcPassword;

    private void init(){
        userDAO = new UserDAO(jdbcURL, jdbcUsername, jdbcPassword);
        accountDAO = new AccountDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }
    public User getUserFrom(String authorization){
        User user = null;
        String[] authorizationParts = authorization.split(" ");
        if(authorizationParts.length == 2){
            String encodedUserPass = authorizationParts[1];
            byte[] decodedUserPass = Base64.getDecoder().decode(encodedUserPass);
            String decodedUserPassStr = new String(decodedUserPass);
            String[] userPassParts = decodedUserPassStr.split(":");
            if(userPassParts.length == 2){
                String username = userPassParts[0];
                String pass = userPassParts[1];
                user = userDAO.validateUser(username, pass);
            }
        }
        return user;
    }

    private void throwError(HttpStatus statusCode, String message) 
        throws ResponseStatusException{
        throw new ResponseStatusException(statusCode, message);
    }

    @GetMapping("/balances")
    List<Account> getBalances(
        @RequestHeader("Authorization") String authorization
    ) {
        init();
        List<Account> list = new ArrayList<Account>();
        User user = getUserFrom(authorization);
        if(user == null){
            throwError(HttpStatus.FORBIDDEN, "Usuario o contraseña no válidos");
        }else{
            list = accountDAO.list(user);
        }
        return list;
    }
    
    @PostMapping("/withdraw/{accountNumber}")
    Account withdraw(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String accountNumber,
        @RequestBody WithdrawRequest request
    ) {
        init();
        Account result  = null;
        User user = getUserFrom(authorization);
        if(user == null){
            throwError(HttpStatus.FORBIDDEN, "Usuario o contraseña no válidos");
        }else{
            Account account = accountDAO.getAccount(user, accountNumber);
            if(account == null){
                throwError(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
            }
            double newBalance = account.getBalance() - request.getAmount();
            if(newBalance < 0){
                throwError(HttpStatus.BAD_REQUEST, "Monto solicitado mayor al saldo");
            } else{
                if(accountDAO.setBalance(account, newBalance)){
                    result = account;
                }
            }
        }

        return result;
    }
    
    @PostMapping("/deposit/{accountNumber}")
    Account deposit(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String accountNumber,
        @RequestBody DepositRequest request
    ) {
        init();
        Account result  = null;
        User user = getUserFrom(authorization);
        if(user == null){
            throwError(HttpStatus.FORBIDDEN, "Usuario o contraseña no válidos");
        }else{
            Account account = accountDAO.getAccount(user, accountNumber);
            if(account == null){
                throwError(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
            }
            double newBalance = account.getBalance() + request.getAmount();
            if(accountDAO.setBalance(account, newBalance)){
                result = account;
            }
        }

        return result;
    }
    
    @PostMapping("/transfer/{accountNumberFrom}/{accountNumberTo}")
    Account transfer(
        @RequestHeader("Authorization") String authorization,
        @PathVariable String accountNumberFrom,
        @PathVariable String accountNumberTo,
        @RequestBody TransferRequest request
    ) {
        init();
        Account result  = null;
        User user = getUserFrom(authorization);
        if(user == null){
            throwError(HttpStatus.FORBIDDEN, "Usuario o contraseña no válidos");
        }else{
            Account accountFrom = accountDAO.getAccount(user, accountNumberFrom);
            Account accountTo = accountDAO.getAccount(null, accountNumberTo);
            if(accountFrom == null){
                throwError(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
            }
            double newBalanceFrom = accountFrom.getBalance() - request.getAmount();
            double newBalanceTo = accountTo.getBalance() + request.getAmount();
            if(newBalanceFrom < 0){
                throwError(HttpStatus.BAD_REQUEST, "Monto solicitado mayor al saldo");
            } else{
                if(accountDAO.setBalance(accountFrom, newBalanceFrom) &&
                    accountDAO.setBalance(accountTo, newBalanceTo)){
                    result = accountFrom;
                }
            }
        }

        return result;
    }
}
