package com.kenshin.teachers_record.controllers;

import com.kenshin.teachers_record.App;
import com.kenshin.teachers_record.configs.DbConfig;
import com.kenshin.teachers_record.models.User;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.springframework.security.crypto.bcrypt.BCrypt;

import javax.swing.*;
import java.io.IOException;
import java.sql.*;
import java.util.Objects;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private Label registerLbl;

    public void loginButtonClicked() throws SQLException, IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();

        User user = new User(username, password);

        Connection connection = DriverManager.getConnection(DbConfig.host+DbConfig.database, DbConfig.username, DbConfig.password);

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT password FROM user WHERE username = '" + username + "';");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            String hashedPassword = resultSet.getString("password");

            if (BCrypt.checkpw(user.getPassword(), hashedPassword)) {
                FXMLLoader fxmlLoader = new FXMLLoader((Objects.requireNonNull(App.class.getResource("views/dashboard-view.fxml"))));
                Scene scene = new Scene(fxmlLoader.load());
                App.primaryStage.setScene(scene);
            }
            else {
                JOptionPane.showMessageDialog(null, "Invalid Password!");
            }
        }

        connection.close();
    }
}