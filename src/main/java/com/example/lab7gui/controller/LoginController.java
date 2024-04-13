package com.example.lab7gui.controller;

import com.example.lab7gui.SocialNetworkApplication;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.repository.paging.Page;
import com.example.lab7gui.repository.paging.Pageable;
import com.example.lab7gui.service.Service;
import com.example.lab7gui.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

public class LoginController implements Observer {

    @FXML
    TableView<Utilizator> userTable;
    @FXML
    TableColumn<Utilizator, Long> userIdColumn;
    @FXML
    TableColumn<Utilizator, String> userFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> userLastNameColumn;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    @FXML
    PasswordField addPasswordField;

    private Service service;

    public void setService(Service service){
        this.service = service;
        initData();
        service.addObserver(this);
    }

    public void initData(){
        Iterable<Utilizator> usersIterable= service.getUtilizatori();
        List<Utilizator> userList = StreamSupport.stream(usersIterable.spliterator(), false).toList();
        model.setAll(userList);

    }

    public void initialize() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        userTable.setItems(model);
    }

    public void handleLogin(){
        Utilizator selected = userTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
        }
        else {
        String passwd = addPasswordField.getText();
        Utilizator userul = service.getUserPasswd(selected.getId(), passwd);
        if(userul == null){
            UserAction.showMessage(null, Alert.AlertType.WARNING, "Login error", "Parola incorecta.");
        }
        else{
            try{
                FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("user-view.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(fxmlLoader.load(), 674,665);
                stage.setScene(scene);

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Main app");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.setScene(scene);

                UserController userController = fxmlLoader.getController();
                userController.setService(this.service);
                dialogStage.show();

                Stage currentStage = (Stage) addPasswordField.getScene().getWindow();
                currentStage.close();

            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        }
    }

    public void handleRegister(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("register-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 300, 200);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Register");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            RegisterController registerController = fxmlLoader.getController();
            registerController.setService(this.service);
            dialogStage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update() {
        initData();
    }
}
