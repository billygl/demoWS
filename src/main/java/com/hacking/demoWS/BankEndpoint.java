package com.hacking.demows;

import java.math.BigDecimal;
import java.util.List;

import com.hacking.demows.dao.ProductDAO;
import com.hacking.demows.dao.UserDAO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class BankEndpoint {
    private static final String NAMESPACE_URI = "http://hacking.com/demows";

    private UserDAO userDAO;

    @Value( "${hacking.datasource.url}" )
    private String jdbcURL;
    @Value( "${hacking.datasource.username}" )
    private String jdbcUsername;
    @Value( "${hacking.datasource.password}" )
    private String jdbcPassword;

    public BankEndpoint(){
        userDAO = new UserDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBalancesRequest")
	@ResponsePayload
	public GetBalancesResponse getBalances(@RequestPayload GetBalancesRequest request) {
		GetBalancesResponse response = new GetBalancesResponse();
		List<Balance> balances = response.getBalances();
        Balance balance = new Balance();
        try {
            balance.setAccountName(userDAO.listAllUsers().size() + "");    
        } catch (Exception e) {
            balance.setAccountName("-");
        }        
        balance.setAccountNumber("12341234");
        balance.setAmount(new BigDecimal(100));
        balances.add(balance);
		return response;
	}
}
