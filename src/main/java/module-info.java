module com.example.pfm {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.pfm to javafx.fxml;
    exports com.example.pfm;
}