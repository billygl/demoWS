package com.hacking.demows.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.hacking.demows.models.Product;

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
    
    public boolean deleteProduct(Product product) throws SQLException {
        String sql = "DELETE FROM products where product_id = ?";
        
        connect();
        
        PreparedStatement statement = jdbcConnection.prepareStatement(sql);
        statement.setInt(1, product.getId());
        
        boolean rowDeleted = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
        return rowDeleted;     
    }
    
    public boolean updateProduct(Product product) throws SQLException {
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
        
        boolean rowUpdated = statement.executeUpdate() > 0;
        statement.close();
        disconnect();
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