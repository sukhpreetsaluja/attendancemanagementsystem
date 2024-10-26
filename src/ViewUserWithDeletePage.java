package src;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ViewUserWithDeletePage extends JFrame {

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton deleteButton;

    private Connection conn;

    public ViewUserWithDeletePage() {
        setTitle("View and Delete Users");
        setSize(600, 400);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Employee ID");
        tableModel.addColumn("Gender");
        tableModel.addColumn("Email");
        tableModel.addColumn("Contact");

        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        JPanel searchPanel = new JPanel();
        searchField = new JTextField(20);
        searchButton = new JButton("Search by Email");
        deleteButton = new JButton("Delete Selected");

        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(deleteButton);

        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        fetchUserData("");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = searchField.getText();
                fetchUserData(email);
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a row to delete!");
                } else {
                    int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this record?", "Delete Confirmation", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        int id = (int) tableModel.getValueAt(selectedRow, 1);
                        deleteUser(id);
                        tableModel.removeRow(selectedRow);
                    }
                }
            }
        });
        setVisible(true);
    }
    private void fetchUserData(String emailFilter) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "meow");

            String query = "SELECT * FROM employees";
            if (!emailFilter.isEmpty()) {
                query += " WHERE email LIKE ?";
            }

            PreparedStatement statement = conn.prepareStatement(query);
            if (!emailFilter.isEmpty()) {
                statement.setString(1, "%" + emailFilter + "%");
            }

            ResultSet rs = statement.executeQuery();
            tableModel.setRowCount(0);

            while (rs.next()) {
                String name = rs.getString("name");
                int employeeId = rs.getInt("employee_id");
                String gender = rs.getString("gender");
                String email = rs.getString("email");
                String contact = rs.getString("contact");

                tableModel.addRow(new Object[]{name, employeeId, gender, email, contact});
            }

            rs.close();
            statement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void deleteUser(int id) {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/attendance", "root", "meow");

            String query = "DELETE FROM employees WHERE employee_id = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setInt(1, id);

            int rowsDeleted = preparedStatement.executeUpdate();
            if (rowsDeleted > 0) {
                JOptionPane.showMessageDialog(null, "User deleted successfully!");
            }

            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new ViewUserWithDeletePage();
    }
}

