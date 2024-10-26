package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegisterPage extends JFrame {

    private Connection conn;
    public RegisterPage() {
        setTitle("Register New Employee");
        setSize(400, 400);
        setLayout(new GridLayout(7, 2));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel empIdLabel = new JLabel("Employee ID:");
        JTextField empIdField = new JTextField();

        JLabel genderLabel = new JLabel("Gender:");
        JRadioButton maleRadio = new JRadioButton("Male");
        JRadioButton femaleRadio = new JRadioButton("Female");
        ButtonGroup genderGroup = new ButtonGroup();
        genderGroup.add(maleRadio);
        genderGroup.add(femaleRadio);

        JPanel genderPanel = new JPanel();
        genderPanel.add(maleRadio);
        genderPanel.add(femaleRadio);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        JLabel contactLabel = new JLabel("Contact:");
        JTextField contactField = new JTextField();

        JButton submitButton = new JButton("Submit");

        add(nameLabel);
        add(nameField);
        add(empIdLabel);
        add(empIdField);
        add(genderLabel);
        add(genderPanel);
        add(emailLabel);
        add(emailField);
        add(contactLabel);
        add(contactField);
        add(new JLabel());
        add(submitButton);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = nameField.getText();
                String empId = empIdField.getText();
                String gender = null;
                if (maleRadio.isSelected()) {
                    gender = "Male";
                } else if (femaleRadio.isSelected()) {
                    gender = "Female";
                }
                String email = emailField.getText();
                String contact = contactField.getText();

                if (validateInput(name, empId, email, contact, gender)) {
                    insertEmployee(name, empId, gender, email, contact);
                } else {
                    StringBuilder errorMessage = new StringBuilder("Please fill in all fields properly!\n");
                    if (!isValidEmail(email)) {
                        errorMessage.append("Invalid email format.\n");
                    }
                    if (!isValidPhone(contact)) {
                        errorMessage.append("Invalid Phone Number.");
                    }
                    JOptionPane.showMessageDialog(null, errorMessage.toString());
                }
            }
        });

        setVisible(true);
    }

    private boolean validateInput(String name, String empId, String email, String contact, String gender) {
        return !name.isEmpty() && !empId.isEmpty() && !email.isEmpty() && !contact.isEmpty() 
               && gender != null && isValidEmail(email) && isValidPhone(contact);
    }    

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return email.matches(emailRegex);
    }

    private boolean isValidPhone(String phone) {
        String phoneRegex = "^\\d{10}$";
        return phone.matches(phoneRegex);
    }

    private void insertEmployee(String name, String empId, String gender, String email, String contact) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "meow");


            String query = "INSERT INTO employees (name, employee_id, gender, email, contact) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, empId);
            preparedStatement.setString(3, gender);
            preparedStatement.setString(4, email);
            preparedStatement.setString(5, contact);

            int rowsInserted = preparedStatement.executeUpdate();
            if (rowsInserted > 0) {
                JOptionPane.showMessageDialog(null, "Employee Registered Successfully!");
            }

            preparedStatement.close();
            conn.close();
            setVisible(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new RegisterPage();
    }
}
