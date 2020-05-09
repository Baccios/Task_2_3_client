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
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RouteScreenController implements Initializable {
    @FXML
    public Label originAirportLabel;
    @FXML
    public Label destinationAirportLabel;
    @FXML
    public TextField delayProbText;
    @FXML
    public TextField meanDelayText;
    @FXML
    public TextField delayCauseText;
    @FXML
    public TextField cancProbText;
    @FXML
    public TextField cancCauseText;
    @FXML
    PieChart AirlinePiechart;
    private ArrayList<Airline> airlineRows=new ArrayList<>();
    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources){
        originAirportLabel.setText(Start.route.getOrigin().getName());
        destinationAirportLabel.setText(Start.route.getDestination().getName());
        RouteStatistics rs=Start.route.getStats();
        delayProbText.setText(String.valueOf(rs.fifteenDelayProb));
        meanDelayText.setText(String.valueOf(rs.getMeanDelay()));
        delayCauseText.setText(rs.getMostLikelyCauseDelay());
        cancProbText.setText(String.valueOf(rs.cancellationProb));
        cancCauseText.setText(rs.getMostLikelyCauseCanc());

        //TODO GET BEST AIRLINES FOR THIS ROUTE
        ObservableList<PieChart.Data> AirlinepieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("aaaaaaaaaaa", 13),
                        new PieChart.Data("aaaaaaaaaaa", 25),
                        new PieChart.Data("aaaaaaaaaaa", 10),
                        new PieChart.Data("aaaaaaaaaaa", 22),
                        new PieChart.Data("aaaaaaaaaaa", 30));
        AirlinePiechart.setData(AirlinepieChartData);
        for (final PieChart.Data data : AirlinePiechart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            System.out.println(String.valueOf(data.getPieValue()) + "%");
                            for(Airline a:airlineRows){
                                if(a.getName().equals(String.valueOf(data.getPieValue()))){
                                    Start.airline=a;
                                    try{
                                        switchToAirlineScreen();
                                    }
                                    catch(Exception ex){
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }
                    });
        }
    }
    @FXML
    private void switchToAirlineScreen() throws IOException {
        Start.setRoot("airlineScreen");
    }

}
