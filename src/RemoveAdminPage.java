package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RemoveAdminPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton removeAdminButton;

    public RemoveAdminPage() {
        setTitle("Remove Admin");
        setSize(400, 300);
        setLayout(new GridLayout(3, 2));

        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();

        removeAdminButton = new JButton("Remove Admin");

        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(removeAdminButton);

        removeAdminButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removeAdminFromDatabase();
            }
        });

        setVisible(true);
    }

    private void removeAdminFromDatabase() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.");
            return;
        }

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "meow");
            
            String checkQuery = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            checkStmt.setString(2, password);

            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                String deleteQuery = "DELETE FROM users WHERE username = ?";
                PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
                deleteStmt.setString(1, username);
                
                int rowsAffected = deleteStmt.executeUpdate();
                if (rowsAffected > 0) {
                    JOptionPane.showMessageDialog(this, "Admin removed successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "Error removing admin.");
                }

                deleteStmt.close();
            } else {
                JOptionPane.showMessageDialog(this, "Admin not found or incorrect password.");
            }

            checkStmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new RemoveAdminPage();
    }
}
