package com.hacking.demows.filters;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.hacking.demows.components.JwtTokenUtil;
import com.hacking.demows.services.HackingUserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private HackingUserService hackingUserService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Override
	protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, 
        FilterChain chain) throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");

		String username = null;
		String jwtToken = null;
		
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				username = jwtTokenUtil.getUsernameFromToken(jwtToken);
			} catch (IllegalArgumentException e) {
				System.out.println("Unable to get JWT Token");
			} catch (ExpiredJwtException e) {
				System.out.println("JWT Token has expired");
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