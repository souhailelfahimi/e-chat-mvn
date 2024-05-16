package org.ordinal.src.repository;

import org.ordinal.src.configuration.DatabaseConnection;
import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MessageDao {
    private final DatabaseConnection databaseConnection;

    public MessageDao(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void saveMessage(String senderId, String receiverId, String message) {
        String sql = "INSERT INTO message(senderId, receiverId, message) VALUES(?, ?, ?)";

        try (Connection conn = this.databaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, senderId);
            pstmt.setString(2, receiverId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void saveMessages(List<Message> messages) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_body) VALUES (?, ?, ?)";

        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            for (Message message : messages) {
                pstmt.setInt(1, message.getSender().getUserId());
                pstmt.setInt(2, message.getReceiver().getUserId());
                pstmt.setString(3, message.getMessageBody());
                pstmt.addBatch();
            }

            pstmt.executeBatch();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    public List<Message> findMessagesBySenderAndReceiver(User sender, User recipient) {

        List<Message> results = new ArrayList<>();
        String sql = "SELECT * FROM messages " +
                     "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?) " +
                     "ORDER BY message_created_at ASC";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, sender.getUserId());
            pstmt.setInt(2, recipient.getUserId());
            pstmt.setInt(3, recipient.getUserId());
            pstmt.setInt(4, sender.getUserId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Message message = new Message();
                message.setMessageBody(rs.getString("message_body"));
                if(sender.getUserId() == rs.getInt("sender_id")
                   && recipient.getUserId() == rs.getInt("receiver_id")) {
                    message.setSender(sender);
                    message.setReceiver(recipient);
                }else {
                    message.setSender(recipient);
                    message.setReceiver(sender);
                }

                results.add(message);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return results;
    }

}
