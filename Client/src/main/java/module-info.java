module com.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires CommonLibrary;


    opens com.client to javafx.fxml;
    exports com.client;
}