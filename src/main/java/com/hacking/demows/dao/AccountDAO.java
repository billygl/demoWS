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
                long id = resultSet.getLong("account_id");
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
                long id = resultSet.getLong("account_id");
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

    public Account create(User user, Account account){
        try{
            connect();
            String sql = "INSERT INTO accounts ";
            sql += "(balance, type, name, number, user_id) ";
            sql += "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement statement = jdbcConnection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
            );
            statement.setDouble(1, account.getBalance());
            statement.setInt(2, account.getType());
            statement.setString(3, account.getName());
            statement.setString(4, account.getNumber());
            statement.setInt(5, user.getId());
            
            System.out.println(sql);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(resultSet.next()) {
                long id = resultSet.getLong(1);
                account.setId(id);
            }            
            resultSet.close();
            statement.close();
            disconnect();
        }catch(SQLException ex){

        }
        return account;     
    }
    
    public Account getAccount(User user, String number) {
        return getAccount(user, number, 0);
    }
    public Account getAccount(User user, String number, long id) {
        Account account = null;
        try{
            //unsecured
            String sql = "SELECT * FROM accounts WHERE ";
            if(id != 0){
                sql += " account_id = " + id + "";
            }else{
                sql += " number = '" + number + "'";
            }
            if(user != null){
                sql += " and user_id = " + user.getId();
            }
            System.out.println(sql);
            
            connect();
            
            Statement statement = jdbcConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);            
            
            if(resultSet.next()) {
                id = resultSet.getLong("account_id");
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
            statement.setLong(2, account.getId());
            
            result = statement.executeUpdate() > 0;
            statement.close();
            disconnect();
        }catch(SQLException ex){

        }
        return result;     
    }
}