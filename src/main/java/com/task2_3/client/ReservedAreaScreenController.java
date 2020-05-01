package com.task2_3.client;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
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
    private void openChangeAdministratorCredentialsBox() throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource("changeAdministratorCredentialsScreen.fxml"));
            Scene scene=new Scene(fxmlLoader.load(),150, 150);
            Stage stage = new Stage();
            stage.setTitle("Change credentials");
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
    private void openForceUpdateBox() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void openForceScrapingBox() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void openSetLimitBox() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void openSetReplicaParametersBox() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void openSetStartingYearBox() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }

}
