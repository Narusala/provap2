package org.example.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private Connection connection;

    public Connection getConnection() {
        Connection con;
        try {
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/supermercado", "super", "super");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return con;
    }


}
