package com.hacking.demows.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hacking.demows.models.Product;
import com.hacking.demows.models.User;

public class ProductDAO extends BaseDAO {
    
    public ProductDAO(String jdbcURL, String jdbcUsername, String jdbcPassword) {
        super(jdbcURL, jdbcUsername, jdbcPassword);
    }
    
    public boolean insertProduct(Product product) throws SQLException {
        String sql = "INSERT INTO products (title, description, price, image_url) VALUES (?, ?, ?, ?)";
        connect();
        
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setString(1, product.getTitle());
        statement.setString(2, product.getDescription());
        statement.setFloat(3, product.getPrice());
        statement.setString(4, product.getImageURL());
        
        boolean rowInserted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowInserted;
    }
    
    public List<Product> listAllProducts() throws SQLException {
        List<Product> listProduct = new ArrayList<>();
        
        String sql = "SELECT * FROM products";
        
        connect();
        
        Statement statement = jdbcConnection.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        
        while (resultSet.next()) {
            int id = resultSet.getInt("product_id");
            String title = resultSet.getString("title");
            String description = resultSet.getString("description");
            float price = resultSet.getFloat("price");
            String imageURL = resultSet.getString("image_url");
            
            Product product = new Product(id, title, description, price, imageURL);
            listProduct.add(product);
        }
        
        resultSet.close();
        statement.close();
        
        disconnect();
        
        return listProduct;
    }
    
    public List<Product> listProductsByUser(User user){
        List<Product> listProduct = new ArrayList<>();        
        try {
            String sql = "SELECT * FROM products WHERE user_id = " +
                user.getId();
            connect();
            
            Statement statement = jdbcConnection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            
            while (resultSet.next()) {
                int id = resultSet.getInt("product_id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");
                float price = resultSet.getFloat("price");
                String imageURL = resultSet.getString("image_url");
                
                Product product = new Product(id, title, description, price, imageURL);
                listProduct.add(product);
            }
            
            resultSet.close();
            statement.close();
            
            disconnect();    
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return listProduct;
    }
    
    public boolean deleteProduct(Product product) {
        boolean rowDeleted = false;
        try {
            String sql = "DELETE FROM products where product_id = ?";        
            connect();
            
            PreparedStatement statement = jdbcConnection.prepareStatement(sql);
            statement.setInt(1, product.getId());
            
            rowDeleted = statement.executeUpdate() > 0;
            statement.close();
            disconnect();   
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return rowDeleted;     
    }
    
    public boolean updateProduct(Product product) {
        boolean rowUpdated = false;
        try {            
            String sql = "UPDATE products SET title = ?, description = ?, price = ?, image_url = ?";
            if(product.getImageURL() == null){
                sql = "UPDATE products SET title = ?, description = ?, price = ?";
            }
            sql += " WHERE product_id = ?";
            connect();
            
            PreparedStatement statement = jdbcConnection.prepareStatement(sql);
            statement.setString(1, product.getTitle());
            statement.setString(2, product.getDescription());
            statement.setFloat(3, product.getPrice());
            int p = 4;
            if(product.getImageURL() != null){
                statement.setString(p, product.getImageURL());
                p++;
            }
            statement.setInt(p, product.getId());
            
            rowUpdated = statement.executeUpdate() > 0;
            statement.close();
            disconnect();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rowUpdated;
    }
    
    public Product getProduct(int id) throws SQLException {
        Product product = null;
        String sql = "SELECT * FROM products WHERE product_id = ?";
        
        connect();
        
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setInt(1, id);
        
        ResultSet resultSet = statement.executeQuery();
        
        if (resultSet.next()) {
            String title = resultSet.getString("title");
            String description = resultSet.getString("description");
            float price = resultSet.getFloat("price");
            String imageURL = resultSet.getString("image_url");
            
            product = new Product(id, title, description, price, imageURL);
        }
        
        resultSet.close();
        statement.close();
        
        return product;
    }
}