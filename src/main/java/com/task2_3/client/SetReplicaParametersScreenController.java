package com.task2_3.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SetReplicaParametersScreenController {
    @FXML
    ComboBox combobox;
    @FXML
    Label errorLabel;
    @FXML
    public void setReplicationLevel(){
        if(combobox.getValue()==null){
            errorLabel.setText("Select a replication level!");
            errorLabel.setVisible(true);
            return;
        }
        // TODO OPEN SOCKET WITH SERVER AND SEND parameter
        //if request correctly handled:
        try {
            errorLabel.setVisible(false);
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("confirmBox.fxml"));
            Scene scene=new Scene(fxmlLoader.load(),300, 150);
            Label confirmMessage=(Label) scene.lookup("#confirmMessage");
            confirmMessage.setText("Replication level correctly set.");
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
