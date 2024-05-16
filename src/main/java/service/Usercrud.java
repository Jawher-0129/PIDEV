package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import Entity.User;
import service.BCrypt;
import cnx.MyConnections;
import java.sql.Statement; // Import Statement class here

public class Usercrud {
    private Connection cnx;

    public Usercrud() {
        this.cnx = MyConnections.getInstance().getCnx();
    }

    public User login(String email, String password) {
        User loggedInUser = null;
        try {
            PreparedStatement stm = cnx.prepareStatement("SELECT * FROM user1 WHERE email = ?");
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                if (BCrypt.checkpw(password, hashedPassword)) {
                    int id = rs.getInt("id");
                    String nom = rs.getString("nom");
                    String prenom = rs.getString("prenom");
                    String adresse = rs.getString("adresse");
                    String telephone = rs.getString("telephone");
                    String rolesString = rs.getString("Roles");
                    String[] rolesArray = rolesString.split(",");
                    List<String> roles = new ArrayList<>();
                    for (String role : rolesArray) {
                        roles.add(role);
                    }

                    loggedInUser = new User(id, nom, prenom, email, hashedPassword, adresse, telephone, rolesString);
                } else {
                    System.out.println("Incorrect password.");
                }
            } else {
                System.out.println("User not found with email: " + email);
            }
        } catch (SQLException ex) {
            System.out.println("Error logging in: " + ex.getMessage());
        }
        return loggedInUser;
    }


    public void addUser(User user) {
        String email = user.getEmail();
        String checkQuery = "SELECT COUNT(*) FROM user1 WHERE email='" + email + "'";
        try {
            Statement checkStatement = cnx.createStatement();
            ResultSet resultSet = checkStatement.executeQuery(checkQuery);
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count > 0) {
                System.out.println("User with this email already exists in the database.");
                return;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking for existing email: " + e.getMessage());
            return;
        }

        String query = "INSERT INTO user1 (nom, prenom, email, `password`, Roles, adresse, telephone) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement st = cnx.prepareStatement(query)) {
            st.setString(1, user.getNom());
            st.setString(2, user.getPrenom());
            st.setString(3, user.getEmail());
            st.setString(4, user.getPassword());
            st.setString(5, user.getRoles());
            st.setString(6, user.getAdresse());
            st.setString(7, user.getTelephone());

            st.executeUpdate();
            System.out.println("User added!!");
        } catch (SQLException e) {
            System.out.println("Error occurred while adding user: " + e.getMessage());
        }
    }


    public void addUser2(User user) {


        // If the email doesn't exist, proceed with adding the user
        String requete = "INSERT INTO Utilisateurs (nom, prenom, email, `password`, Role, adresse) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement st = MyConnections.getInstance().getCnx().prepareStatement(requete)) {
            st.setString(1, user.getNom());
            st.setString(2, user.getPrenom());
            st.setString(3, user.getEmail());
            st.setString(4, user.getPassword());
            st.setString(5, "user");
            st.setString(6, user.getAdresse());
            st.executeUpdate();
            System.out.println("User added!!");
        } catch (SQLException e) {
            System.out.println("Error occurred while adding user: " + e.getMessage());
        }
    }
    public boolean isEmailExistsInDatabase(String email) {
        String query = "SELECT COUNT(*) FROM user1 WHERE email=?";
        try {
            PreparedStatement statement = MyConnections.getInstance().getCnx().prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking email existence: " + e.getMessage());
        }
        return false;
    }

    public User getUserByEmail(String email) {
        String query = "SELECT * FROM user1 WHERE email=?";
        User user = null;

        try {
            PreparedStatement statement = MyConnections.getInstance().getCnx().prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String userEmail = resultSet.getString("email");
                String password = resultSet.getString("password");
                String adresse = resultSet.getString("adresse"); // Additional field
                String telephone = resultSet.getString("telephone"); // Additional field
                String role = resultSet.getString("Roles");

                user = new User(id, nom, prenom, userEmail, password, adresse, telephone, role); // Adjusted instantiation
            }
        } catch (SQLException e) {
            System.out.println("Error fetching user by email: " + e.getMessage());
        }

        return user;
    }


    public void updateUser(User user) {
        // Check if the email already exists in the database, excluding the current user
        String email = user.getEmail();
        int userId = user.getId();
        String checkQuery = "SELECT COUNT(*) FROM user1 WHERE email='" + email + "' AND id<>" + userId;
        try {
            Statement checkStatement = MyConnections.getInstance().getCnx().createStatement();
            ResultSet resultSet = checkStatement.executeQuery(checkQuery);
            resultSet.next();
            int count = resultSet.getInt(1);
            if (count > 0) {
                System.out.println("this email already exists in the database.");
                return; // Exit the method without updating the user
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while checking for existing email: " + e.getMessage());
            return; // Exit the method if an error occurs
        }

        // If the email doesn't exist, proceed with updating the user
        String requete = "UPDATE user1 SET " +
                "nom='" + user.getNom() + "', " +
                "prenom='" + user.getPrenom() + "', " +
                "email='" + user.getEmail() + "', " +
                "`password`='" + user.getPassword() + "', " +
                "adresse='" + user.getAdresse() + "', " + // Added comma here
                "telephone='" + user.getTelephone() + "', " +
                "Roles='" + user.getRoles() + "' " + // Removed comma here
                "WHERE id=" + user.getId();

        try {
            Statement st = MyConnections.getInstance().getCnx().createStatement();
            st.executeUpdate(requete);
            System.out.println("User updated!!");
        } catch (SQLException e) {
            System.out.println("Error occurred while updating user: " + e.getMessage());
        }
    }

    public void deleteUser(User user) {
        String requete = "DELETE FROM user1 WHERE id=" + user.getId();
        try {
            Statement st = MyConnections.getInstance().getCnx().createStatement();
            st.executeUpdate(requete);
            System.out.println("User deleted!!");
        } catch (SQLException e) {
            System.out.println("Error occurred while deleting user: " + e.getMessage());
        }
    }

    public List<User> getAllData() {
        List<User> userList = new ArrayList<>();

        String query = "SELECT id, nom, prenom, email, `password`, `Roles`, adresse, telephone FROM user1"; // Include telephone field in the query
        try {
            Statement statement = MyConnections.getInstance().getCnx().createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String prenom = resultSet.getString("prenom");
                String email = resultSet.getString("email");
                String password = resultSet.getString("password");
                String roles = resultSet.getString("Roles"); // Corrected the column name
                String adresse = resultSet.getString("adresse"); // Corrected variable name
                String telephone = resultSet.getString("telephone"); // Added telephone field

                User user = new User(id, nom, prenom, email, password, adresse, telephone, roles);
                userList.add(user);
            }
        } catch (SQLException e) {
            System.out.println("Error occurred while fetching user data: " + e.getMessage());
        }

        return userList;
    }
    public void resetPassword(String email, String newPassword) {
        String query = "UPDATE user1 SET `password` = ? WHERE email = ?";

        try {
            PreparedStatement statement = MyConnections.getInstance().getCnx().prepareStatement(query);
            statement.setString(1, newPassword);
            statement.setString(2, email);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Password reset successfully.");
            } else {
                System.out.println("Failed to reset password. User not found.");
            }
        } catch (SQLException e) {
            System.out.println("Error resetting password: " + e.getMessage());
        }
    }
    public boolean isVerificationCodeCorrect(String email, String verificationCode) {
        String query = "SELECT verification_code FROM user1 WHERE email = ?";
        try {
            PreparedStatement statement = MyConnections.getInstance().getCnx().prepareStatement(query);
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String storedVerificationCode = resultSet.getString("verification_code");
                return verificationCode.equals(storedVerificationCode);
            } else {
                System.out.println("No user found with email: " + email);
                return false; // Email not found in the database
            }
        } catch (SQLException e) {
            System.out.println("Error checking verification code: " + e.getMessage());
            return false; // Error occurred while querying the database
        }
    }


    public void updateVerificationCode(String email, String verificationCode) {
        String query = "UPDATE user1 SET verification_code = ? WHERE email = ?";
        try {
            PreparedStatement statement = MyConnections.getInstance().getCnx().prepareStatement(query);
            statement.setString(1, verificationCode);
            statement.setString(2, email);
            statement.executeUpdate();
            System.out.println("Verification code updated for user with email: " + email);
        } catch (SQLException e) {
            System.out.println("Error updating verification code: " + e.getMessage());
        }
    }




}