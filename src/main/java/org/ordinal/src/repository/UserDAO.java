package org.ordinal.src.repository;

import org.ordinal.src.configuration.DatabaseConnection;
import org.ordinal.src.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    private final DatabaseConnection databaseConnection;

    public UserDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Connection connection = databaseConnection.getConnection();
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
        try (Connection connection = databaseConnection.getConnection()) {
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

            try (Connection connection = databaseConnection.getConnection();
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

        try (Connection connection = databaseConnection.getConnection();
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


}

