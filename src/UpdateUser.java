package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;

public class UpdateUser extends JFrame {
    private JTextField nameField, employeeIdField, emailField, contactField;
    private JRadioButton maleButton, femaleButton;
    private JButton searchButton, updateButton;
    private Connection conn;
    private String userEmail;

    public UpdateUser() {
        setTitle("Update User");
        setSize(400, 400);
        setLayout(new BorderLayout());

        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Enter Email:"));
        JTextField searchEmailField = new JTextField(20);
        searchPanel.add(searchEmailField);
        searchButton = new JButton("Search");
        searchPanel.add(searchButton);
        add(searchPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(6, 2));

        formPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Employee ID:"));
        employeeIdField = new JTextField();
        formPanel.add(employeeIdField);

        formPanel.add(new JLabel("Gender:"));
        maleButton = new JRadioButton("Male");
        femaleButton = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleButton);
        genderGroup.add(femaleButton);
        JPanel genderPanel = new JPanel();
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        formPanel.add(genderPanel);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        emailField.setEditable(false); 
        formPanel.add(emailField);

        formPanel.add(new JLabel("Contact:"));
        contactField = new JTextField();
        formPanel.add(contactField);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        updateButton = new JButton("Update");
        buttonPanel.add(updateButton);
        add(buttonPanel, BorderLayout.SOUTH);

        enableForm(false);

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userEmail = searchEmailField.getText();
                searchUserByEmail(userEmail);
            }
        });

        searchEmailField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    userEmail = searchEmailField.getText();
                    searchUserByEmail(userEmail);
                }
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateUserDetails();
            }
        });

        setVisible(true);
    }

    private void enableForm(boolean enable) {
        nameField.setEnabled(enable);
        employeeIdField.setEnabled(enable);
        maleButton.setEnabled(enable);
        femaleButton.setEnabled(enable);
        contactField.setEnabled(enable);
        updateButton.setEnabled(enable);
    }

    private void searchUserByEmail(String email) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "meow");

            String query = "SELECT * FROM employees WHERE email = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, email);

            ResultSet rs = preparedStatement.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                employeeIdField.setText(rs.getString("employee_id"));
                String gender = rs.getString("gender");
                if (gender.equals("Male")) {
                    maleButton.setSelected(true);
                } else if (gender.equals("Female")) {
                    femaleButton.setSelected(true);
                }
                emailField.setText(rs.getString("email"));
                contactField.setText(rs.getString("contact"));
                enableForm(true);
            } else {
                JOptionPane.showMessageDialog(null, "No user found with the given email.");
                enableForm(false);
            }

            rs.close();
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateUserDetails() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "meow");
            String query = "UPDATE employees SET name = ?, employee_id = ?, gender = ?, contact = ? WHERE email = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);

            preparedStatement.setString(1, nameField.getText());
            preparedStatement.setString(2, employeeIdField.getText());
            String gender = maleButton.isSelected() ? "Male" : "Female";
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, contactField.getText());
            preparedStatement.setString(5, emailField.getText());

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(null, "User details updated successfully.");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new UpdateUser();
    }
}
