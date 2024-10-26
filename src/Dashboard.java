package src;
import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Dashboard extends JFrame {

    public Dashboard(String username) {
        setTitle("Dashboard");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        JLabel welcomeLabel = new JLabel("Welcome back, " + username + "!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JMenuBar menuBar = new JMenuBar();
        
        JMenu userMenu = new JMenu("User");
        JMenuItem registerUser = new JMenuItem("Register User");
        JMenuItem viewUser = new JMenuItem("View User");
        JMenuItem updateUser = new JMenuItem("Update User");
        JMenuItem deleteUser = new JMenuItem("Delete User");

        userMenu.add(registerUser);
        userMenu.add(viewUser);
        userMenu.add(updateUser);
        userMenu.add(deleteUser);

        registerUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RegisterPage();
            }
        });

        viewUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewUserPage();
            }
        });

        deleteUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ViewUserWithDeletePage();
            }
        });
        
        updateUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new UpdateUser();
            }
        });
    
        JMenu qrMenu = new JMenu("View QR");
        qrMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                new GenerateQRPage();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        JMenu attendanceMenu = new JMenu("Mark Attendance");
        
        attendanceMenu.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                new QRScan();
            }

            @Override
            public void menuDeselected(MenuEvent e) {
            }

            @Override
            public void menuCanceled(MenuEvent e) {
            }
        });

        JMenu adminMenu = new JMenu("Admin");
        JMenuItem addAdmin = new JMenuItem("Add Admin");
        JMenuItem removeAdmin = new JMenuItem("Remove Admin");

        addAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddAdminPage();
            }
        });

        removeAdmin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new RemoveAdminPage();
            }
        });

        adminMenu.add(addAdmin);
        adminMenu.add(removeAdmin);
    
        menuBar.add(userMenu);
        menuBar.add(qrMenu);
        menuBar.add(attendanceMenu);
        menuBar.add(adminMenu);

        setJMenuBar(menuBar);

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(welcomeLabel, BorderLayout.CENTER);
        add(panel);
        setVisible(true);
    }

    public static void main(String[] args) {
        new Dashboard("tester");
    }
    
}
