package com.hacking.demows;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class BankEndpoint {
    private static final String NAMESPACE_URI = "http://hacking.com/demows";

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getBalancesRequest")
	@ResponsePayload
	public GetBalancesResponse getBalances(@RequestPayload GetBalancesRequest request) {
		GetBalancesResponse response = new GetBalancesResponse();
		List<Balance> balances = response.getBalances();
        Balance balance = new Balance();
        balance.setAccountName("ahorro");
        balance.setAccountNumber("12341234");
        balance.setAmount(new BigDecimal(100));
        balances.add(balance);
		return response;
	}
}
