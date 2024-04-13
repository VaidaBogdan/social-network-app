package com.example.lab7gui.controller;

import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.service.Service;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterController {

    @FXML
    PasswordField registerPasswordField;
    @FXML
    TextField registerFirstNameField;
    @FXML
    TextField registerLastNameField;

    Service service;

    public void setService(Service service){
        this.service = service;
    }

    public void handleCancel(){
        Stage currentStage = (Stage) registerPasswordField.getScene().getWindow();
        currentStage.close();
    }

    public void handleRegister(){
        String nume1 = registerFirstNameField.getText();
        String nume2 = registerLastNameField.getText();
        String pass = registerPasswordField.getText();
        if(nume1.isBlank() || nume2.isBlank() || pass.isBlank()){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Add error", "Introduceti date in toate cele 3 campuri!!!");
        }
        else{
            Utilizator u = new Utilizator(nume1, nume2);
            u = service.addUserPasswd(u, pass);
            if(u.getId() == null){
                UserAction.showMessage(null, Alert.AlertType.WARNING, "Add error", "Mai incercati.");
            }
            else {
                UserAction.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Userul a fost adaugat");
                Stage currentStage = (Stage) registerPasswordField.getScene().getWindow();
                currentStage.close();
            }
        }
    }
}
