package com.task2_3.client;

import javafx.fxml.FXML;
import java.io.IOException;

public class RouteScreenController {

    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }

}
