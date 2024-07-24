package com.kenshin.teachers_record;

import com.kenshin.teachers_record.configs.DbConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;

public class App extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException, SQLException {
        initializeDatabase();

        primaryStage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("views/login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Teacher's Information System");
        primaryStage.setResizable(false);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    private CompletableFuture<Void> initializeDatabase() throws SQLException {
        return CompletableFuture.runAsync(() -> {
            try {
                Connection conn = DriverManager.getConnection(DbConfig.host, DbConfig.username, DbConfig.password);
                Statement stmt = conn.createStatement();

                String query = "CREATE DATABASE IF NOT EXISTS " + DbConfig.database + ";";
                stmt.execute(query);
                conn.close();

                conn = DriverManager.getConnection(DbConfig.host + DbConfig.database+"?allowMultiQueries=true", DbConfig.username, DbConfig.password);
                stmt = conn.createStatement();

                query = """
                        DROP TABLE IF EXISTS user;
                        DROP TABLE IF EXISTS teacher;
                        
                        CREATE TABLE user (
                            username VARCHAR(25),
                            password VARCHAR(72) NOT NULL,
                            PRIMARY KEY(username)
                        );
                        
                        CREATE TABLE teacher (
                            id VARCHAR(25),
                            name VARCHAR(25) NOT NULL,
                            subject VARCHAR(25) NOT NULL,
                            email VARCHAR(25) NOT NULL UNIQUE,
                            PRIMARY KEY(id)
                        );
                        
                        INSERT INTO user VALUE ('zainu3x', '$2a$12$cf7qB5FxJ95LcIcYp7J2Ke994YorsRGsdCRyIO9A5faroCEZS7H06');
                        
                        """.replaceAll("(?m)^\\s+", "")
                        .replaceAll("[\\n\\t\\r]", "");

                stmt.execute(query);

                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}