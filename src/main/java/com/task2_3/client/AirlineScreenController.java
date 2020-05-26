
package com.task2_3.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.*;
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

public class AirlineScreenController implements Initializable {

    @FXML
    public Label airlineLabel;
    @FXML
    public HBox hbox1;
    @FXML
    public HBox hbox2;
    @FXML
    public TextField qosText;
    @FXML
    public TextField delayProbText;
    @FXML
    public TextField meanDelayText;
    @FXML
    public TextField cancProbText;
    @FXML
    public TextField totalServedRoutesText;
    @FXML
    public TextField firstPlacesCounterText;

    @FXML
    PieChart AirportPiechart;
    @FXML
    BarChart airportBarChart;
    private ArrayList<RankingItem<Airport>> mostServedAirports=new ArrayList<>();

    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources){
        for (Node node : hbox1.getChildren()) {
            if (node instanceof TextField) {
                ((TextField)node).setEditable(false);
                ((TextField)node).setCursor(Cursor.DEFAULT);
            }
        }
        for (Node node : hbox2.getChildren()) {
            if (node instanceof TextField) {
                ((TextField)node).setEditable(false);
                ((TextField)node).setCursor(Cursor.DEFAULT);
            }
        }

        airlineLabel.setText("Airline: "+Start.airline.getName());
        AirlineStatistics rs=Start.airline.getStats();
        qosText.setText(String.format("%.2f", (rs.qosIndicator)));
        delayProbText.setText(String.format("%.2f", (rs.fifteenDelayProb))+"%");
        cancProbText.setText(String.format("%.2f", (rs.cancellationProb))+"%");
        totalServedRoutesText.setText(String.valueOf(Start.airline.getTotalServedRoutes()));
        firstPlacesCounterText.setText(String.valueOf(Start.airline.getFirstPlacesCount()));

        ObservableList<PieChart.Data> AirportpieChartData=FXCollections.observableArrayList();
        mostServedAirports= rs.getMostServedAirports();
        double totalQos=0;
        for(RankingItem<Airport> a:mostServedAirports){
            totalQos+=a.value;
        }
        for(RankingItem<Airport> a:mostServedAirports){
            String name=a.item.getName();
            double qos=a.value;
            double percentage=(qos*100)/totalQos;
            String percentageStr = " ("+String.format("%.0f", percentage)+"%)";
            String s=name+percentageStr;
            PieChart.Data d=new PieChart.Data(s,qos);
            AirportpieChartData.add(d);
        }

        AirportPiechart.setData(AirportpieChartData);
        AirportPiechart.setTitle("Most served airports:");
        AirportPiechart.setLegendVisible(false);
        for (final PieChart.Data data : AirportPiechart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            for(RankingItem<Airport> a:mostServedAirports){
                                if(a.value==data.getPieValue()){
                                    Start.airport=a.item;
                                    try{
                                        switchToAirportScreen();
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
    private void switchToAirportScreen() throws IOException {
        Start.setRoot("airportScreen");
    }
}
