package com.example.lab7gui.controller;

import javafx.scene.control.Alert;
import javafx.stage.Stage;

public class UserAction {
    public static void showMessage(Stage stage, Alert.AlertType alertType, String title, String message) {
        Alert userAlert = new Alert(alertType);
        userAlert.initOwner(stage);
        userAlert.setContentText(message);
        userAlert.setTitle(title);
        userAlert.showAndWait();
    }
}
