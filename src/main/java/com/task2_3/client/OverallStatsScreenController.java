package com.task2_3.client;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private void switchToOverallStats() throws IOException {
        Start.setRoot("signinScreen");
    }
    @FXML
    private void switchToInitialScreen() throws IOException {
        Start.setRoot("initialScreen");
    }
    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources){
        TableColumn airportCol = new TableColumn("Airports");
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
                switchToAirportScreen();
            }
        });
        airlineTableView.setOnMouseClicked((MouseEvent event) -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                selectedIndex=airlineTableView.getSelectionModel().getSelectedIndex();
                //Verify if a row is selected
                if(selectedIndex<0)
                    return;
                Start.airline=airlineRows.get(selectedIndex);
                switchToAirlineScreen();
            }
        });
        updateTables();
    }
    private void updateTables() {
        airlineRows.clear();
        airportRows.clear();
        ObservableList<Airline> observableAirlines= FXCollections.observableArrayList();
        ObservableList<Airport> observableAirports= FXCollections.observableArrayList();
        //TODO execute query to retrieve rankings according to QOS and assign to observables airportbeans and airlinebeans

    }

    private void getAirline(){
        if(airlineInput.getText().equals("")){
            errorLabel.setText("You must insert something!");
            errorLabel.setVisible(true);
            return;
        }
        else {
        //TODO RETRIEVE LIST OF AIRLINES
            if (selectedAirline == null) {
                errorLabel.setText("Selected airline doesn't exist.");
                errorLabel.setVisible(true);
                return;
            }
        //check if the user selects a non existing doctor
        Start.airline=selectedAirline;
        switchToAirlineScreen();
        }
    }
    private void getAirport(){
        if(airportInput.getText().equals("")){
            errorLabel.setText("You must insert something!");
            errorLabel.setVisible(true);
            return;
        }
        else {
            //TODO RETRIEVE LIST OF AIRLINES
            if (selectedAirport == null) {
                errorLabel.setText("Selected airport doesn't exist.");
                errorLabel.setVisible(true);
                return;
            }
            //check if the user selects a non existing doctor
            Start.airline=selectedAirport;
            switchToAirportScreen();
        }
    }

}
