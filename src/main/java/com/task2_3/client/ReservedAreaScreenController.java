package com.task2_3.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ReservedAreaScreenController {

    @FXML
    private void switchToAdminAuthenticationScreen() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void switchToChangeAdminCredentialsScreen() throws IOException {
        Start.setRoot("changeAdminCredentialsScreen");
    }
    @FXML
    private void switchToForceUpdateScreen() throws IOException {
        //TODO open socket to send update request to server
        //if request correctly handled:
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("confirmBox.fxml"));
            Scene scene=new Scene(fxmlLoader.load(),300, 150);
            Label confirmMessage=(Label) scene.lookup("#confirmMessage");
            confirmMessage.setText("Database correctly updated.");
            Stage stage = new Stage();
     //       stage.setTitle("Change credentials");
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
    private void switchToForceScrapingScreen() throws IOException {
        //TODO open socket to send update request to server
        //if request correctly handled:
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("confirmBox.fxml"));
            Scene scene=new Scene(fxmlLoader.load(),300, 150);
            Label confirmMessage=(Label) scene.lookup("#confirmMessage");
            confirmMessage.setText("Scraping correctly requested.");
            Stage stage = new Stage();
            //       stage.setTitle("Change credentials");
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
    private void switchToSetLimitScreen() throws IOException {
        Start.setRoot("setLimitScreen");
    }
    @FXML
    private void switchToSetReplicaParametersScreen() throws IOException {
        Start.setRoot("setReplicaParametersScreen");
    }
    @FXML
    private void switchToSetStartingYearScreen() throws IOException {
        Start.setRoot("setStartingYearScreen");
    }

}
