package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrderManagementApp extends JFrame implements ActionListener {

    private JTextField orderNumberTextField;
    private JTextField maxAmountTextField;
    private JTextField distinctProductsTextField;
    private JTextField searchProductTextField;
    private JTextField excludeProductTextField;
    private JTextField deleteQuantityTextField;
    private JTextArea resultTextArea;
    private JTextField deleteProductNameTextField;

    private Connection connection;

    public OrderManagementApp() {
        super("Order Management App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 400);
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


        JPanel inputPanel = new JPanel();
        JLabel orderNumberLabel = new JLabel("Номер заказа:");
        orderNumberTextField = new JTextField(10);
        JButton getOrderInfoButton = new JButton("Получить информацию о заказе");
        getOrderInfoButton.addActionListener(this);
        inputPanel.add(orderNumberLabel);
        inputPanel.add(orderNumberTextField);
        inputPanel.add(getOrderInfoButton);


        JPanel maxAmountPanel = new JPanel();
        JLabel maxAmountLabel = new JLabel("Максимальная сумма:");
        maxAmountTextField = new JTextField(10);
        JLabel distinctProductsLabel = new JLabel("Количество различных товаров:");
        distinctProductsTextField = new JTextField(10);
        JButton getOrdersByAmount = new JButton("Получить номера заказов");
        getOrdersByAmount.addActionListener(this);
        maxAmountPanel.add(maxAmountLabel);
        maxAmountPanel.add(maxAmountTextField);
        maxAmountPanel.add(distinctProductsLabel);
        maxAmountPanel.add(distinctProductsTextField);
        maxAmountPanel.add(getOrdersByAmount);


        JPanel searchProductPanel = new JPanel();
        JLabel searchProductLabel = new JLabel("Товар:");
        searchProductTextField = new JTextField(10);
        JButton getOrdersByProductButton = new JButton("Получить номера заказов содержащих заданный товар");
        getOrdersByProductButton.addActionListener(this);
        searchProductPanel.add(searchProductLabel);
        searchProductPanel.add(searchProductTextField);
        searchProductPanel.add(getOrdersByProductButton);


        JPanel excludeProductPanel = new JPanel();
        JLabel excludeProductLabel = new JLabel("Товар:");
        excludeProductTextField = new JTextField(10);
        JButton getOrdersWithoutProductButton = new JButton("Получить номера заказов не содержащих заданный товар");
        getOrdersWithoutProductButton.addActionListener(this);
        excludeProductPanel.add(excludeProductLabel);
        excludeProductPanel.add(excludeProductTextField);
        excludeProductPanel.add(getOrdersWithoutProductButton);


        JPanel newOrderPanel = new JPanel();
        JButton createNewOrderButton = new JButton("Сформировать новый заказ");
        createNewOrderButton.addActionListener(this);
        newOrderPanel.add(createNewOrderButton);

        JPanel deleteOrdersPanel = new JPanel();
        JLabel deleteQuantityLabel = new JLabel("Количество:");
        deleteQuantityTextField = new JTextField(10);
        JLabel deleteProductNameLabel = new JLabel("Название:");
        deleteProductNameTextField = new JTextField(10);
        JButton deleteOrdersButton = new JButton("Удалить заказы");
        deleteOrdersButton.addActionListener(this);
        deleteOrdersPanel.add(deleteQuantityLabel);
        deleteOrdersPanel.add(deleteQuantityTextField);
        deleteOrdersPanel.add(deleteProductNameLabel);
        deleteOrdersPanel.add(deleteProductNameTextField);
        deleteOrdersPanel.add(deleteOrdersButton);


        resultTextArea = new JTextArea(8, 50);
        resultTextArea.setEditable(false);
        JPanel textAreaPanel = new JPanel();
        textAreaPanel.add(resultTextArea);


        add(inputPanel);
        add(maxAmountPanel);
        add(searchProductPanel);
        add(excludeProductPanel);
        add(deleteOrdersPanel);
        add(newOrderPanel);
        add(textAreaPanel);
        setVisible(true);

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/orders_db", "root", "Dimach321");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при подключении к базе данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new OrderManagementApp();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Получить информацию о заказе")) {
            String orderNumber = orderNumberTextField.getText();
            getOrderInformation(orderNumber);
        } else  if (e.getActionCommand().equals("Получить номера заказов")) {
            String maxAmount = maxAmountTextField.getText();
            String distinctProducts = distinctProductsTextField.getText();
            getOrdersByAmount(maxAmount, distinctProducts);
        } else  if (e.getActionCommand().equals("Получить номера заказов содержащих заданный товар")) {
            String product = searchProductTextField.getText();
            getOrdersByProduct(product);
        } else  if (e.getActionCommand().equals("Получить номера заказов не содержащих заданный товар")) {
            String product = excludeProductTextField.getText();
            getOrdersWithoutProduct(product);
        } else  if (e.getActionCommand().equals("Удалить заказы")) {
            String quantity = deleteQuantityTextField.getText();
            String product = deleteProductNameTextField.getText();
            deleteOrders(quantity, product);
        } else  if (e.getActionCommand().equals("Сформировать новый заказ")) {
            NewOrderGui newOrderGui = new NewOrderGui();
        }
    }

    private void getOrderInformation(String orderNumber) {
        try {

            PreparedStatement orderStatement = connection.prepareStatement("SELECT * FROM orders WHERE order_number = ?");
            orderStatement.setString(1, orderNumber);
            ResultSet orderResultSet = orderStatement.executeQuery();

            ResultSet orderItemsResultSet = null;
            PreparedStatement orderItemsStatement = null;
            if (orderResultSet.next()) {
                int orderId = orderResultSet.getInt("order_id");
                LocalDate orderDate = orderResultSet.getDate("order_date").toLocalDate();


                orderItemsStatement = connection.prepareStatement("SELECT * FROM order_items WHERE order_id = ?");
                orderItemsStatement.setInt(1, orderId);
                orderItemsResultSet = orderItemsStatement.executeQuery();

                List<String> orderItems = new ArrayList<>();
                while (orderItemsResultSet.next()) {
                    int productId = orderItemsResultSet.getInt("product_id");
                    int quantity = orderItemsResultSet.getInt("quantity");


                    PreparedStatement productStatement = connection.prepareStatement("SELECT * FROM products WHERE product_id = ?");
                    productStatement.setInt(1, productId);
                    ResultSet productResultSet = productStatement.executeQuery();

                    if (productResultSet.next()) {
                        String productName = productResultSet.getString("product_name");
                        String productDescription = productResultSet.getString("product_description");
                        double productPrice = productResultSet.getDouble("product_price");

                        orderItems.add("Товар: " + productName + "\nОписание: " + productDescription +
                                "\nЦена: " + productPrice + "\nКоличество: " + quantity + "\n");
                    }

                    productResultSet.close();
                    productStatement.close();
                }


                resultTextArea.setText("Номер заказа: " + orderNumber + "\nДата поступления: " + orderDate +
                        "\n\nТовары в заказе:\n" + String.join("", orderItems));
            } else {
                JOptionPane.showMessageDialog(this, "Ошибка при получения информации о заказе.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }

            orderItemsResultSet.close();
            orderItemsStatement.close();
            orderResultSet.close();
            orderStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при получения информации о заказе.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getOrdersByAmount(String maxAmount, String distinctProducts) {
        try {
            PreparedStatement query = connection.prepareStatement("SELECT ord.order_number\n" +
                    "FROM orders ord\n" +
                    "JOIN order_items orditem ON ord.order_id = orditem.order_id\n" +
                    "JOIN products prod ON orditem.product_id = prod.product_id\n" +
                    "GROUP BY ord.order_number\n" +
                    "HAVING SUM(prod.product_price * orditem.quantity) >= (?)\n" +
                    "   AND SUM(orditem.quantity) = (?);");

            query.setString(1, maxAmount);
            query.setString(2, distinctProducts);
            ResultSet resultSet = query.executeQuery();

            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("Максимальная сумма: ").append(maxAmount).append("\nКоличество различных товаров: ").append(distinctProducts).append("\n\nНомера заказов:\n");

            while (resultSet.next()) {
                String orderNumber = resultSet.getString("order_number");
                resultBuilder.append(orderNumber).append("\n");
            }

            resultTextArea.setText(resultBuilder.toString());

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при получении номеров заказов.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getOrdersByProduct(String searchProduct) {
        try {
            PreparedStatement query = connection.prepareStatement("SELECT ord.order_number\n" +
                    "FROM orders ord\n" +
                    "JOIN order_items orditem ON ord.order_id = orditem.order_id\n" +
                    "JOIN products prod ON orditem.product_id = prod.product_id\n" +
                    "WHERE prod.product_name = (?);");

            query.setString(1, searchProduct);
            ResultSet resultSet = query.executeQuery();


            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("С товаром: ").append(searchProduct).append("\n\nНомера заказов:\n");


            while (resultSet.next()) {
                String orderNumber = resultSet.getString("order_number");
                resultBuilder.append(orderNumber).append("\n");
            }

            resultTextArea.setText(resultBuilder.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при получении номеров заказов.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void getOrdersWithoutProduct(String excludeProduct) {
        try {
            PreparedStatement query = connection.prepareStatement("SELECT ord.order_number\n" +
                    "FROM orders ord\n" +
                    "JOIN order_items orditem ON ord.order_id = orditem.order_id\n" +
                    "JOIN products products ON orditem.product_id = products.product_id\n" +
                    "WHERE products.product_name != (?)\n" +
                    "   AND DATE(ord.order_date) = CURDATE();");

            query.setString(1, excludeProduct);
            ResultSet resultSet = query.executeQuery();

            StringBuilder resultBuilder = new StringBuilder();
            resultBuilder.append("Без товара: ").append(excludeProduct).append("\n\nНомера заказов:\n");

            while (resultSet.next()) {
                String orderNumber = resultSet.getString("order_number");
                resultBuilder.append(orderNumber).append("\n");
            }

            resultTextArea.setText(resultBuilder.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при получении номеров заказов.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOrders(String deleteQuantity, String product) {
        try {
            PreparedStatement tempTableQuery = connection.prepareStatement("CREATE TEMPORARY TABLE temp_order_ids AS\n" +
                    "SELECT oi.order_id\n" +
                    "FROM order_items oi\n" +
                    "JOIN products p ON oi.product_id = p.product_id\n" +
                    "WHERE p.product_name = ? AND oi.quantity = ?");

            tempTableQuery.setString(1, product);
            tempTableQuery.setString(2, deleteQuantity);
            tempTableQuery.executeUpdate();

            PreparedStatement deleteOrderItemsQuery = connection.prepareStatement("DELETE FROM order_items\n" +
                    "WHERE order_id IN (SELECT order_id FROM temp_order_ids)");

            deleteOrderItemsQuery.executeUpdate();

            PreparedStatement deleteOrdersQuery = connection.prepareStatement("DELETE FROM orders\n" +
                    "WHERE order_id IN (SELECT order_id FROM temp_order_ids)");

            deleteOrdersQuery.executeUpdate();

            PreparedStatement dropTempTableQuery = connection.prepareStatement("DROP TEMPORARY TABLE IF EXISTS temp_order_ids");
            dropTempTableQuery.executeUpdate();

            JOptionPane.showMessageDialog(this, "Удалено.", "Успех", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при удалении заказов.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
