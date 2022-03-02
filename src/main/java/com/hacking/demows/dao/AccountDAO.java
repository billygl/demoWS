package com.hacking.demows.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hacking.demows.models.Account;
import com.hacking.demows.models.User;



public class AccountDAO extends BaseDAO {
    
    public AccountDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        super(jdbcURL, jdbcUsername, jdbcPassword);
    }
    
    public List<Account> list(User user) {
        List<Account> list = new ArrayList<>();
        try{
            //unsecured
            String sql = "SELECT * FROM accounts WHERE user_id = " + 
                user.getId();
            System.out.println(sql);
            
            connect();
            
            Statement statement = jdbcConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            while (resultSet.next()) {
                int id = resultSet.getInt("account_id");
                double balance = resultSet.getDouble("balance");
                String name = resultSet.getString("name");
                String number = resultSet.getString("number");
                
                Account account = new Account(
                    id, balance, name, number
                );
                list.add(account);
            }
            
            resultSet.close();
            statement.close();
            
            disconnect();
        }catch(SQLException ex){
            ex.printStackTrace();
        }        
        return list;
    }
    
    public List<Account> listByDocumentId(String documentId) {
        List<Account> list = new ArrayList<>();
        try{
            //unsecured
            String sql = "SELECT * FROM accounts " +
                "INNER JOIN users ON users.user_id = accounts.user_id " +
                "WHERE document_id = '" + documentId + "'";
            System.out.println(sql);
            
            connect();
            
            Statement statement = jdbcConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            while (resultSet.next()) {
                int id = resultSet.getInt("account_id");
                double balance = resultSet.getDouble("balance");
                String name = resultSet.getString("name");
                String number = resultSet.getString("number");
                
                Account account = new Account(
                    id, balance, name, number
                );
                list.add(account);
            }
            
            resultSet.close();
            statement.close();
            
            disconnect();
        }catch(SQLException ex){
            ex.printStackTrace();
        }        
        return list;
    }
    
    public Account getAccount(User user, String number) {
        Account account = null;
        try{
            //unsecured
            String sql = "SELECT * FROM accounts WHERE number = '" + number + "'";
            if(user != null){
                sql += " and user_id = " + user.getId();
            }
            System.out.println(sql);
            
            connect();
            
            Statement statement = jdbcConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);            
            
            if(resultSet.next()) {
                int id = resultSet.getInt("account_id");
                double balance = resultSet.getDouble("balance");
                String name = resultSet.getString("name");
                
                account = new Account(
                    id, balance, name, number
                );
            }
            
            resultSet.close();
            statement.close();
            
            disconnect();
        }catch(SQLException ex){
            ex.printStackTrace();
        }        
        return account;
    }

    public boolean setBalance(Account account, double balance){
        boolean result = false;
        try{   
            String sql = "UPDATE accounts SET balance = ?";
            sql += " WHERE account_id = ?";
            connect();
            
            PreparedStatement statement = jdbcConnection.prepareStatement(sql);
            statement.setDouble(1, balance);
            statement.setInt(2, account.getId());
            
            result = statement.executeUpdate() > 0;
            statement.close();
            disconnect();
        }catch(SQLException ex){

        }
        return result;     
    }
}