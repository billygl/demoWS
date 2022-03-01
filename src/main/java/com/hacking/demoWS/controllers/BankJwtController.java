package com.hacking.demows.controllers;

import com.hacking.demows.components.JwtTokenUtil;
import com.hacking.demows.dao.AccountDAO;
import com.hacking.demows.dao.UserDAO;
import com.hacking.demows.dto.JwtRequest;
import com.hacking.demows.dto.JwtResponse;
import com.hacking.demows.models.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

		return ResponseEntity.ok(new JwtResponse(token));
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
}
