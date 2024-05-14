package org.ordinal.src.db;

import org.ordinal.src.model.Message;
import org.ordinal.src.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final DatabaseService databaseService;

    public UserDAO(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("user_id");
                String name = rs.getString("user_name");

                User user = new User();
                user.setUserId(id);
                user.setUserName(name);

                users.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return users;
    }


    public void saveUser(User newUser) {
        try (Connection connection = databaseService.getConnection()) {
            String sql = "INSERT INTO users (user_name) VALUES (?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, newUser.getUserName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<User> findByNames(List<String> names) {
        List<User> foundUsers = new ArrayList<>();
        for (String name : names) {
            String sql = "SELECT * FROM users WHERE user_name = ?";

            try (Connection connection = databaseService.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(sql)) {

                stmt.setString(1, name);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String userName = rs.getString("user_name");

                    User user = new User();
                    user.setUserId(id);
                    user.setUserName(userName);

                    foundUsers.add(user);
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        return foundUsers;

    }

    public User findByName(String name) {
        User user = null;

        String sql = "SELECT * FROM users WHERE user_name = ?";

        try (Connection connection = databaseService.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("user_id");
                String userName = rs.getString("user_name");

                user = new User();
                user.setUserId(id);
                user.setUserName(userName);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return user;
    }


    public void saveMessages(List<Message> messages) {
        String sql = "INSERT INTO messages (sender_id, receiver_id, message_body) VALUES (?, ?, ?)";

        try (Connection connection = databaseService.getConnection();
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
}

