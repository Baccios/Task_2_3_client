package com.task2_3.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RouteScreenController implements Initializable {
    @FXML
    public HBox hbox1;
    @FXML
    public HBox hbox2;
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
    ArrayList<RankingItem<Airline>> bestAirlines=new ArrayList<>();;
    private ArrayList<Airline> airlineRows=new ArrayList<>();
    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources){
        for (Node node : hbox1.getChildren()) {
            if (node instanceof TextField) {
                ((TextField)node).setDisable(true);
            }
        }
        for (Node node : hbox2.getChildren()) {
            if (node instanceof TextField) {
                ((TextField)node).setDisable(true);
            }
        }

        originAirportLabel.setText("Origin airport:  "+Start.route.getOrigin().getName());
        destinationAirportLabel.setText("Destination airport:  "+Start.route.getDestination().getName());
        RouteStatistics rs=Start.route.getStats();
        delayProbText.setText(String.valueOf(rs.fifteenDelayProb));
        meanDelayText.setText(String.valueOf(rs.getMeanDelay()));
        delayCauseText.setText(rs.getMostLikelyCauseDelay());
        cancProbText.setText(String.valueOf(rs.cancellationProb));
        cancCauseText.setText(rs.getMostLikelyCauseCanc());

        ObservableList<PieChart.Data> AirlinepieChartData=FXCollections.observableArrayList();
        bestAirlines= rs.getBestAirlines();
        for(RankingItem<Airline> a:bestAirlines){
            String name=a.item.getName();
            double qos=a.value;
            PieChart.Data d=new PieChart.Data(name,qos);
            AirlinepieChartData.add(d);

        }
        AirlinePiechart.setData(AirlinepieChartData);
        AirlinePiechart.setLegendVisible(false);
        AirlinePiechart.setTitle("Most efficient airlines:");

        for (final PieChart.Data data : AirlinePiechart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {

                            for(RankingItem<Airline> a:bestAirlines){
                                if(a.item.getName().equals(String.valueOf(data.getName()))){
                                    Start.airline=a.item;
                                    try{
                                        System.out.println(String.valueOf(a.item.getName() + "%"));
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
