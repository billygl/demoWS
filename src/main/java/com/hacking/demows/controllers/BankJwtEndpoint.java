package com.hacking.demows.controllers;

import java.math.BigDecimal;
import java.util.List;

import com.hacking.demows.AuthenticateRequest;
import com.hacking.demows.AuthenticateResponse;
import com.hacking.demows.Balance;
import com.hacking.demows.DepositJwtRequest;
import com.hacking.demows.DepositResponse;
import com.hacking.demows.GetBalancesJwtRequest;
import com.hacking.demows.GetBalancesResponse;
import com.hacking.demows.ServiceStatus;
import com.hacking.demows.TransferJwtRequest;
import com.hacking.demows.TransferResponse;
import com.hacking.demows.WithdrawJwtRequest;
import com.hacking.demows.WithdrawResponse;
import com.hacking.demows.components.JwtTokenUtil;
import com.hacking.demows.dao.AccountDAO;
import com.hacking.demows.dao.UserDAO;
import com.hacking.demows.exception.ServiceFaultException;
import com.hacking.demows.models.Account;
import com.hacking.demows.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class BankJwtEndpoint {
    private static final String NAMESPACE_URI = "http://hacking.com/demows";

    @Autowired
	private JwtTokenUtil jwtTokenUtil;
    
    private UserDAO userDAO;
    private AccountDAO accountDAO;

    @Value( "${hacking.datasource.url}" )
    private String jdbcURL;
    @Value( "${hacking.datasource.username}" )
    private String jdbcUsername;
    @Value( "${hacking.datasource.password}" )
    private String jdbcPassword;

    public void init(){
        if(userDAO == null){
            userDAO = new UserDAO(jdbcURL, jdbcUsername, jdbcPassword);
            accountDAO = new AccountDAO(jdbcURL, jdbcUsername, jdbcPassword);
        }
    }

    private void throwError(String errorMessage, String statusCode, 
        String message) throws ServiceFaultException{
        ServiceStatus serviceStatus = new ServiceStatus();
        serviceStatus.setStatusCode(statusCode);
        serviceStatus.setMessage(message);

        throw new ServiceFaultException(errorMessage, serviceStatus);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "authenticateRequest")
	@ResponsePayload
	public AuthenticateResponse authenticate(@RequestPayload AuthenticateRequest request) {
        init();
        User user = userDAO.validateUser(request.getUser(), request.getPass());
        if(user == null){
            throwError("Error", "401", "Usuario o contraseña no válidos");
        } else{
            AuthenticateResponse response = new AuthenticateResponse();

            String token = jwtTokenUtil.generateToken(user);
            userDAO.addToken(token, user);
            response.setToken(token);
            return response;
        }
		return null;
	}
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBalancesJwtRequest")
	@ResponsePayload
	public GetBalancesResponse getBalances(@RequestPayload GetBalancesJwtRequest request) {
        init();
        String username = jwtTokenUtil.getUsernameFromToken(request.getToken());
        if(!userDAO.validateToken(username, request.getToken())){
            throwError("Error", "401", "Token inválido");
        } else{
            User user = userDAO.getUser(username);
            List<Account> list = accountDAO.list(user);

            GetBalancesResponse response = new GetBalancesResponse();
            List<Balance> balances = response.getBalances();
            for (Account account : list) {
                Balance balance = new Balance();
                balance.setAccountName(account.getName());
                balance.setAccountNumber(account.getNumber());
                balance.setAmount(new BigDecimal(account.getBalance()));
                balances.add(balance);
            }
            return response;
        }
		return null;
	}
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "withdrawJwtRequest")
	@ResponsePayload
	public WithdrawResponse withdraw(@RequestPayload WithdrawJwtRequest request) {
        init();
        String username = jwtTokenUtil.getUsernameFromToken(request.getToken());
        if(!userDAO.validateToken(username, request.getToken())){
            throwError("Error", "401", "Token inválido");
        } else{
            User user = userDAO.getUser(username);
            WithdrawResponse response = new WithdrawResponse();
            boolean result = false;
            Account account = accountDAO.getAccount(null, request.getAccount());
            if(account == null){
                throwError("Error", "404", "Cuenta no encontrada");
            }
            double newBalance = account.getBalance() - 
                request.getAmount().doubleValue();
            if(newBalance < 0){
                throwError("Error", "400", "Monto solicitado mayor al saldo");
            } else{
                result = accountDAO.setBalance(account, newBalance);
            }
            response.setResult(result);
            return response;
        }
		return null;
	}
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "depositJwtRequest")
	@ResponsePayload
	public DepositResponse deposit(@RequestPayload DepositJwtRequest request) {
        init();
        String username = jwtTokenUtil.getUsernameFromToken(request.getToken());
        if(!userDAO.validateToken(username, request.getToken())){
            throwError("Error", "401", "Token inválido");
        } else{
            DepositResponse response = new DepositResponse();
            boolean result = false;
            Account account = accountDAO.getAccount(null, request.getAccount());
            if(account == null){
                throwError("Error", "404", "Cuenta no encontrada");
            }
            double newBalance = account.getBalance() +
                request.getAmount().doubleValue();
            result = accountDAO.setBalance(account, newBalance);
            response.setResult(result);
            return response;
        }
		return null;
	}
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "transferJwtRequest")
	@ResponsePayload
	public TransferResponse transfer(@RequestPayload TransferJwtRequest request) {
        init();
        String username = jwtTokenUtil.getUsernameFromToken(request.getToken());
        if(!userDAO.validateToken(username, request.getToken())){
            throwError("Error", "401", "Token inválido");
        } else{
            User user = userDAO.getUser(username);
            TransferResponse response = new TransferResponse();
            boolean result = false;
            Account accountFrom = 
                accountDAO.getAccount(null, request.getAccountFrom());
            Account accountTo = 
                accountDAO.getAccount(null, request.getAccountTo());
            if(accountFrom == null){
                throwError("Error", "404", "Cuenta origen no encontrada");
            }
            if(accountTo == null){
                throwError("Error", "404", "Cuenta origen no destino");
            }
            double newBalanceFrom = accountFrom.getBalance() - 
                request.getAmount().doubleValue();
            double newBalanceTo = accountTo.getBalance() + 
                request.getAmount().doubleValue();
            if(newBalanceFrom < 0){
                throwError("Error", "400", "Monto solicitado mayor al saldo");
            } else{
                result = accountDAO.setBalance(accountFrom, newBalanceFrom);
                result = accountDAO.setBalance(accountTo, newBalanceTo);
            }
            response.setResult(result);
            return response;
        }
		return null;
	}
}
