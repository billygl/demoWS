package com.hacking.demows;

import java.math.BigDecimal;
import java.util.List;

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
        User user = userDAO.validateUser(request.getUser(), request.getPass());
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
}
