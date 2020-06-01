
package com.task2_3.client;

        import javafx.collections.FXCollections;
        import javafx.collections.ObservableList;
        import javafx.event.EventHandler;
        import javafx.fxml.FXML;
        import javafx.fxml.Initializable;
        import javafx.scene.Cursor;
        import javafx.scene.Node;
        import javafx.scene.chart.PieChart;
        import javafx.scene.control.Label;
        import javafx.scene.control.TableColumn;
        import javafx.scene.control.TableView;
        import javafx.scene.control.TextField;
        import javafx.scene.input.MouseEvent;
        import javafx.scene.layout.AnchorPane;
        import javafx.scene.layout.HBox;

        import java.io.IOException;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.ResourceBundle;

public class AirportScreenController implements Initializable {
    @FXML
    public Label airportLabel;
    @FXML
    HBox hbox1;
    @FXML
    HBox hbox2;
    @FXML
    public TextField delayProbText;
    @FXML
    public TextField delayCauseText;
    @FXML
    public TextField cancProbText;
    @FXML
    public TextField cancCauseText;
    @FXML
    public TextField twoHopsDestinationsText;;
    @FXML
    public PieChart AirlinePiechart;
    @FXML
    public PieChart RoutePiechart;
    private ArrayList<RankingItem<Airline>> mostServedAirlines=new ArrayList<>();
    private ArrayList<RankingItem<Route>> mostServedRoutes=new ArrayList<>();



    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }
    @FXML
    private void switchToAirlineScreen() throws IOException {
        Start.setRoot("airlineScreen");
    }
    @FXML
    private void switchToRouteScreen() throws IOException {
        Start.setRoot("routeScreen");
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources){
        for (Node node : hbox1.getChildren()) {
            if (node instanceof TextField) {
                ((TextField)node).setEditable(false);
                ((TextField)node).setDisable(true);
                ((TextField)node).setCursor(Cursor.DEFAULT);
            }
        }
        for (Node node : hbox2.getChildren()) {
            if (node instanceof TextField) {
                ((TextField)node).setEditable(false);
                ((TextField)node).setDisable(true);
                ((TextField)node).setCursor(Cursor.DEFAULT);
            }
        }
        airportLabel.setText("Airport: "+Start.airport.getName());
        AirportStatistics rs=Start.airport.getStats();
        delayProbText.setText(String.format("%.2f", (rs.fifteenDelayProb*100))+"%");
        cancProbText.setText(String.format("%.2f", (rs.cancellationProb*100))+"%");
        delayCauseText.setText(rs.getMostLikelyCauseDelay());
        cancCauseText.setText(rs.getMostLikelyCauseCanc());
        twoHopsDestinationsText.setText(String.valueOf(Start.airport.getTwoHopsDestinations()));

        ObservableList<PieChart.Data> AirlinepieChartData=FXCollections.observableArrayList();
        mostServedAirlines= rs.getMostServedAirlines();
        double totalQos=0;
        for(RankingItem<Airline> a:mostServedAirlines){
            totalQos+=a.value;
        }
        for(RankingItem<Airline> a:mostServedAirlines){
            String name=a.item.getName();
            double qos=a.value;
            double percentage=(qos*100)/totalQos;
            String percentageStr = " ("+String.format("%.0f", percentage)+"%)";
            String s=name+percentageStr;
            PieChart.Data d=new PieChart.Data(s,qos);
            AirlinepieChartData.add(d);

        }
        AirlinePiechart.setData(AirlinepieChartData);
        AirlinePiechart.setTitle("Most served airlines:");
        AirlinePiechart.setLegendVisible(false);


        ObservableList<PieChart.Data> RoutepieChartData=FXCollections.observableArrayList();
        mostServedRoutes= rs.getMostServedRoutes();
        totalQos=0;
        for(RankingItem<Route> a:mostServedRoutes){
            totalQos+=a.value;
        }
        for(RankingItem<Route> a:mostServedRoutes){
            String name=a.item.getOrigin().getIATA_code()+" - "+a.item.getDestination().getIATA_code();
            double qos=a.value;
            double percentage=(qos*100)/totalQos;
            String percentageStr = " ("+String.format("%.0f", percentage)+"%)";
            String s=name+percentageStr;
            PieChart.Data d=new PieChart.Data(s,qos);
            RoutepieChartData.add(d);
        }
        RoutePiechart.setData(RoutepieChartData);
        RoutePiechart.setTitle("Most served routes:");
        RoutePiechart.setLegendVisible(false);

        for (final PieChart.Data data : AirlinePiechart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            System.out.println(mostServedAirlines.size());

                            for(RankingItem<Airline> a:mostServedAirlines){
                                if(a.value==data.getPieValue()){
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

        for (final PieChart.Data data : RoutePiechart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            System.out.println(mostServedRoutes.size());

                            for(RankingItem<Route> a:mostServedRoutes){
                                String routeId=a.item.getOrigin().getIATA_code()+" - "+a.item.getDestination().getIATA_code();
                                if(a.value==data.getPieValue()){
                                    Start.route=a.item;
                                    try{
                                        System.out.println(String.valueOf(a.item.getDestination().getIATA_code() + "%"));
                                        switchToRouteScreen();
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


}
