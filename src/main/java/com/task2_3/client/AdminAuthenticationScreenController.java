package com.task2_3.client;

import javafx.fxml.FXML;
import java.io.IOException;

public class AdminAuthenticationScreenController {

    @FXML
    private void switchToReservedAreaScreen() throws IOException {
        Start.setRoot("overallStatsScreen");
    }
    @FXML
    private void switchToInitialScreen() throws IOException {
        Start.setRoot("initialScreen");
    }
}
