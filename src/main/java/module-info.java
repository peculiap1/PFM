module com.example.pfm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.pfm to javafx.fxml;
    exports com.example.pfm;
    exports com.example.pfm.dao;
    opens com.example.pfm.dao to javafx.fxml;
    exports com.example.pfm.model;
    opens com.example.pfm.model to javafx.fxml;
    exports com.example.pfm.util;
    opens com.example.pfm.util to javafx.fxml;
}