package com.task2_3.client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RouteScreenController implements Initializable {
    @FXML
    public TextField cancProbText;
    @FXML
    public TextField delayProbText;
    @FXML
    public TextField cancCauseText;
    @FXML
    public TextField delayCauseText;
    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources){
        RouteStatistics rs=Start.route.getStats();
        cancProbText.setText(String.valueOf(rs.cancellationProb));
        delayProbText.setText(String.valueOf(rs.fifteenDelayProb));
        cancCauseText.setText(rs.getMostLikelyCauseCanc());
        delayCauseText.setText(rs.getMostLikelyCauseDelay());
    }

}
