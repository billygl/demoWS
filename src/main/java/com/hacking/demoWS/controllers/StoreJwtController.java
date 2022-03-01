package com.hacking.demows.controllers;

import java.util.List;

import com.hacking.demows.components.JwtTokenUtil;
import com.hacking.demows.dao.ProductDAO;
import com.hacking.demows.dao.UserDAO;
import com.hacking.demows.dto.JwtRequest;
import com.hacking.demows.dto.JwtResponse;
import com.hacking.demows.dto.LogoutResponse;
import com.hacking.demows.models.Product;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@CrossOrigin
@RequestMapping("storejwt")
public class StoreJwtController {
    private UserDAO userDAO;
    private ProductDAO productDAO;
    
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
        productDAO = new ProductDAO(jdbcURL, jdbcUsername, jdbcPassword);
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
    
    @GetMapping("/products")
	public List<Product> getProducts(){
        init();
        return productDAO.listProductsByUser(getUser());
	}

    private User getUser(){
        UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext()
            .getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        return userDAO.getUser(username);
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

    @RequestMapping(value = "/products/{productId}", method = RequestMethod.PATCH)
    com.hacking.demows.models.Product updateProduct(
        @PathVariable int productId,
        @RequestBody Product request
    ) {
        init();
        Product product = new Product(
            productId,
            request.getTitle(),
            request.getDescription(),
            request.getPrice(),
            request.getImageURL()
        );
        productDAO.updateProduct(product);
        return product;
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
