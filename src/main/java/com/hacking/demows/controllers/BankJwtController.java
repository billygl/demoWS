package com.hacking.demows.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hacking.demows.dto.TransferResponse;
import com.hacking.demows.adapters.ITextAdapter;
import com.hacking.demows.components.JwtTokenUtil;
import com.hacking.demows.dao.AccountDAO;
import com.hacking.demows.dao.UserDAO;
import com.hacking.demows.dto.AccountResponse;
import com.hacking.demows.dto.AccountResponseAccount;
import com.hacking.demows.dto.CreditRequest;
import com.hacking.demows.dto.DepositRequest;
import com.hacking.demows.dto.JwtRequest;
import com.hacking.demows.dto.JwtResponse;
import com.hacking.demows.dto.LogoutResponse;
import com.hacking.demows.dto.TransferRequest;
import com.hacking.demows.dto.WithdrawRequest;
import com.hacking.demows.models.Account;
import com.hacking.demows.models.User;
import com.hacking.demows.models.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@RequestMapping("restjwt")
public class BankJwtController {
    private UserDAO userDAO;
    private AccountDAO accountDAO;
    
    @Autowired
	private AuthenticationManager authenticationManager;
    
    @Autowired
	private JwtTokenUtil jwtTokenUtil;
    
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

    private User getUserByRequest(String userpass){
        if(userpass == null){
            return null;
        }
        byte[] decodedUserPass = java.util.Base64.getDecoder().decode(userpass);
        String decodedUserPassStr = new String(decodedUserPass);
        String[] userPassParts = decodedUserPassStr.split(":");
        if(userPassParts.length == 2){
            String username = userPassParts[0];
            String pass = userPassParts[1];
            return new User(username, pass, null);
        }
        return null;
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(
        @RequestBody JwtRequest authenticationRequest) throws Exception {
        init();
        User user = getUserByRequest(authenticationRequest.getUserpass());
        if(user != null){
            authenticate(user.getUser(), user.getPass());
        }else{
            throw new Exception("INVALID_CREDENTIALS", 
            new BadCredentialsException("error"));
        }
		user = userDAO.validateUser(user.getUser(), user.getPass());

		final String token = jwtTokenUtil.generateToken(user);
        userDAO.addToken(token, user);

		return ResponseEntity.ok(new JwtResponse(token));
	}

    private User getUser(){
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userDAO.getUser(username);
    }

    @GetMapping("/balances")
    List<Account> getBalances() {
        init();
        List<Account> list = new ArrayList<Account>();
        list = accountDAO.list(getUser());
        return list;
    }
    
    @GetMapping("/pdf")
    Map<String, String> getPdf() {
        init();
        Map<String, String> result = new HashMap<String, String>();
        result.put("pdf", ITextAdapter.getPDF(null));
        return result;
    }

    @PostMapping("/withdraw/{accountNumber}")
    TransferResponse withdraw(
        @PathVariable String accountNumber,
        @RequestBody WithdrawRequest request
    ) {
        init();
        TransferResponse result  = new TransferResponse();
        Account account = accountDAO.getAccount(null, accountNumber);
        if(account == null){
            throwError(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
        }
        double newBalance = account.getBalance() - request.getAmount();
        if(newBalance < 0){
            throwError(HttpStatus.BAD_REQUEST, "Monto solicitado mayor al saldo");
        } else{
            if(accountDAO.setBalance(account, newBalance)){
                result.setSuccess(true);
                result.setMovement(
                    accountDAO.transfer(account, null, request.getAmount())
                );
            }
        }
        return result;
    }
    
    @PostMapping("/deposit/{accountNumber}")
    TransferResponse deposit(
        @PathVariable String accountNumber,
        @RequestBody DepositRequest request
    ) {
        init();
        TransferResponse result  = new TransferResponse();   
        Account account = accountDAO.getAccount(getUser(), accountNumber);
        if(account == null){
            throwError(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
        }
        double newBalance = account.getBalance() + request.getAmount();
        if(accountDAO.setBalance(account, newBalance)){
            result.setSuccess(true);
            result.setMovement(
                accountDAO.transfer(null, account, request.getAmount())
            );
        }
        return result;
    }

    @PostMapping("/transfer/{accountNumberFrom}/{accountNumberTo}")
    TransferResponse transfer(
        @PathVariable String accountNumberFrom,
        @PathVariable String accountNumberTo,
        @RequestBody TransferRequest request
    ) {
        init();
        TransferResponse result  = new TransferResponse();
        User user = getUser();
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
                result.setSuccess(true);
                result.setMovement(
                    accountDAO.transfer(
                        accountFrom, accountTo, request.getAmount()
                    )
                );
            }
        }

        return result;
    }

    @PostMapping("/credit")
    AccountResponse getCredit(
        @RequestBody CreditRequest request
    ) {
        init();
        AccountResponse result  = new AccountResponse();
        double AMOUNT_OK = 4000;
        if(request.getAmount() > AMOUNT_OK){
            User user = getUser();
            Account account = new Account(
                Account.TYPE_CREDIT, request.getAmount(), "CREDIT", ""
            );
            account = accountDAO.create(user, account);
            result.setSuccess(true);
            result.setAccount(new AccountResponseAccount(account.getId()));
        }
        return result;
    }
    
    @PostMapping("/credit/{id}/{accountNumber}")
    TransferResponse getCreditDeposit(
        @PathVariable long id,
        @PathVariable String accountNumber
    ) {
        init();
        TransferResponse result  = new TransferResponse();
        //User user = getUser();
        Account accountFrom = accountDAO.getAccount(null, null, id);
        Account accountTo = accountDAO.getAccount(null, accountNumber);
        if(accountFrom == null){
            throwError(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
        }
        double amount = accountFrom.getBalance();
        double newBalanceFrom = accountFrom.getBalance() - amount;
        double newBalanceTo = accountTo.getBalance() + amount;
        if(newBalanceFrom < 0){
            throwError(HttpStatus.BAD_REQUEST, "Monto solicitado mayor al saldo");
        } else if(amount == 0){
            throwError(HttpStatus.BAD_REQUEST, "Saldo insuficiente de la cuenta de origen");
        } else{
            if(accountDAO.setBalance(accountFrom, newBalanceFrom) &&
                accountDAO.setBalance(accountTo, newBalanceTo)){
                result.setSuccess(true);
                result.setMovement(
                    accountDAO.transfer(
                        accountFrom, accountTo, amount
                    )
                );
            }
        }
        return result;
    }

    @RequestMapping("/logout")
    LogoutResponse logout(@RequestHeader (name="Authorization") String header) {
        init();
        String jwtToken = Utils.getToken(header);
        User user = getUser();
        userDAO.removeToken(user, jwtToken);
        LogoutResponse result = new LogoutResponse("ok");
        return result;
    }

	private void authenticate(String username, String password) throws Exception {
		try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
            );
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
	}

    private void throwError(HttpStatus statusCode, String message) 
        throws ResponseStatusException{
        throw new ResponseStatusException(statusCode, message);
    }
}
