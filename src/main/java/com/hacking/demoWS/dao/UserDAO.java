package com.hacking.demows.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hacking.demows.models.User;

public class UserDAO {
    private String jdbcURL;
    private String jdbcUsername;
    private String jdbcPassword;
    private Connection jdbcConnection;
    
    public UserDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        this.jdbcURL = jdbcURL;
        this.jdbcUsername = jdbcUsername;
        this.jdbcPassword = jdbcPassword;
    }
    
    protected void connect() throws SQLException {
        if (jdbcConnection == null || jdbcConnection.isClosed()) {
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                throw new SQLException(e);
            }
            jdbcConnection = DriverManager.getConnection(
                                        jdbcURL, jdbcUsername, jdbcPassword);
        }
    }
    
    protected void disconnect() throws SQLException {
        if (jdbcConnection != null && !jdbcConnection.isClosed()) {
            jdbcConnection.close();
        }
    }
    
    public User validateUser(String _user, String _password) throws SQLException {
        User user = null;
        //unsecured
        String sql = "SELECT * FROM users WHERE user = '" + _user + "' " 
            + " and password = '" + _password +"'";
        System.out.println(sql);
        connect();        
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        //statement.setString(1, _user);
        //statement.setString(2, _password);
        
        ResultSet resultSet = statement.executeQuery();
        
        if (resultSet.next()) {
            String username = resultSet.getString("user");
            String password = resultSet.getString("password");
            String role = resultSet.getString("role");
            user = new User(username, password, role);
        }
        
        resultSet.close();
        statement.close();
        
        return user;
    }

    public List<User> listAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        connect();
        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        
        while (resultSet.next()) {
            int id = resultSet.getInt("user_id");
            String name = resultSet.getString("user");
            String password = resultSet.getString("password");
            String role = resultSet.getString("role");
            
            User user = new User(id, name, password, role);
            users.add(user);
        }
        resultSet.close();
        statement.close();
        disconnect();
        return users;
    }
}