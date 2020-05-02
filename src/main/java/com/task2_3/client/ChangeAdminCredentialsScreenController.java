package com.task2_3.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangeAdminCredentialsScreenController {

    @FXML
    private TextField userInput;
    @FXML
    private TextField passwordInput;
    @FXML
    private Label errorLabel;

    @FXML
    private void changeCredentials()throws IOException {
        if(userInput.getText().equals("") ||passwordInput.getText().equals("")){
            errorLabel.setText("All fields must be filled!");
            errorLabel.setVisible(true);
            return;
        }
        // TODO OPEN SOCKET WITH SERVER AND SEND NEW CREDENTIALS
        //if request correctly handled:
        try {
            userInput.setText("");
            passwordInput.setText("");
            errorLabel.setVisible(false);
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("confirmBox.fxml"));
            Scene scene=new Scene(fxmlLoader.load(),300, 150);
            Label confirmMessage=(Label) scene.lookup("#confirmMessage");
            confirmMessage.setText("Credentials successfully changed.");
            Stage stage = new Stage();
    //        stage.setTitle("Change credentials");
            stage.setScene(scene);
            stage.show();
            // Hide this current window (if this is what you want)
            //   ((Node)(event.getSource())).getScene().getWindow().hide();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    public void switchToReservedAreaScreen()throws IOException{
        Start.setRoot("reservedAreaScreen");
    }
}
