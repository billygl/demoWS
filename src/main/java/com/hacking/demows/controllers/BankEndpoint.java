package com.hacking.demows.controllers;

import java.math.BigDecimal;
import java.util.List;

import com.hacking.demows.Balance;
import com.hacking.demows.DepositRequest;
import com.hacking.demows.DepositResponse;
import com.hacking.demows.GetBalancesRequest;
import com.hacking.demows.GetBalancesResponse;
import com.hacking.demows.ServiceStatus;
import com.hacking.demows.TransferRequest;
import com.hacking.demows.TransferResponse;
import com.hacking.demows.WithdrawRequest;
import com.hacking.demows.WithdrawResponse;
import com.hacking.demows.dao.AccountDAO;
import com.hacking.demows.dao.UserDAO;
import com.hacking.demows.exception.ServiceFaultException;
import com.hacking.demows.models.Account;
import com.hacking.demows.models.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class BankEndpoint {
    private static final String NAMESPACE_URI = "http://hacking.com/demows";

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

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBalancesRequest")
	@ResponsePayload
	public GetBalancesResponse getBalances(@RequestPayload GetBalancesRequest request) {
        init();
        User user = null;//userDAO.validateUser(request.getUser(), request.getPass());
        if(user == null){
            throwError("Error", "401", "Usuario o contraseña no válidos");
        } else{
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
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "withdrawRequest")
	@ResponsePayload
	public WithdrawResponse withdraw(@RequestPayload WithdrawRequest request) {
        init();
        //User user = userDAO.validateUser(request.getUser(), request.getPass());
        User user = null;
        if(user == null){
            throwError("Error", "401", "Usuario o contraseña no válidos");
        } else{
            WithdrawResponse response = new WithdrawResponse();
            boolean result = false;
            Account account = accountDAO.getAccount(user, request.getAccount());
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
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "depositRequest")
	@ResponsePayload
	public DepositResponse deposit(@RequestPayload DepositRequest request) {
        init();
        //User user = userDAO.validateUser(request.getUser(), request.getPass());
        User user = null;
        if(user == null){
            throwError("Error", "401", "Usuario o contraseña no válidos");
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
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "transferRequest")
	@ResponsePayload
	public TransferResponse transfer(@RequestPayload TransferRequest request) {
        init();
        //User user = userDAO.validateUser(request.getUser(), request.getPass());
        User user = null;
        if(user == null){
            throwError("Error", "401", "Usuario o contraseña no válidos");
        } else{
            TransferResponse response = new TransferResponse();
            boolean result = false;
            Account accountFrom = 
                accountDAO.getAccount(user, request.getAccountFrom());
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
