package src;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Vector;

public class GenerateQRPage extends JFrame {
    private JTable userTable;
    private JButton generateQRButton, saveQRButton;
    private JLabel qrCodeLabel;
    private BufferedImage qrCodeImage;
    private Connection conn;

    public GenerateQRPage() {
        setTitle("Generate QR Code");
        setSize(900, 600);
        setLayout(new BorderLayout());

        JPanel tablePanel = new JPanel(new BorderLayout());
        JPanel qrPanel = new JPanel(new BorderLayout());

        DefaultTableModel model = new DefaultTableModel();
        userTable = new JTable(model);
        model.addColumn("Employee ID");
        model.addColumn("Name");
        model.addColumn("Email");
        model.addColumn("Contact");

        JScrollPane scrollPane = new JScrollPane(userTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        loadUsers();

        generateQRButton = new JButton("Generate QR");
        tablePanel.add(generateQRButton, BorderLayout.SOUTH);

        qrCodeLabel = new JLabel();
        qrCodeLabel.setHorizontalAlignment(JLabel.CENTER);
        qrPanel.add(qrCodeLabel, BorderLayout.CENTER);

        add(tablePanel, BorderLayout.WEST);
        add(qrPanel, BorderLayout.CENTER);

        saveQRButton = new JButton("Save QR Code");
        saveQRButton.setEnabled(false);
        qrPanel.add(saveQRButton, BorderLayout.SOUTH);

        generateQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = userTable.getSelectedRow();
                if (selectedRow != -1) {
                    String employeeID = (String) userTable.getValueAt(selectedRow, 0);
                    String name = (String) userTable.getValueAt(selectedRow, 1);
                    String email = (String) userTable.getValueAt(selectedRow, 2);
                    String contact = (String) userTable.getValueAt(selectedRow, 3);

                    String qrData = "Employee ID: " + employeeID + "\n"
                                  + "Name: " + name + "\n"
                                  + "Email: " + email + "\n"
                                  + "Contact: " + contact;

                    qrCodeImage = generateQRCode(qrData);

                    if (qrCodeImage != null) {
                        qrCodeLabel.setIcon(new ImageIcon(qrCodeImage));
                        saveQRButton.setEnabled(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to generate QR code");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Please select a user");
                }
            }
        });

        saveQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveQRCodeToFile();
            }
        });

        setVisible(true);
    }

    private void loadUsers() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "meow");

            String query = "SELECT employee_id, name, email, contact FROM employees";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            DefaultTableModel model = (DefaultTableModel) userTable.getModel();
            model.setRowCount(0); 

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("employee_id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("contact"));
                model.addRow(row);
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage generateQRCode(String data) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 250, 250);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveQRCodeToFile() {
        if (qrCodeImage != null) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Save QR Code");

            fileChooser.setSelectedFile(new File("QRCode.png"));

            int userSelection = fileChooser.showSaveDialog(this);
            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File fileToSave = fileChooser.getSelectedFile();
                try {
                    ImageIO.write(qrCodeImage, "png", fileToSave);
                    JOptionPane.showMessageDialog(this, "QR code saved successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No QR code generated to save.");
        }
    }

    public static void main(String[] args) {
        new GenerateQRPage();
    }
}