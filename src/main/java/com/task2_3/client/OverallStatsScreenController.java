package com.task2_3.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import org.controlsfx.control.textfield.TextFields;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class OverallStatsScreenController implements Initializable {
    @FXML
    private TableView airlineTableView;
    @FXML
    private TableView airportTableView;
    @FXML
    private PieChart AirlinePiechart;
    @FXML
    private PieChart AirportPiechart;
    private ArrayList<Airline> airlineRows=new ArrayList<>();
    private ArrayList<Airport> airportRows=new ArrayList<>();
    private int selectedIndex;

    @FXML
    private TextField airlineInput;
    @FXML
    private TextField airportInput;
    @FXML
    private TextField originAirportInput;
    @FXML
    private TextField destinationAirportInput;
    @FXML
    private Label errorLabel;


    @FXML
    private void switchToInitialScreen() throws IOException {
        Start.setRoot("initialScreen");
    }@FXML
    private void switchToAirportScreen() throws IOException {
        Start.setRoot("initialScreen");
    }@FXML
    private void switchToAirlineScreen() throws IOException {
        Start.setRoot("initialScreen");
    }

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources){
   /*     TableColumn airportCol = new TableColumn("Airports");
        airportCol.setCellValueFactory(
                new PropertyValueFactory<AirportBean,String>("airport")
        );
        airportCol.setStyle("-fx-alignment:CENTER");

        TableColumn airlineCol = new TableColumn("Airlines");
        airlineCol.setCellValueFactory(
                new PropertyValueFactory<AirlineBean,String>("airline")
        );
        airlineCol.setStyle("-fx-alignment:CENTER");

        airportTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        airportTableView.getColumns().addAll(airportCol);
        airlineTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        airlineTableView.getColumns().addAll(airlineCol);

        airportTableView.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                selectedIndex=airportTableView.getSelectionModel().getSelectedIndex();
                //Verify if a row is selected
                if(selectedIndex<0)
                    return;
                Start.airport=airportRows.get(selectedIndex);
                try{
                    switchToAirportScreen();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        airlineTableView.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                selectedIndex=airlineTableView.getSelectionModel().getSelectedIndex();
                //Verify if a row is selected
                if(selectedIndex<0)
                    return;
                Start.airline=airlineRows.get(selectedIndex);
                try{
                    switchToAirlineScreen();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        });*/
   //TODO retrieve ranking of airports and airlines according to QoS and initialize airlinwRow and airportRow
        //Only consider the 6 best elements.
        ObservableList<PieChart.Data> AirlinepieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("aaaaaaaaaaa", 13),
                        new PieChart.Data("aaaaaaaaaaa", 25),
                        new PieChart.Data("aaaaaaaaaaa", 10),
                        new PieChart.Data("aaaaaaaaaaa", 22),
                        new PieChart.Data("aaaaaaaaaaa", 30));
        AirlinePiechart.setData(AirlinepieChartData);
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
        }   //as a character is digited in input the array of matching airport is updated.
        airportInput.addEventFilter(KeyEvent.KEY_RELEASED,
                new EventHandler<KeyEvent>() {
                @Override
                    public void handle(KeyEvent e) {
                        System.out.println(airportInput.getText());
                        ArrayList<Airport> matchingAirports=Start.neoDbManager.searchAirports_byString(airportInput.getText());
                        for(Airport a: matchingAirports) {
                            System.out.println(a.getName());
                        }
                    }
                });
        TextFields.bindAutoCompletion(airportInput,"ciao","ciaoo");
        airlineInput.addEventFilter(KeyEvent.KEY_RELEASED,
                new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        System.out.println(airlineInput.getText());
                        Start.neoDbManager.searchAirports_byString(airlineInput.getText());
                    }
                });
        airportInput.addEventFilter(KeyEvent.KEY_RELEASED,
                new EventHandler<KeyEvent>() {
                    @Override
                    public void handle(KeyEvent e) {
                        System.out.println(airportInput.getText());
                        Start.neoDbManager.searchAirports_byString(airportInput.getText());
                    }
                });
    /*    updateTables();*/
    }
    /*
    private void updateTables() {
        airlineRows.clear();
        airportRows.clear();
        ObservableList<Airline> observableAirlines= FXCollections.observableArrayList();
        ObservableList<Airport> observableAirports= FXCollections.observableArrayList();
        //TODO execute query to retrieve rankings according to QOS and assign to observables airportbeans and airlinebeans

    }
*/
    //Check if airline inputs are valid and access to statistics
    private void getInputAirlineStatistics(){
        if(airlineInput.getText().equals("")){
            errorLabel.setText("You must insert something!");
            errorLabel.setVisible(true);
            return;
        }
        else {
        //TODO RETRIEVE LIST OF AIRLINES
            Airline selectedAirline=null;
            if (selectedAirline == null) {
                errorLabel.setText("Selected airline doesn't exist.");
                errorLabel.setVisible(true);
                return;
            }
        Start.airline=selectedAirline;
            try{
                switchToAirlineScreen();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    //Check if airport inputs are valid and access to statistics
    private void getInputAirportStatistics(){
        if(airportInput.getText().equals("")){
            errorLabel.setText("You must insert something!");
            errorLabel.setVisible(true);
            return;
        }
        else {
            Airline selectedAirport=null;
            //TODO RETRIEVE LIST OF AIRLINES
            if (selectedAirport == null) {
                errorLabel.setText("Selected airport doesn't exist.");
                errorLabel.setVisible(true);
                return;
            }
            //check if the user selects a non existing doctor
            Start.airline=selectedAirport;
            try{
                switchToAirportScreen();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
