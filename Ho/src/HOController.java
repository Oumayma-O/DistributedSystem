import java.io.IOException;
import java.io.UnsupportedEncodingException;
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

    private java.sql.Connection dbconnection;
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

        // Consume messages from BO1 queue and insert into HO database
        Consumer consumer_1 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(message);
                String[] data = message.split(",");


                try {
                    Date date = (Date) properties.getHeaders().get("date");

                    String region = properties.getHeaders().get("region").toString();
                    String product = properties.getHeaders().get("product").toString();
                    int qty = Integer.parseInt(properties.getHeaders().get("qty").toString());
                    float cost = Float.parseFloat(properties.getHeaders().get("cost").toString());
                    double amt = Double.parseDouble(properties.getHeaders().get("amt").toString());
                    float tax = Float.parseFloat(properties.getHeaders().get("tax").toString());
                    double total = Double.parseDouble(properties.getHeaders().get("total").toString());

                    insertIntoHOStmt.setDate(1,date );
                    insertIntoHOStmt.setString(2, region);
                    insertIntoHOStmt.setString(3, product);
                    insertIntoHOStmt.setInt(4, qty);
                    insertIntoHOStmt.setFloat(5, cost);
                    insertIntoHOStmt.setDouble(6, amt);
                    insertIntoHOStmt.setFloat(7, tax);
                    insertIntoHOStmt.setDouble(8, total);
                    System.out.println(insertIntoHOStmt);
                    insertIntoHOStmt.executeUpdate();

                    insertIntoHOStmt.executeUpdate();
                    insertIntoHOStmt.close();
                    System.out.println(message);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };


        // Consume messages from BO2 queue and insert into HO database
        Consumer consumer_2 = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String message = new String(body, "UTF-8");
                System.out.println(message);
                String[] data = message.split(",");


                try {
                    Date date = (Date) properties.getHeaders().get("date");

                    String region = properties.getHeaders().get("region").toString();
                    String product = properties.getHeaders().get("product").toString();
                    int qty = Integer.parseInt(properties.getHeaders().get("qty").toString());
                    float cost = Float.parseFloat(properties.getHeaders().get("cost").toString());
                    double amt = Double.parseDouble(properties.getHeaders().get("amt").toString());
                    float tax = Float.parseFloat(properties.getHeaders().get("tax").toString());
                    double total = Double.parseDouble(properties.getHeaders().get("total").toString());

                    insertIntoHOStmt.setDate(1,date );
                    insertIntoHOStmt.setString(2, region);
                    insertIntoHOStmt.setString(3, product);
                    insertIntoHOStmt.setInt(4, qty);
                    insertIntoHOStmt.setFloat(5, cost);
                    insertIntoHOStmt.setDouble(6, amt);
                    insertIntoHOStmt.setFloat(7, tax);
                    insertIntoHOStmt.setDouble(8, total);
                    insertIntoHOStmt.executeUpdate();

                    insertIntoHOStmt.executeUpdate();
                    insertIntoHOStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        channel.basicConsume(BO1_QUEUE_NAME, true, consumer_1);
        channel.basicConsume(BO2_QUEUE_NAME, true, consumer_2);

        // Keep main thread alive
        /*while (true) {
            Thread.sleep(1000);
        }*/
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
            this.dbconnection  = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            insertIntoHOStmt = this.dbconnection.prepareStatement("INSERT INTO `product sales` (date, region, product, qty, cost, amt, tax, total) VALUES (?,?,?,?,?,?,?,?)");


        }


    public void insertSale(Date date ,String region, String product, int qty, float cost, double amt, float tax, double total) throws SQLException {
        // Insert sale into HO database
        insertIntoHOStmt.setDate(1, date );
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
        dbconnection.close();
    }
}
