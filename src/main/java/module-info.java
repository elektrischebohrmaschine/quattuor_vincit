module fhtw.quattuor.client {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.fasterxml.jackson.databind;

    opens fhtw.quattuor.client to javafx.fxml;
    exports fhtw.quattuor.client;
}