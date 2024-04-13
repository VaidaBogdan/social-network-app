package com.example.lab7gui;

import com.example.lab7gui.controller.LoginController;
import com.example.lab7gui.controller.UserController;
import com.example.lab7gui.domain.*;
import com.example.lab7gui.domain.validators.ValidationFactory;
import com.example.lab7gui.domain.validators.ValidationStrategy;
import com.example.lab7gui.repository.*;
import com.example.lab7gui.repository.paging.PagingRepository;
import com.example.lab7gui.repository.paging.UserPagingRepository;
import com.example.lab7gui.service.Service;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOError;
import java.io.IOException;

public class SocialNetworkApplication extends Application {

    private Service service;

    String url="";
    String username = "";
    String password = "";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        var v = ValidationFactory.getInstance();
        Repository<Long, Utilizator> userRepository = new UserDBRepository(url, username, password,
                v.createValidator(ValidationStrategy.UTILIZATOR));
        Repository<Tuple<Long, Long>, Prietenie> prietenieRepository = new PrietenieDBRepository(url, username, password,
                userRepository, v.createValidator(ValidationStrategy.PRIETENIE));
        Repository<Long, Message> messageRepository = new MessageDBRepository(url, username, password,
                userRepository, v.createValidator(ValidationStrategy.MESAJ));
        Repository<Long, Conversation> conversationRepository = new ConversationDBRepository(url, username, password,
                userRepository, messageRepository, v.createValidator(ValidationStrategy.CONVERSATIE));
        PagingRepository<Long, Utilizator> userPagingRepository = new UserPagingRepository(url, username, password,
                v.createValidator(ValidationStrategy.UTILIZATOR));

        service = new Service(userRepository, prietenieRepository, messageRepository, conversationRepository, userPagingRepository);
        initView(primaryStage);
        primaryStage.setTitle("Login");
        primaryStage.show();


    }

    public void initView(Stage stage) throws IOException{
//        FXMLLoader loader = new FXMLLoader(SocialNetworkApplication.class.getResource("user-view.fxml"));
//        stage.setScene(new Scene(loader.load(),674,665));
//        UserController  userController= loader.getController();
//        userController.setService(service);
        FXMLLoader loader = new FXMLLoader(SocialNetworkApplication.class.getResource("login-view.fxml"));
        stage.setScene(new Scene(loader.load(), 600, 400));
        LoginController loginController = loader.getController();
        loginController.setService(service);


    }
}
