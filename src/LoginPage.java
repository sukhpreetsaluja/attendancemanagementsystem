package src;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginPage extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    public LoginPage() {

        setTitle("Attendance Management System");
        setSize(400, 300);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel background = new JLabel();
        setContentPane(background);
        background.setLayout(null);
        
        JLabel titleLabel = new JLabel("ATTENDANCE MANAGEMENT SYSTEM");
        titleLabel.setBounds(50, 20, 400, 30);
        titleLabel.setFont(new Font("Serif", Font.BOLD, 16));
        background.add(titleLabel);
        

        JLabel usernameLabel = new JLabel("USERNAME");
        usernameLabel.setBounds(50, 80, 100, 30);
        background.add(usernameLabel);
        
        usernameField = new JTextField();
        usernameField.setBounds(150, 80, 150, 30);
        background.add(usernameField);
        
        JLabel passwordLabel = new JLabel("PASSWORD");
        passwordLabel.setBounds(50, 120, 100, 30);
        background.add(passwordLabel);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(150, 120, 150, 30);
        background.add(passwordField);
        
        loginButton = new JButton("LOGIN");
        loginButton.setBounds(150, 170, 100, 30);
        background.add(loginButton);
        
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = new String(passwordField.getPassword());
                
                if (authenticateUser(username, password)) {
                    dispose();
                    new Dashboard(username);
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Username/Password");
                }
            }
        });
        
        setVisible(true);
    }
    
    public boolean authenticateUser(String username, String password) {
        boolean isAuthenticated = false;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "meow");
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                isAuthenticated = true;
            }
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isAuthenticated;
    }

    public static void main(String[] args) {
        new LoginPage();
    }
}
