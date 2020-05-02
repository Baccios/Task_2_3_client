package com.task2_3.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SetStartingYearScreenController {
    @FXML
    TextField yearInput;
    @FXML
    Label errorLabel;
    @FXML
    public void setStartingYear(){
        if(yearInput.getText().equals("")){
            errorLabel.setText("Write something!");
            errorLabel.setVisible(true);
            return;
        }
        // TODO OPEN SOCKET WITH SERVER AND SEND parameter
        //if request correctly handled:
        try {
            yearInput.setText("");
            errorLabel.setVisible(false);
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("confirmBox.fxml"));
            Scene scene=new Scene(fxmlLoader.load(),300, 150);
            Label confirmMessage=(Label) scene.lookup("#confirmMessage");
            confirmMessage.setText("New starting year correctly set.");
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
    public void switchToReservedAreaScreen()throws IOException {
        Start.setRoot("reservedAreaScreen");
    }
}
