package com.kenshin.teachers_record.controllers;

import com.kenshin.teachers_record.configs.DbConfig;
import com.kenshin.teachers_record.models.Teacher;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import javax.swing.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    @FXML
    private TextField teacherIdField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextField emailField;
    @FXML
    private TableView<Teacher> teacherTableView;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        List<String> listOfFieldName = List.of("Id", "Name", "Subject", "Email");

        listOfFieldName.forEach(fieldName -> {
            TableColumn<Teacher, String> fieldColumn = new TableColumn<Teacher, String>(fieldName);
            fieldColumn.setStyle("-fx-font-size: 18");
            fieldColumn.setCellValueFactory(new PropertyValueFactory<>(fieldName));

            teacherTableView.getColumns().add(fieldColumn);
        });

        try {
            updateTeacherRecordList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        teacherTableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getClickCount() == 2) {
                Teacher selectedTeacher = teacherTableView.getSelectionModel().getSelectedItem();

                teacherIdField.setText(selectedTeacher.getId());
                nameField.setText(selectedTeacher.getName());
                subjectField.setText(selectedTeacher.getSubject());
                emailField.setText(selectedTeacher.getEmail());
            }
        });
    }

    public void saveButtonClicked() throws SQLException {
        Teacher teacher = new Teacher();

        teacher.setId(teacherIdField.getText());
        teacher.setName(nameField.getText());
        teacher.setSubject(subjectField.getText());
        teacher.setEmail(emailField.getText());

        Connection connection = DriverManager.getConnection(DbConfig.host + DbConfig.database, DbConfig.username, DbConfig.password);
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO teacher (id, name, subject, email) VALUE ('" + teacher.getId()
                + "','" + teacher.getName() + "','" + teacher.getSubject() + "','" + teacher.getEmail() + "');");

        stmt.execute();

        JOptionPane.showMessageDialog(null, "RECORD SAVED!");
        connection.close();

        updateTeacherRecordList();
    }

    public void editButtonClicked() throws SQLException {
        Teacher selectedTeacher = teacherTableView.getSelectionModel().getSelectedItem();
        Teacher teacher = new Teacher();

        teacher.setName(nameField.getText());
        teacher.setSubject(subjectField.getText());
        teacher.setEmail(emailField.getText());

        Connection connection = DriverManager.getConnection(DbConfig.host + DbConfig.database, DbConfig.username, DbConfig.password);
        PreparedStatement stmt = connection.prepareStatement("UPDATE teacher SET name = '" + teacher.getName()
                + "', subject = '" + teacher.getSubject() + "', email = '" + teacher.getEmail() + "' WHERE id = '" + selectedTeacher.getId() + "';");

        System.out.println(stmt.toString());

        stmt.execute();

        JOptionPane.showMessageDialog(null, "RECORD SAVED!");
        connection.close();

        updateTeacherRecordList();
    }

    public void deleteButtonClicked() throws SQLException {
        String selectedTeacherId = teacherTableView.getSelectionModel().getSelectedItem().getId();

        Connection connection = DriverManager.getConnection(DbConfig.host + DbConfig.database, DbConfig.username, DbConfig.password);
        PreparedStatement stmt = connection.prepareStatement("DELETE FROM teacher WHERE id = '" + selectedTeacherId + "';");
        stmt.execute();

        JOptionPane.showMessageDialog(null, "Record Deleted!");
        updateTeacherRecordList();
    }

    private void updateTeacherRecordList() throws SQLException {
        Connection connection = DriverManager.getConnection(DbConfig.host + DbConfig.database, DbConfig.username, DbConfig.password);
        PreparedStatement stmt = connection.prepareStatement("SELECT * FROM teacher");

        ResultSet resultSet = stmt.executeQuery();
        List<Teacher> teacherList = new ArrayList<>();

        while (resultSet.next()) {
            String id = resultSet.getString("id");
            String name = resultSet.getString("name");
            String subject = resultSet.getString("subject");
            String email = resultSet.getString("email");

            teacherList.add(new Teacher(id, name, subject, email));
        }

        connection.close();

        ObservableList<Teacher> observableList = FXCollections.observableList(teacherList);
        teacherTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        teacherTableView.setItems(observableList);
    }
}
