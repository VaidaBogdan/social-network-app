package com.example.lab7gui.controller;

import com.example.lab7gui.SocialNetworkApplication;
import com.example.lab7gui.domain.Conversation;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.service.Service;
import com.example.lab7gui.utils.Observer;
import javafx.beans.property.SimpleLongProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

public class ConversatiiController implements Observer {
    @FXML
    TableView<Utilizator> userTable;
    @FXML
    TableColumn<Utilizator, Long> userIdColumn;
    @FXML
    TableColumn<Utilizator, String> userFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> userLastNameColumn;
    @FXML
    TableView<Conversation> conversationTable;
    @FXML
    TableColumn<Conversation, Long> conversationIdColumn;
    @FXML
    TableColumn<Conversation, Long> conversationNumberParticipantsColumn;
    @FXML
    TableColumn<Conversation, Long> conversationNumberMessagesColumn;

    ObservableList<Utilizator> userModel = FXCollections.observableArrayList();
    ObservableList<Conversation> conversationModel = FXCollections.observableArrayList();

    private Service service;
    private Stage modelStage;

    public void setService(Service service, Stage modelStage){
        this.service = service;
        this.modelStage = modelStage;
        service.addObserver(this);
        initData();
    }
    public void initData(){
        Iterable<Conversation> conversationIterable = service.getConversatii();
        List<Conversation> conversationList = StreamSupport.stream(conversationIterable.spliterator(), false).toList();
        conversationModel.setAll(conversationList);
    }

    @FXML
    public void initialize(){
        userIdColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));

        conversationIdColumn.setCellValueFactory(new PropertyValueFactory<Conversation, Long>("id"));
        conversationNumberParticipantsColumn.setCellValueFactory(cellData -> {
            Conversation conversation = cellData.getValue();
            return new SimpleLongProperty(conversation.getParticipants().size()).asObject();
        });

        conversationNumberMessagesColumn.setCellValueFactory(cellData -> {
            Conversation conversation = cellData.getValue();
            return new SimpleLongProperty(conversation.getMessages().size()).asObject();
        });

        conversationTable.setItems(conversationModel);

        conversationTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->{
            if(newSelection != null){
                Conversation selected = conversationTable.getSelectionModel().getSelectedItem();
                userModel.setAll(selected.getParticipants());
                userTable.setItems(userModel);
            }
        });
    }

    public void newConvo(){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("new-conversation-view.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(fxmlLoader.load(), 300, 400);
            stage.setScene(scene);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Conversation add");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.setScene(scene);

            ConversatieAddController conversatieAddController = fxmlLoader.getController();
            conversatieAddController.setService(this.service, dialogStage);
            dialogStage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showConversation(){
        Conversation c = conversationTable.getSelectionModel().getSelectedItem();
        if(c == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
        }
        else {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(SocialNetworkApplication.class.getResource("conversation-chat-view.fxml"));
                Stage stage = new Stage();
                Scene scene = new Scene(fxmlLoader.load(), 600, 600);
                stage.setScene(scene);

                Stage dialogStage = new Stage();
                dialogStage.setTitle("Conversation add");
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.setScene(scene);

                ConversatieChatController controller = fxmlLoader.getController();
                controller.setService(this.service, dialogStage, c);
                dialogStage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void deleteConvo(){
        Conversation c = conversationTable.getSelectionModel().getSelectedItem();
        if(c == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati inainte de apasare!!!");
        }
        else {
            c=service.deleteConversation(c.getId());
            if(c==null){
                UserAction.showMessage(null, Alert.AlertType.WARNING, "Delete error", "Mai incercati.");
            }
            else {
                UserAction.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Conversatia a fost stearsa.");
            }
        }
    }


    @Override
    public void update() {
        initData();
    }
}
