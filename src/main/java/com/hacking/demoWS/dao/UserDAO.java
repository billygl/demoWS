package com.hacking.demows.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hacking.demows.models.User;

public class UserDAO extends BaseDAO{
    
    public UserDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        super(jdbcURL, jdbcUsername, jdbcPassword);
    }
    
    public User validateUser(String _user, String _password){
        User user = null;
        try{
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
                int id = resultSet.getInt("user_id");
                String username = resultSet.getString("user");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                user = new User(id, username, password, role);
            }
            
            resultSet.close();
            statement.close();
        
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
        return user;
    }
    
    public User getUser(String _user){
        User user = null;
        try{
            //unsecured
            String sql = "SELECT * FROM users WHERE user = '" + _user + "'";
            System.out.println(sql);
            connect();        
            PreparedStatement statement = jdbcConnection.prepareStatement(sql);
            //statement.setString(1, _user);
            //statement.setString(2, _password);
            
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                int id = resultSet.getInt("user_id");
                String username = resultSet.getString("user");
                String password = resultSet.getString("password");
                String role = resultSet.getString("role");
                user = new User(id, username, password, role);
            }
            
            resultSet.close();
            statement.close();
        
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        
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