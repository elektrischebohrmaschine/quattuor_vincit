module fhtw.quattuor.client {
    requires javafx.controls;
    requires javafx.fxml;


    opens fhtw.quattuor.client to javafx.fxml;
    exports fhtw.quattuor.client;
}