package com.task2_3.client;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.io.IOException;
import java.util.ArrayList;

public class Start extends Application{
    public static Scene scene;
    public static Admin_Protocol_Client adminManager;
    public static Neo4jDBManager neoDbManager;
    //Entities that are selected by user to be analyzed.
    public static Route route;
    public static Airline airline;
    public static Airport airport;

    //variables to pass parameters between initial screen and hints screen
    public static Airport hintOrigin;
    public static Airport hintDestination;

    public void start(Stage stage)throws IOException{
        neoDbManager=Neo4jDBManager.getInstance();
        scene = new Scene(loadFXML("initialScreen"));
        stage.setMinHeight(720);
        stage.setMinWidth(1080);
        stage.setMaximized(true);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setOnCloseRequest(event -> {
            Neo4jDBManager.getInstance().close();
        });
        stage.show();
    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void main (String[] args) {
        launch();
    }
}

