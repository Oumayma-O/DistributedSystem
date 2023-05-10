import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class HOView extends JFrame {

    private static final long serialVersionUID = 1L;
    private HOController HOController;
    private JTable table;

    public HOView() {
        this.HOController = new HOController();

        // Set up the frame
        setTitle("HO Sync");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Add a button to start the sync
        JButton syncButton = new JButton("Start Sync");
        syncButton.addActionListener(e -> {
            try {
                HOController.startSync();
                displayTable();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error syncing data: " + ex.getMessage(), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
        add(syncButton, BorderLayout.NORTH);

        // Add a table to display the recently added records
        table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Display the table
        displayTable();
    }

    private void displayTable() {
        try {
            // Connect to the HO database
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = null;
            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ho", "root", "");

                // Retrieve the recently added records
                PreparedStatement statement = connection.prepareStatement(
                        "SELECT * FROM `product sales` WHERE Product = ? ORDER BY date DESC LIMIT 10");
                statement.setString(1, "Product");
                ResultSet resultSet = statement.executeQuery();

                // Create a table model and add the records to it
                DefaultTableModel model = new DefaultTableModel();
                model.addColumn("Date");
                model.addColumn("Region");
                model.addColumn("Product");
                model.addColumn("Qty");
                model.addColumn("Cost");
                model.addColumn("Amt");
                model.addColumn("Tax");
                model.addColumn("Total");
                while (resultSet.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(resultSet.getDate("date"));
                    row.add(resultSet.getString("region"));
                    row.add(resultSet.getString("product"));
                    row.add(resultSet.getInt("qty"));
                    row.add(resultSet.getFloat("cost"));
                    row.add(resultSet.getDouble("amt"));
                    row.add(resultSet.getFloat("tax"));
                    row.add(resultSet.getDouble("total"));
                    model.addRow(row);
                }
                table.setModel(model);
            } finally {
                // Close the connection
                if (connection != null) {
                    connection.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving data: " + e.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        HOView view = new HOView();
        view.setVisible(true);
    }
}
