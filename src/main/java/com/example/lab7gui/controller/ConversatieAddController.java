package com.example.lab7gui.controller;

import com.example.lab7gui.domain.Conversation;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.service.Service;
import com.example.lab7gui.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.List;
import java.util.stream.StreamSupport;

public class ConversatieAddController implements Observer {
    @FXML
    TableView<Utilizator> userTable;
    @FXML
    TableColumn<Utilizator, Long> userIdColumn;
    @FXML
    TableColumn<Utilizator, String> userFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> userLastNameColumn;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    private Service service;
    private Stage modelStage;

    public void setService(Service service, Stage modelStage){
        this.service = service;
        this.modelStage = modelStage;
        service.addObserver(this);
        initData();
    }
    public void initData(){
        Iterable<Utilizator> usersIterable= service.getUtilizatori();
        List<Utilizator> userList = StreamSupport.stream(usersIterable.spliterator(), false).toList();
        model.setAll(userList);
    }
    @FXML
    public void initialize() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        userTable.setItems(model);

        userTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    public void creareConversatie(){
        ObservableList<Utilizator> selectedUsers = userTable.getSelectionModel().getSelectedItems();
        if(selectedUsers.size() < 2){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati minim 2 useri pt a crea o converstie!!");
        }
        else{
            List<Utilizator> l = selectedUsers.stream().toList();
            Conversation c = new Conversation(l);
            Conversation a = service.addConversation(c);
            if(a == null){
                UserAction.showMessage(null, Alert.AlertType.WARNING, "Add error", "Mai incercati.");
            }
            else{
                UserAction.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Conversatia a fost creata.");
            }
        }
    }

    @Override
    public void update() {
        initData();
    }
}
