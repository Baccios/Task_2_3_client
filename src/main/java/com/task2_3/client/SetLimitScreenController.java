package com.task2_3.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SetLimitScreenController {
    @FXML
    TextField limitInput;
    @FXML
    Label errorLabel;

    @FXML
    public void setLimit(){
        if(limitInput.getText().equals("")){
            errorLabel.setText("Write something!");
            errorLabel.setVisible(true);
            return;
        }
        // TODO OPEN SOCKET WITH SERVER AND SEND parameter
        //if request correctly handled:
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("confirmBox.fxml"));
            Scene scene=new Scene(fxmlLoader.load(),300, 150);
            Label confirmMessage=(Label) scene.lookup("#confirmMessage");
            confirmMessage.setText("New database size limit correctly set.");
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
}
