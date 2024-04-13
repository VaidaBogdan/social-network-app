package com.example.lab7gui.controller;

import com.example.lab7gui.domain.Tuple;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.service.Service;
import com.example.lab7gui.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.StreamSupport;

public class PrietenieAddController implements Observer {
    private Service service;

    @FXML
    TableView<Utilizator> userTable;
    @FXML
    TableColumn<Utilizator, Long> userIdColumn;
    @FXML
    TableColumn<Utilizator, String> userFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> userLastNameColumn;
    @FXML
    TableView<Utilizator> friendsTable;
    @FXML
    TableColumn<Utilizator, Long> friendIdColumn;
    @FXML
    TableColumn<Utilizator, String> friendFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> friendLastNameColumn;

    public Stage modelStage;

    ObservableList<Utilizator> userModel = FXCollections.observableArrayList();
    ObservableList<Utilizator> friendModel =  FXCollections.observableArrayList();

    public void setService(Service service, Stage modelStage){
        this.service = service;
        this.modelStage = modelStage;
        service.addObserver(this);
        initData();
    }

    public void initData(){
        Iterable<Utilizator> usersIterable= service.getUtilizatori();
        List<Utilizator> userList = StreamSupport.stream(usersIterable.spliterator(), false).toList();
        userModel.setAll(userList);
        friendModel.setAll(userList);

    }
    @FXML
    public void initialize(){
        userIdColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));

        friendIdColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        friendFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        friendLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        userTable.setItems(userModel);
        friendsTable.setItems(friendModel);

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Utilizator selectedUser = userTable.getSelectionModel().getSelectedItem();
                friendModel.remove(selectedUser);
                if(oldSelection != null) {
                    friendModel.add(oldSelection);
                }
                friendsTable.setItems(friendModel);

            }
        });

    }

    public void createPrietenie(){
        Utilizator u1 = userTable.getSelectionModel().getSelectedItem();
        Utilizator u2 = friendsTable.getSelectionModel().getSelectedItem();
        if(u1 == null || u2 == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
        }
        else{
            if(service.createFriendship(new Tuple<>(u1.getId(), u2.getId()))){
                UserAction.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Cerere adaugata");
            }
            else{
                UserAction.showMessage(null, Alert.AlertType.WARNING, "Add error", "Mai incercati.");
            }
        }

    }

    @Override
    public void update() {
        initData();
    }
}
