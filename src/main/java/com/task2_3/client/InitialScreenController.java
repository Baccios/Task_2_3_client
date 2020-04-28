package com.task2_3.client;

import javafx.fxml.FXML;
import java.io.IOException;

public class InitialScreenController {

    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }
    @FXML
    private void switchToAdminAuthentication() throws IOException {
        Start.setRoot("adminAuthenticationScreen");
    }
}
