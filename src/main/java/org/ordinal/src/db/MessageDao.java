package org.ordinal.src.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MessageDao {
    private final DatabaseService databaseService;

    public MessageDao(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void saveMessage(String senderId, String receiverId, String message) {
        String sql = "INSERT INTO message(senderId, receiverId, message) VALUES(?, ?, ?)";

        try (Connection conn = this.databaseService.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, senderId);
            pstmt.setString(2, receiverId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
