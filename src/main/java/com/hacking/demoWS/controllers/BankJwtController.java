package com.hacking.demows.controllers;

import java.util.ArrayList;
import java.util.List;

import com.hacking.demows.components.JwtTokenUtil;
import com.hacking.demows.dao.AccountDAO;
import com.hacking.demows.dao.UserDAO;
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

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public ResponseEntity<?> createAuthenticationToken(
        @RequestBody JwtRequest authenticationRequest) throws Exception {
        init();
        authenticate(
            authenticationRequest.getUsername(), 
            authenticationRequest.getPassword()
        );

		final User user = userDAO.validateUser(
            authenticationRequest.getUsername(), 
            authenticationRequest.getPassword()
        );

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

    @PostMapping("/withdraw/{accountNumber}")
    Account withdraw(
        @PathVariable String accountNumber,
        @RequestBody WithdrawRequest request
    ) {
        init();
        Account result  = null;        
        Account account = accountDAO.getAccount(getUser(), accountNumber);
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
        return result;
    }
    
    @PostMapping("/deposit/{accountNumber}")
    Account deposit(
        @PathVariable String accountNumber,
        @RequestBody DepositRequest request
    ) {
        init();
        Account result  = null;        
        Account account = accountDAO.getAccount(getUser(), accountNumber);
        if(account == null){
            throwError(HttpStatus.NOT_FOUND, "Cuenta no encontrada");
        }
        double newBalance = account.getBalance() + request.getAmount();
        if(accountDAO.setBalance(account, newBalance)){
            result = account;
        }
        return result;
    }

    @PostMapping("/transfer/{accountNumberFrom}/{accountNumberTo}")
    Account transfer(
        @PathVariable String accountNumberFrom,
        @PathVariable String accountNumberTo,
        @RequestBody TransferRequest request
    ) {
        init();
        Account result  = null;
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
                result = accountFrom;
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
