package com.task2_3.client;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class AdminAuthenticationScreenController {

    @FXML
    private void switchToInitialScreen() throws IOException {
        Start.setRoot("initialScreen");
    }
    @FXML
    private void signin()throws IOException{
        TextField userInput=(TextField) Start.scene.lookup("#userInput");
        TextField passwordInput=(TextField) Start.scene.lookup("#passwordInput");
        Label errorLabel=(Label) Start.scene.lookup("#errorLabel");
        if(userInput.getText().equals("") ||passwordInput.getText().equals("")){
            errorLabel.setText("All fields must be filled!");
            errorLabel.setVisible(true);
            return;
        }
        int ret=Start.adminManager.startAuthHandshake(userInput.getText(),passwordInput.getText());
        if(ret==0){
            Start.setRoot("ReservedAreaScreen");
            return;
        }
        if(ret==1){
            errorLabel.setText("Inserted credentials are wrong!");
            errorLabel.setVisible(true);
            return;
        }
        if(ret==2){
            errorLabel.setText("Admin already logged");
            errorLabel.setVisible(true);
            return;
        }
        System.out.println("Unexpected error during autentication");
        System.exit(1);
    }
}
