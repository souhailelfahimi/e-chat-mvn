package org.ordinal.src.db;

import java.sql.*;

public class DatabaseService {
    private final String url = "jdbc:mysql://localhost:3306/e-chat";
    private final String username = "root"; 
    private final String password = "root";

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    
    public void saveMessage(String senderId, String receiverId, String message) {
        String sql = "INSERT INTO message(senderId, receiverId, message) VALUES(?, ?, ?)";
        
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, senderId);
            pstmt.setString(2, receiverId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        DatabaseService databaseService=new DatabaseService();
        databaseService.saveMessage("1","2","salam");
    }
}
