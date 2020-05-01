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
    private void switchToChangeAdminCredentialsScreen() throws IOException {
        Start.setRoot("changeAdminCredentialsScreen");
    }
    @FXML
    private void switchToForceUpdateScreen() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void switchToForceScrapingScreen() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void switchToSetLimitScreen() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void switchToSetReplicaParametersScreen() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
    @FXML
    private void switchToSetStartingYearScreen() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }

}
