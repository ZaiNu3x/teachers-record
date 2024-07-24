module com.kenshin.teachers_record {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires spring.security.crypto;
    requires java.desktop;
    requires javafx.base;

    opens com.kenshin.teachers_record to javafx.fxml;
    opens com.kenshin.teachers_record.models to javafx.base;
    exports com.kenshin.teachers_record;
    exports com.kenshin.teachers_record.controllers;
    opens com.kenshin.teachers_record.controllers to javafx.fxml;
}