package com.task2_3.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HintsScreenController implements Initializable {

    @FXML
    TableView tableView;
    private int selectedIndex;
    private ObservableList<RouteBean> routeBeanRows;
    private ArrayList<Route> routeRows;

    @FXML
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        routeBeanRows=FXCollections.observableArrayList();
        routeRows=Start.neoDbManager.searchSimilarRoutes_byOriginAndDest(Start.hintOrigin,Start.hintDestination);
        System.out.println(Start.hintOrigin.getName()+Start.hintDestination.getName());
        System.out.println(routeRows.size());
        for(Route r: routeRows){
            routeBeanRows.add(r.toBean());
            System.out.println(r.toString());
        }

        TableColumn originAirportCol = new TableColumn("Origin airport");
        originAirportCol.setCellValueFactory(
                new PropertyValueFactory<RouteBean, String>("originAirport")
        );
        originAirportCol.setStyle("-fx-alignment:CENTER");

        TableColumn destinationAirportCol = new TableColumn("Destination airport");
        destinationAirportCol.setCellValueFactory(
                new PropertyValueFactory<RouteBean, String>("destinationAirport")
        );
        destinationAirportCol.setStyle("-fx-alignment:CENTER");

        tableView.setItems(routeBeanRows);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(originAirportCol);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.getColumns().addAll(destinationAirportCol);


        tableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getButton().equals(MouseButton.PRIMARY)) {
                selectedIndex = tableView.getSelectionModel().getSelectedIndex();
                //Verify if a row is selected
                if (selectedIndex < 0)
                    return;
                Start.route = routeRows.get(selectedIndex);
                try {
                    switchToRouteScreen();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    @FXML
    private void switchToRouteScreen() throws IOException {
        Start.setRoot("routeScreen");
    }
    @FXML
    private void switchToOverallStats() throws IOException {
        Start.setRoot("overallStatsScreen");
    }

}
