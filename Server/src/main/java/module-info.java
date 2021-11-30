module com.server {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires CommonLibrary;


    opens com.server to javafx.fxml;
    opens com.server.datasource to javafx.fxml;

    exports com.server;
    exports com.server.datasource;
}