import java.sql.*;

import com.rabbitmq.client.*;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;


public class HOController {

    private static final String EXCHANGE_NAME = "bo_to_ho_exchange";
    private static final String BO1_QUEUE_NAME = "bo1_queue";
    private static final String BO2_QUEUE_NAME = "bo2_queue";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ho";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private Connection rabbitmqConnection;
    private Channel channel;
    private PreparedStatement insertIntoHOStmt;

    public HOController() {
        try {
            connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startSync() throws Exception {
        // Consume messages from BO1 queue
        channel.basicConsume(BO1_QUEUE_NAME, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

                String region = delivery.getProperties().getHeaders().get("region").toString();
                String product = delivery.getProperties().getHeaders().get("product").toString();
                int qty = Integer.parseInt(delivery.getProperties().getHeaders().get("qty").toString());
                float cost = Float.parseFloat(delivery.getProperties().getHeaders().get("cost").toString());
                double amt = Double.parseDouble(delivery.getProperties().getHeaders().get("amt").toString());
                float tax = Float.parseFloat(delivery.getProperties().getHeaders().get("tax").toString());
                double total = Double.parseDouble(delivery.getProperties().getHeaders().get("total").toString());
                try {
                    insertSale( region, product, qty, cost, amt, tax, total);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

        }, consumerTag -> {});

        // Consume messages from BO2 queue
        channel.basicConsume(BO2_QUEUE_NAME, true, (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");

                String region = delivery.getProperties().getHeaders().get("region").toString();
                String product = delivery.getProperties().getHeaders().get("product").toString();
                int qty = Integer.parseInt(delivery.getProperties().getHeaders().get("qty").toString());
                float cost = Float.parseFloat(delivery.getProperties().getHeaders().get("cost").toString());
                double amt = Double.parseDouble(delivery.getProperties().getHeaders().get("amt").toString());
                float tax = Float.parseFloat(delivery.getProperties().getHeaders().get("tax").toString());
                double total = Double.parseDouble(delivery.getProperties().getHeaders().get("total").toString());
                try {
                    insertSale( region, product, qty, cost, amt, tax, total);
                } catch (SQLException e) {
                    e.printStackTrace();
                }


        }, consumerTag -> {});
    }


        public void connect() throws Exception {
            // Connect to RabbitMQ
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            com.rabbitmq.client.Connection connection = factory.newConnection();

            this.channel = connection.createChannel();

            // make queues durable
            boolean durable = true;
            this.channel.queueDeclare(BO1_QUEUE_NAME , durable, false, false, null);
            this.channel.queueDeclare(BO2_QUEUE_NAME , durable, false, false, null);

            // Connect to MySQL database
            // Set up MySQL database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection dbConnection = (Connection) DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/ho", "root", "");
            insertIntoHOStmt = ((java.sql.Connection) dbConnection).prepareStatement("INSERT INTO `product sales` (date, region, product, qty, cost, amt, tax, total) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        }


    public void insertSale(String region, String product, int qty, float cost, double amt, float tax, double total) throws SQLException {
        // Insert sale into HO database
        insertIntoHOStmt.setDate(1, new Date(System.currentTimeMillis()));
        insertIntoHOStmt.setString(2, region);
        insertIntoHOStmt.setString(3, product);
        insertIntoHOStmt.setInt(4, qty);
        insertIntoHOStmt.setFloat(5, cost);
        insertIntoHOStmt.setDouble(6, amt);
        insertIntoHOStmt.setFloat(7, tax);
        insertIntoHOStmt.setDouble(8, total);
        insertIntoHOStmt.executeUpdate();
    }


    public void close() throws Exception {
        rabbitmqConnection.close();
    }
}
