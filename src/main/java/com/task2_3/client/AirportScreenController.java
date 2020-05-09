
package com.task2_3.client;

        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.event.EventHandler;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;
        import javafx.scene.chart.PieChart;
        import javafx.scene.control.Label;
        import javafx.scene.control.TableColumn;
        import javafx.scene.control.TableView;
        import javafx.scene.control.TextField;
        import javafx.scene.input.MouseEvent;

        import java.io.IOException;
        import java.net.URL;
        import java.util.ResourceBundle;

public class AirportScreenController implements Initializable {
    @FXML
    public Label airportLabel;
    @FXML
    public TextField delayProbText;
    @FXML
    public TextField delayCauseText;
    @FXML
    public TextField cancProbText;
    @FXML
    public TextField cancCauseText;
    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources){
        airportLabel.setText(Start.airport.getName());
        AirportStatistics rs=Start.airport.getStats();
        delayProbText.setText(String.valueOf(rs.fifteenDelayProb));
        delayCauseText.setText(rs.getMostLikelyCauseDelay());
        cancProbText.setText(String.valueOf(rs.cancellationProb));
        cancCauseText.setText(rs.getMostLikelyCauseCanc());

    }

}
