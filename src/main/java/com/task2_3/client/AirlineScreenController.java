
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
    PieChart AirportPiechart;
    private ArrayList<Airport> airportRows=new ArrayList<>();

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
        airlineLabel.setText("Airline: "+Start.airline.getName());
        AirlineStatistics rs=Start.airline.getStats();
        qosText.setText(String.valueOf(rs.getQosIndicator()));
        delayProbText.setText(String.valueOf(rs.fifteenDelayProb));
        meanDelayText.setText(String.valueOf(rs.fifteenDelayProb));
        cancProbText.setText(String.valueOf(rs.cancellationProb));

        ObservableList<PieChart.Data> AirportpieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("aaaaaaaaaaa", 13),
                        new PieChart.Data("aaaaaaaaaaa", 25),
                        new PieChart.Data("aaaaaaaaaaa", 10),
                        new PieChart.Data("aaaaaaaaaaa", 22),
                        new PieChart.Data("aaaaaaaaaaa", 30),
                        new PieChart.Data("aaaaaaaaaaa", 13));
        AirportPiechart.setData(AirportpieChartData);
        for (final PieChart.Data data : AirportPiechart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            System.out.println(String.valueOf(data.getPieValue()) + "%");
                            for(Airport a:airportRows){
                                if(a.getName().equals(String.valueOf(data.getPieValue()))){
                                    Start.airport=a;
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
