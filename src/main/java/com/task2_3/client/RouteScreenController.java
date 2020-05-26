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
    BarChart airlineBarChart;
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
                System.out.println(((TextField) node).getText());
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

        originAirportLabel.setText("Origin airport:  "+Start.route.getOrigin().getName());
        destinationAirportLabel.setText("Destination airport:  "+Start.route.getDestination().getName());
        RouteStatistics rs=Start.route.getStats();

        delayProbText.setText(String.format("%.2f", (rs.fifteenDelayProb*100))+"%");
        cancProbText.setText(String.format("%.2f", (rs.cancellationProb*100))+"%");
        meanDelayText.setText(String.format("%.2f", (rs.getMeanDelay()))+" min");
        delayCauseText.setText(rs.getMostLikelyCauseDelay());
        cancCauseText.setText(rs.getMostLikelyCauseCanc());

        CategoryAxis x=new CategoryAxis();
        x.setLabel("airlines");
        NumberAxis y=new NumberAxis();
        y.setLabel("qos");
        XYChart.Series XYdata=new XYChart.Series();

        bestAirlines= rs.getBestAirlines();
        double totalQos=0;
        for(RankingItem<Airline> a:bestAirlines){
            totalQos+=a.value;
        }
        for(RankingItem<Airline> a:bestAirlines){
            String name=a.item.getName();
            double qos=a.value;
            qos=(qos==-1)?10:qos;

            XYChart.Data b=new XYChart.Data(name,qos);
            XYdata.getData().add(b);
        }
        airlineBarChart.setTitle("Most efficient airlines:");
        airlineBarChart.setLegendVisible(false);
        airlineBarChart.getData().add(XYdata);
        ObservableList<XYChart.Data> s=XYdata.getData();
        for(int i=0;i<s.size();i++){
            XYChart.Data data=s.get(i);
            System.out.println(data);
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            for(RankingItem<Airline> a:bestAirlines){
                                if(a.value==(double)data.getYValue()){
                                    Start.airline=a.item;
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
