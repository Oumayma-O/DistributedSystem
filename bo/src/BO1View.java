import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class BO1View {
    private BO1Controller boController;
    private DefaultTableModel tableModel;
    private JTable table;

    public BO1View() throws Exception {
        boController = new BO1Controller();
        createGUI();
    }

    private void createGUI() {
        JFrame frame = new JFrame("BO1 View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        tableModel = new DefaultTableModel(new String[]{"Id","Date", "Region", "Product", "Qty", "Cost", "Amt", "Tax", "Total", "Sent"}, 0);
        table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JButton syncButton = new JButton("Sync");
        syncButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    boController.syncSales();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        panel.add(syncButton, BorderLayout.SOUTH);

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    public void updateTable() {
        try {
            List<ProductSale> unsentSales = boController.getUnsentSales();
            tableModel.setRowCount(0);
            for (ProductSale sale : unsentSales) {
                Object[] row = new Object[]{ sale.getId(),sale.getDate(), sale.getRegion(), sale.getProduct(), sale.getQty(), sale.getCost(), sale.getAmt(), sale.getTax(), sale.getTotal(), sale.isSent()};
                tableModel.addRow(row);
            }
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        BO1View view = new BO1View();
        view.updateTable();
    }
}
