package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NewOrderGui extends JFrame implements ActionListener {

    private Connection connection;

    public NewOrderGui() {
        JFrame frame = new JFrame("Новый заказ");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(900, 400);

        JPanel panel = new JPanel();

        JTextField orderNumberField = new JTextField(10);
        JTextField orderDateField = new JTextField(10);
        JTextField productNameField = new JTextField(10);
        JTextField productDescriptionField = new JTextField(10);
        JTextField productPriceField = new JTextField(10);
        JTextField quantityField = new JTextField(10);
        JButton createOrderButton = new JButton("Создать заказ");

        panel.add(new JLabel("Номер заказа:"), BorderLayout.WEST);
        panel.add(orderNumberField, BorderLayout.CENTER);
        panel.add(new JLabel("Дата заказа:"), BorderLayout.WEST);
        panel.add(orderDateField, BorderLayout.CENTER);
        panel.add(new JLabel("Название товара:"), BorderLayout.WEST);
        panel.add(productNameField, BorderLayout.CENTER);
        panel.add(new JLabel("Описание товара:"), BorderLayout.WEST);
        panel.add(productDescriptionField, BorderLayout.CENTER);
        panel.add(new JLabel("Цена товара:"), BorderLayout.WEST);
        panel.add(productPriceField, BorderLayout.CENTER);
        panel.add(new JLabel("Количество:"), BorderLayout.WEST);
        panel.add(quantityField, BorderLayout.CENTER);
        panel.add(createOrderButton, BorderLayout.PAGE_END);


        createOrderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String orderNumber = orderNumberField.getText();
                String orderDate = orderDateField.getText();
                String productName = productNameField.getText();
                String productDescription = productDescriptionField.getText();
                String productPrice = productPriceField.getText();
                String quantity = quantityField.getText();
                if (createOrder(orderNumber, orderDate, productName, productDescription, productPrice, quantity)) {
                    JOptionPane.showMessageDialog(frame, "Новый заказ успешно создан.", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    frame.dispose();
                } else {
                    JOptionPane.showMessageDialog(frame, "Ошибка при создании нового заказа.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(createOrderButton);
        frame.getContentPane().add(panel);

        frame.setVisible(true);
    }

    private boolean createOrder(String orderNumber, String orderDate, String productName, String productDescription, String productPrice, String quantity) {

        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/orders_db", "root", "Dimach321");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Ошибка при подключении к базе данных.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }

        try {
            Statement statement = connection.createStatement();

            String insertOrderQuery = "INSERT INTO orders (order_number, order_date) VALUES (?, ?)";
            try (PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderQuery)) {
                insertOrderStmt.setString(1, orderNumber);
                insertOrderStmt.setString(2, orderDate);
                insertOrderStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            int orderId;
            String selectOrderIdQuery = "SELECT LAST_INSERT_ID()";
            try (Statement selectOrderIdStmt = connection.createStatement();
                 ResultSet resultSet = selectOrderIdStmt.executeQuery(selectOrderIdQuery)) {
                resultSet.next();
                orderId = resultSet.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            String insertProductQuery = "INSERT INTO products (product_name, product_description, product_price) VALUES (?, ?, ?)";
            try (PreparedStatement insertProductStmt = connection.prepareStatement(insertProductQuery)) {
                insertProductStmt.setString(1, productName);
                insertProductStmt.setString(2, productDescription);
                insertProductStmt.setString(3, productPrice);
                insertProductStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            int productId;
            String selectProductIdQuery = "SELECT LAST_INSERT_ID()";
            try (Statement selectProductIdStmt = connection.createStatement();
                 ResultSet resultSet = selectProductIdStmt.executeQuery(selectProductIdQuery)) {
                resultSet.next();
                productId = resultSet.getInt(1);
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            String insertOrderItemQuery = "INSERT INTO order_items (order_id, product_id, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement insertOrderItemStmt = connection.prepareStatement(insertOrderItemQuery)) {
                insertOrderItemStmt.setInt(1, orderId);
                insertOrderItemStmt.setInt(2, productId);
                insertOrderItemStmt.setString(3, quantity);
                insertOrderItemStmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }

            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
