package com.example.lab7gui.controller;

import com.example.lab7gui.domain.Conversation;
import com.example.lab7gui.domain.Message;
import com.example.lab7gui.domain.Utilizator;
import com.example.lab7gui.service.Service;
import com.example.lab7gui.utils.Observer;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ConversatieChatController implements Observer {
    private Conversation c;

    @FXML
    TableView<Utilizator> userTable;
    @FXML
    TableColumn<Utilizator, Long> userIdColumn;
    @FXML
    TableColumn<Utilizator, String> userFirstNameColumn;
    @FXML
    TableColumn<Utilizator, String> userLastNameColumn;

    @FXML
    TableView<Message> messageTable;
    @FXML
    TableColumn<Message,Long> messageIdColumn;
    @FXML
    TableColumn<Message,String> messageFromNameColumn;
    @FXML
    TableColumn<Message,String> messageTextColumn;
    @FXML
    TableColumn<Message, LocalDateTime> messageDateColumn;
    @FXML
    TableColumn<Message,Long> messageReplyIdColumn;
    @FXML
    TextField messageTextfield;

    private Service service;
    private Stage modelStage;
    ObservableList<Message> messageModel = FXCollections.observableArrayList();
    ObservableList<Utilizator> userModel = FXCollections.observableArrayList();

    public void setService(Service service, Stage modelStage, Conversation c) {
        this.service = service;
        this.c = c;
        this.modelStage = modelStage;
        service.addObserver(this);
        initData();
    }

    public void initData(){
        List<Utilizator> userList =c.getParticipants();
        userModel.setAll(userList);

        List<Message> messageList = c.getMessages();
        messageModel.setAll(messageList);
    }

    @FXML
    public void initialize() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, Long>("id"));
        userFirstNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("firstName"));
        userLastNameColumn.setCellValueFactory(new PropertyValueFactory<Utilizator, String>("lastName"));

        messageIdColumn.setCellValueFactory(new PropertyValueFactory<Message, Long>("id"));
        messageFromNameColumn.setCellValueFactory(cellData -> {
            Message message = cellData.getValue();
            return new SimpleStringProperty(message.getFrom().getFirstName());
        });
        messageTextColumn.setCellValueFactory(new PropertyValueFactory<Message, String>("mesaj"));
        messageDateColumn.setCellValueFactory(new PropertyValueFactory<Message, LocalDateTime>("data"));
        messageReplyIdColumn.setCellValueFactory(cellData -> {
            Message message = cellData.getValue();
            return message.getOriginal() != null ? new SimpleLongProperty(message.getOriginal().getId()).asObject()
                    : new SimpleLongProperty().asObject();
        });

        userTable.setItems(userModel);
        messageTable.setItems(messageModel);
    }

    public void addMessage(){
        Utilizator selected = userTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati un user inainte de apasare!");
        }
        else{
            Message selectedMessage = messageTable.getSelectionModel().getSelectedItem();
            List<Utilizator> to;
            if(selectedMessage == null) {
                to = new ArrayList<>(c.getParticipants());
                to.remove(selected);
            }
            else{
                to = new ArrayList<>();
                to.add(selectedMessage.getFrom());
            }
            Message nou = new Message(selected, to, messageTextfield.getText() , selectedMessage);
            Message nou2 = service.sendMessage(this.c, nou);
            if(nou2.getId() != null){
                this.c=service.updateConversation(c);
                UserAction.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Mesajul a fost adaugat");
            }
            else {
                UserAction.showMessage(null, Alert.AlertType.WARNING, "Add error", "Mai incercati.");
            }

        }
    }

    public void deleteMessage(){
        Message selected = messageTable.getSelectionModel().getSelectedItem();
        if(selected == null){
            UserAction.showMessage(null, Alert.AlertType.ERROR, "Selection error", "Selectati un mesaj inainte de apasare!");
        }
        else{
            selected=service.deleteMessage(this.c, selected.getId());
            if(selected == null){
                UserAction.showMessage(null, Alert.AlertType.WARNING, "Delete error", "Mai incercati.");
            }
            else{
                UserAction.showMessage(null, Alert.AlertType.INFORMATION, "Succes!", "Mesajul a fost sters.");
                this.c=service.updateConversation(c);
            }
        }
    }
    @Override
    public void update() {
        initData();
    }
}
