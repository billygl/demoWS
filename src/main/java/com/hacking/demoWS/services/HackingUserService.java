package com.hacking.demows.services;

import java.util.ArrayList;

import com.hacking.demows.dao.UserDAO;
import com.hacking.demows.models.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class HackingUserService implements UserDetailsService {
    @Value( "${hacking.datasource.url}" )
    private String jdbcURL;
    @Value( "${hacking.datasource.username}" )
    private String jdbcUsername;
    @Value( "${hacking.datasource.password}" )
    private String jdbcPassword;

    private UserDAO userDAO;

    public void init(){
        userDAO = new UserDAO(jdbcURL, jdbcUsername, jdbcPassword);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        init();
        User user = userDAO.getUser(username);
        if (user != null) {
            String hashedPass = (new BCryptPasswordEncoder()).encode(user.getPass());
            System.out.println(hashedPass);
            return new org.springframework.security.core.userdetails.User(
                user.getUser(), 
                hashedPass, 
                new ArrayList<>()
            );
		} else {
			throw new UsernameNotFoundException(
                "User not found with username: " + username
            );
		}
    }
}