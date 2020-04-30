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
        if(userInput.getText().equals("admin") && passwordInput.getText().equals("admin")){
            Start.setRoot("ReservedAreaScreen");
            return;
        }
        errorLabel.setText("Inserted credentials are wrong!");
        errorLabel.setVisible(true);
        return;

    }
}
