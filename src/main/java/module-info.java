module com.example.lab7gui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.lab7gui to javafx.fxml;
    exports com.example.lab7gui;
    opens com.example.lab7gui.controller;
    exports com.example.lab7gui.controller;
    opens com.example.lab7gui.service;
    exports com.example.lab7gui.service;
    opens com.example.lab7gui.domain;
    exports com.example.lab7gui.domain;
    opens com.example.lab7gui.utils;
    exports com.example.lab7gui.utils;
    opens com.example.lab7gui.repository;
    exports com.example.lab7gui.repository;
    opens com.example.lab7gui.repository.paging;
    exports com.example.lab7gui.repository.paging;
}