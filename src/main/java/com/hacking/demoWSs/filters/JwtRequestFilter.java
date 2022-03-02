package com.hacking.demows.filters;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hacking.demows.components.JwtTokenUtil;
import com.hacking.demows.dao.UserDAO;
import com.hacking.demows.services.HackingUserService;
import com.hacking.demows.models.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private HackingUserService hackingUserService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value( "${hacking.datasource.url}" )
    private String jdbcURL;
    @Value( "${hacking.datasource.username}" )
    private String jdbcUsername;
    @Value( "${hacking.datasource.password}" )
    private String jdbcPassword;

	private UserDAO userDAO;

    private void init(){
        userDAO = new UserDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

	@Override
	protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, 
        FilterChain chain) throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;
		
        jwtToken = Utils.getToken(requestTokenHeader);
        if (jwtToken != null) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
				init();
				if(!userDAO.validateToken(username, jwtToken)){
					username = null;
				}
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
			} catch(SignatureException e){
                System.out.println("Signature exception");
            }
		} else {
			System.out.println("JWT Token does not begin with Bearer String");
		}

		if (username != null && 
            SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails user = this.hackingUserService.loadUserByUsername(username);

			if (jwtTokenUtil.validateToken(jwtToken, user)) {
				UsernamePasswordAuthenticationToken 
                    usernamePasswordAuthenticationToken = 
                        new UsernamePasswordAuthenticationToken(
                            user, null, new ArrayList<>()
                        );
                    usernamePasswordAuthenticationToken
						.setDetails(
                            new WebAuthenticationDetailsSource()
                            .buildDetails(request)
                        );
				SecurityContextHolder.getContext()
                    .setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

}