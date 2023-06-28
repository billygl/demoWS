package com.hacking.demows.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import com.hacking.demows.models.Account;
import com.hacking.demows.models.Movement;
import com.hacking.demows.models.User;

public class MovementDAO extends BaseDAO {
    
    public MovementDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        super(jdbcURL, jdbcUsername, jdbcPassword);
    }
    
    public Movement transfer(Account accountFrom, Account accountTo,
        double amount){
        Movement result = null;
        try{
            connect();
            String sql = "INSERT INTO movements (amount";
            if(accountFrom != null){
                sql += ", account_id_from, account_from";
            }
            if(accountTo != null){
                sql += ", account_id_to, account_to";
            }
            sql += ", created_at) VALUES (?";
            if(accountFrom != null){
                sql += ", ?, ?";
            }
            if(accountTo != null){
                sql += ", ?, ?";
            }
            sql += ", NOW())";
            PreparedStatement statement = jdbcConnection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS
            );
            statement.setDouble(1, amount);
            int column = 2;
            if(accountFrom != null){
                statement.setLong(column++, accountFrom.getId());
                statement.setString(column++, accountFrom.getNumber());
            }
            if(accountTo != null){
                statement.setLong(column++, accountTo.getId());
                statement.setString(column++, accountTo.getNumber());
            }
            System.out.println(sql);
            statement.executeUpdate();
            ResultSet resultSet = statement.getGeneratedKeys();
            if(resultSet.next()) {
                long id = resultSet.getLong(1);
                result = new Movement(id);
            }            
            resultSet.close();
            statement.close();
            disconnect();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return result;     
    }

    public Movement getMovement(User user, long id) {
        Movement result = null;
        try{
            String sql = "SELECT * FROM movements WHERE ";
            sql += " movement_id = ?";
            if(user != null){
                sql += " and user_id = " + user.getId();
            }
            System.out.println(sql);
            
            connect();
            
            PreparedStatement statement = jdbcConnection.prepareStatement(sql);
            statement.setLong(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if(resultSet.next()) {
                id = resultSet.getLong("movement_id");
                double amount = resultSet.getDouble("amount");
                String accountFrom = resultSet.getString("account_from");
                String accountTo = resultSet.getString("account_to");
                Date createdAt = new Date(resultSet.getTimestamp("created_at").getTime());
                
                result = new Movement(
                    id, amount, accountFrom, accountTo, createdAt
                );
            }
            
            resultSet.close();
            statement.close();
            
            disconnect();
        }catch(SQLException ex){
            ex.printStackTrace();
        }        
        return result;
    }
}