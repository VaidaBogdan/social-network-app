package com.example.lab7gui.controller;

import com.example.lab7gui.SocialNetworkApplication;
import com.example.lab7gui.domain.Prietenie;
import com.example.lab7gui.domain.PrietenieStatus;
import com.example.lab7gui.domain.Tuple;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.repository.paging.Page;
import com.example.lab7gui.repository.paging.Pageable;
import com.example.lab7gui.service.Service;
import com.example.lab7gui.utils.Observer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class UserController implements Observer {
    @FXML
    TableView<Utilizator> userTable;
    @FXML
    TableColumn<Utilizator, Long> userIdColumn;
    @FXML
    TableColumn<Utilizator, String> userFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> userLastNameColumn;
    @FXML
    TextField addFirstNameField;
    @FXML
    TextField addLastNameField;
    @FXML
    TextField updateFirstNameField;
    @FXML
    TextField updateLastNameField;
    @FXML
    PasswordField addPasswordField;
    @FXML
    TableView<Utilizator> friendsTable;
    @FXML
    TableColumn<Utilizator, String> friendFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> friendLastNameColumn;
    @FXML
    TableView<Utilizator> pendingTable;
    @FXML
    TableColumn<Utilizator, String> pendingFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> pendingLastNameColumn;

    ObservableList<Utilizator> model = FXCollections.observableArrayList();

    ObservableList<Utilizator> friendsModel = FXCollections.observableArrayList();
    ObservableList<Utilizator> pendingModel= FXCollections.observableArrayList();

    @FXML
    Button previousButton;
    @FXML
    Button nextButton;
    @FXML
    Slider mySlider;

    private int currentPage=0;
    private int numberOfRecordsPerPage = 2;

    private int totalNumberOfElements;


    private Service service;

    public void setService(Service service){
        this.service = service;
        service.addObserver(this);
        initData();
    }

    public void initData(){
//        Iterable<Utilizator> usersIterable= service.getUtilizatori();
//        List<Utilizator> userList = StreamSupport.stream(usersIterable.spliterator(), false).toList();
        Page<Utilizator> usersOnCurrentPage = service.getUsersOnPage(new Pageable(currentPage, numberOfRecordsPerPage));
        totalNumberOfElements=usersOnCurrentPage.getTotalNumberOfElements();
        List<Utilizator> userList = StreamSupport.stream(usersOnCurrentPage.getElementsOnPage().spliterator(), false).toList();
        model.setAll(userList);

        Utilizator selected = userTable.getSelectionModel().getSelectedItem();
        if(selected != null){
            Long id = selected.getId();
            Utilizator u2 = service.getUser(id);
            System.out.println(u2 + " id: " + id);
            friendsModel.setAll(u2.getFriends());
            pendingModel.setAll(u2.getPendingFriends());
        }

        handlePageNavigationChecks();
    }
    @FXML
    public void initialize(){
        mySlider.setValue(2);
        mySlider.setBlockIncrement(1);
        mySlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            numberOfRecordsPerPage = newValue.intValue();
            initData();
        });
        userIdColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        userTable.setItems(model);


        friendLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        friendFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));

        pendingLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));
        pendingFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));

        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                Utilizator selectedUser = userTable.getSelectionModel().getSelectedItem();
                Long id = selectedUser.getId();
                Utilizator u2 = service.getUser(id);
                System.out.println(u2 + " id: " + id);
                friendsModel.setAll(u2.getFriends());
                pendingModel.setAll(u2.getPendingFriends());
                friendsTable.setItems(friendsModel);
                pendingTable.setItems(pendingModel);

            }
        });

    }

    public void deleteUser(){
        Utilizator selected = userTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
        }
        else{
            selected = service.removeUser(selected.getId());
            if(selected == null){
                UserAction.showMessage(null, Alert.AlertType.WARNING, "Delete error", "Mai incercati.");
            }
            else{
                UserAction.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Userul a fost sters.");
            }
        }


    }

    public void addUser(){
        String nume1 = addFirstNameField.getText();
        String nume2 = addLastNameField.getText();
        String pass = addPasswordField.getText();
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
            }
        }

    }

    public void updateUser(){
        String nume1 = updateFirstNameField.getText();
        String nume2 = updateLastNameField.getText();
        Utilizator selected = userTable.getSelectionModel().getSelectedItem();
        if (nume1.isBlank() || nume2.isBlank() || selected == null) {
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Add error", "Introduceti date in ambele campuri si selectati userul!");
        } else {
            selected.setFirstName(nume1);
            selected.setLastName(nume2);
            var updatedOne = service.updateUser(selected);
            if (updatedOne == null) {
                UserAction.showMessage(null, Alert.AlertType.WARNING, "Update error", "Mai incercati.");
            } else {
                UserAction.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Userul a fost modificat");

            }

        }

    }

    public void acceptPrietenie(){
        Utilizator selected1 = pendingTable.getSelectionModel().getSelectedItem();
        Utilizator selected2 = userTable.getSelectionModel().getSelectedItem();
        if(selected1 == null || selected2 == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
        }
        else{
            Long id1 = selected2.getId();
            Long id2 = selected1.getId();
            Prietenie p = service.findPrietenie(new Tuple<>(id1,id2));
            if(p==null){
                Prietenie p2 = service.findPrietenie(new Tuple<>(id2,id1));
                p2.setStatus(PrietenieStatus.ACCEPTED);
                service.updatePrietenie(p2);
            }
            else{
                p.setStatus(PrietenieStatus.ACCEPTED);
                service.updatePrietenie(p);
            }

        }

    }

    public void stergePrietenie(){
        Utilizator selected1 = friendsTable.getSelectionModel().getSelectedItem();
        Utilizator selected2 = userTable.getSelectionModel().getSelectedItem();
        if(selected1 == null || selected2 == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
        }
        else{
            Long id1 = selected2.getId();
            Long id2 = selected1.getId();
            var p = service.findPrietenie(new Tuple<>(id1, id2));
            if(p == null){
                service.deleteFriendship(new Tuple<>(id2, id1));
            }
            else service.deleteFriendship(new Tuple<>(id1, id2));

        }

    }

    public void respingePrietenie(){
        Utilizator selected1 = pendingTable.getSelectionModel().getSelectedItem();
        ObservableList<Utilizator> selectedUsersTabel = userTable.getSelectionModel().getSelectedItems();
        if(selected1 == null || selectedUsersTabel.size() != 1){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
        }
        else{
            Utilizator selected2 = selectedUsersTabel.get(0);
            if(selected2 == null){
                UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
            }
            else{
                Long id1 = selected2.getId();
                Long id2 = selected1.getId();
                var p = service.findPrietenie(new Tuple<>(id1, id2));
                if(p == null){
                    service.deleteFriendship(new Tuple<>(id2, id1));
                }
                else service.deleteFriendship(new Tuple<>(id1, id2));


            }
        }
    }

    public void initAdaugaPrietenie(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("prietenie-add-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 400);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Prietenie Add");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            PrietenieAddController prietenieAddController = fxmlLoader.getController();
            prietenieAddController.setService(this.service, dialogStage);
            dialogStage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initConversatii(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("conversations-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 600, 500);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Manager conversatii");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            ConversatiiController conversatiiController = fxmlLoader.getController();
            conversatiiController.setService(this.service, dialogStage);
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

    private void handlePageNavigationChecks(){
        previousButton.setDisable(currentPage == 0);
        nextButton.setDisable((currentPage+1)*numberOfRecordsPerPage >= totalNumberOfElements);
    }
    public void goToNextPage() {
        System.out.println("NEXT PAGE");
        currentPage++;
        initData();
    }

    public void goToPreviousPage() {
        System.out.println("PREVIOUS PAGE");
        currentPage--;
        initData();
    }
}
