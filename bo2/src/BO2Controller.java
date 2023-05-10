import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;

public class BO2Controller {
    private static final String BO_QUEUE_NAME = "bo2_queue";
    private static final String HO_EXCHANGE_NAME = "bo_to_ho_exchange";
    private static final String HO_ROUTING_KEY = "bo2_to_ho_queue";


    private Connection dbConnection;
    private Channel mqChannel;

    public BO2Controller() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        com.rabbitmq.client.Connection connection = factory.newConnection();
        this.mqChannel = connection.createChannel();
        // Make queue persistent
        boolean durable = true;
        mqChannel.queueDeclare(BO_QUEUE_NAME, durable, false, false, null);
    }



    public void syncSales() throws Exception {
        List<ProductSale> unsentSales = getUnsentSales();
        for (ProductSale sale : unsentSales) {
            mqChannel.basicPublish(HO_EXCHANGE_NAME, HO_ROUTING_KEY, MessageProperties.PERSISTENT_BASIC, sale.toString().getBytes());
            markSaleAsSent(sale.getId());
        }
    }

    public List<ProductSale> getUnsentSales() throws SQLException, ClassNotFoundException {
        // Set up MySQL database connection
        Class.forName("com.mysql.cj.jdbc.Driver");
        this.dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bo2", "root", "");

        List<ProductSale> unsentSales = new ArrayList<ProductSale>();
        PreparedStatement stmt = dbConnection.prepareStatement("SELECT * FROM `product sales` WHERE sent = 0");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            ProductSale sale = new ProductSale();

            sale.setId(rs.getInt("Id"));
            sale.setDate(Date.valueOf(rs.getString("date")));
            sale.setRegion(rs.getString("region"));
            sale.setProduct(rs.getString("product"));
            sale.setQty(rs.getInt("qty"));
            sale.setCost(rs.getFloat("cost"));
            sale.setAmt(rs.getDouble("amt"));
            sale.setTax(rs.getFloat("tax"));
            sale.setTotal(rs.getDouble("total"));
            sale.setSent(rs.getBoolean("sent"));
            unsentSales.add(sale);
        }
        rs.close();
        stmt.close();
        return unsentSales;
    }

    public void markSaleAsSent(int id) throws SQLException, ClassNotFoundException {
        // Set up MySQL database connection
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/bo2", "root", "");
        PreparedStatement stmt = dbConnection.prepareStatement("UPDATE `product sales` SET sent = 1 WHERE id = ?");
        stmt.setInt(1, id);
        stmt.executeUpdate();
        stmt.close();
    }


}
