package com.task2_3.client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
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

public class OverallStatsScreenController implements Initializable {
    ArrayList<String> s=new ArrayList<>();
    @FXML
    AutoCompleteComboBox airportBox;
    @FXML
    AutoCompleteComboBox airlineBox;
    @FXML
    AutoCompleteComboBox originAirportBox;
    @FXML
    AutoCompleteComboBox destinationAirportBox;

    @FXML
    private PieChart AirlinePiechart;
    @FXML
    private PieChart AirportPiechart;
    private ArrayList<RankingItem<Airline>> bestAirlines=new ArrayList<>();
    private ArrayList<RankingItem<Airport>> bestAirports=new ArrayList<>();
    private ScheduledFuture<?> schedFutureAirport;
    private ScheduledExecutorService sesAirport;
    private ScheduledFuture<?> schedFutureAirline;
    private ScheduledExecutorService sesAirline;
    private ScheduledFuture<?> schedFutureOriginAirport;
    private ScheduledExecutorService sesOriginAirport;
    private ScheduledFuture<?> schedFutureDestinationAirport;
    private ScheduledExecutorService sesDestinationAirport;
    private ArrayList<Airport> suggestedAirports=new ArrayList<>();
    private ArrayList<Airline> suggestedAirlines=new ArrayList<>();
    private ArrayList<Route> suggestedRoutes=new ArrayList<>();
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

    private AutoCompletionBinding<String> autoCompletionBinding;


    @FXML
    private void switchToInitialScreen() throws IOException {
        Start.setRoot("initialScreen");
    }
    @FXML
    private void switchToAirportScreen() throws IOException {
        Start.setRoot("airportScreen");
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
     ryBGpYA3AaYAnR5   });*/

        ObservableList<PieChart.Data> AirlinepieChartData=FXCollections.observableArrayList();
        bestAirlines= Start.neoDbManager.getOverallBestAirline();
        for(RankingItem<Airline> a:bestAirlines){
            String name=a.item.getName();
            double qos=a.value;
            PieChart.Data d=new PieChart.Data(name,qos);
            AirlinepieChartData.add(d);

        }
        AirlinePiechart.setData(AirlinepieChartData);
        AirlinePiechart.setLegendVisible(false);
        AirlinePiechart.setTitle("Airlines");

        ObservableList<PieChart.Data> AirportChartData=FXCollections.observableArrayList();
        bestAirports= Start.neoDbManager.getOverallBestAirport();
        for(RankingItem<Airport> a:bestAirports){
            String name=a.item.getName();
            double qos=a.value;
            PieChart.Data d=new PieChart.Data(name,qos);
            AirportChartData.add(d);
        }
        AirportPiechart.setData(AirportChartData);
        AirportPiechart.setLegendVisible(false);
        AirportPiechart.setTitle("Airport");

        for (final PieChart.Data data : AirportPiechart.getData()) {
            data.getNode().addEventHandler(MouseEvent.MOUSE_PRESSED,
                    new EventHandler<MouseEvent>() {
                        @Override public void handle(MouseEvent e) {
                            for(RankingItem<Airport> a:bestAirports){
                                if(a.item.getName().equals(String.valueOf(data.getName()))){
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

        //as a character is digited in input the array of matching airport is updated.
        airportBox.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    if(airportBox.getValue() == null){
                        System.out.println("Insert something.");
                    }
                    else{
                        System.out.println("You have inserted a valid Airport: "+airportBox.getValue().toString());
                        for(Airport a:suggestedAirports){
                            System.out.println(a.getName());
                            if(airportBox.getValue().toString().equals(a.toString())){
                                Start.airport=a;
                                try{switchToAirportScreen();}
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    System.out.println("You have inserted invalid Airport");
                    event.consume();
                    return;
                }else if(event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT){
                    return;
                }
                else {
                    //After releasing a key in input the timer starts: after 1 second we can retrieve the menu. In the meanwhile,if the user inserts a new input the timer is restarted.
                    if(sesAirport!=null) {
                        System.out.println(schedFutureAirport.getDelay(TimeUnit.MILLISECONDS));
                        schedFutureAirport.cancel(true);
                        System.out.println(schedFutureAirport.getDelay(TimeUnit.MILLISECONDS));
                    }
                    Task<Void> longRunningTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Platform.runLater(() -> showAirportMenu());
                            return null;
                        }
                    };
                    Thread t=new Thread(longRunningTask);
                    sesAirport = Executors.newSingleThreadScheduledExecutor();
                    schedFutureAirport=sesAirport.schedule(t,1, TimeUnit.SECONDS);
                }
            }
        });

        airlineBox.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    if(airlineBox.getValue() == null){
                        System.out.println("Insert something.");
                    }else{
                        System.out.println("You have inserted Airline: "+airlineBox.getValue().toString());
                        for(Airline a:suggestedAirlines){
                            System.out.println(a.getName());
                            if(airlineBox.getValue().toString().equals(a.toString())){
                                Start.airline=a;
                                try{switchToAirlineScreen();}
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        System.out.println("You have inserted invalid Airline");
                    }
                    event.consume();
                    return;
                }else if(event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT){
                    return;
                }
                else {
                    //After releasing a key in input the timer starts: after 1 second we can retrieve the menu. In the meanwhile,if the user inserts a new input the timer is restarted.
                    if(sesAirline!=null) {
                        schedFutureAirline.cancel(true);
                    }
                    Task<Void> longRunningTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Platform.runLater(() -> showAirlineMenu());
                            return null;
                        }
                    };
                    Thread t=new Thread(longRunningTask);
                    sesAirline = Executors.newSingleThreadScheduledExecutor();
                    schedFutureAirline=sesAirline.schedule(t,1, TimeUnit.SECONDS);
                }
            }
        });

        originAirportBox.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    if(originAirportBox.getValue() == null || destinationAirportBox.getValue() == null){
                        System.out.println("Insert something.");
                    }else{
                        System.out.println("You have inserted Route: "+originAirportBox.getValue().toString()+" - "+destinationAirportBox.getValue().toString());
                        for(Route a:suggestedRoutes){
                            System.out.println(a.getOrigin().getName()+" - "+a.getDestination().getName());
                            if((originAirportBox.getValue().toString().equals(a.getOrigin().getName())) && (destinationAirportBox.getValue().toString().equals(a.getDestination().getName()))){
                                Start.route=a;
                                try{switchToRouteScreen();}
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        System.out.println("Invalid route");
                    }
                    event.consume();
                    return;
                }else if(event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT){
                    return;
                }
                else {
                    //After releasing a key in input the timer starts: after 1 second we can retrieve the menu. In the meanwhile,if the user inserts a new input the timer is restarted.
                    if(sesOriginAirport!=null) {
                        schedFutureOriginAirport.cancel(true);
                    }
                    Task<Void> longRunningTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Platform.runLater(() -> showOriginAirportMenu());
                            return null;
                        }
                    };
                    Thread t=new Thread(longRunningTask);
                    sesOriginAirport = Executors.newSingleThreadScheduledExecutor();
                    schedFutureOriginAirport=sesOriginAirport.schedule(t,1, TimeUnit.SECONDS);
                }
            }
        });

        destinationAirportBox.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if(event.getCode() == KeyCode.ENTER) {
                    if(originAirportBox.getValue() == null || destinationAirportBox.getValue() == null){
                        System.out.println("Insert something.");
                    }else{
                        System.out.println("You have inserted Route: "+originAirportBox.getValue().toString()+" - "+destinationAirportBox.getValue().toString());
                        for(Route a:suggestedRoutes){
                            System.out.println(a.getOrigin().getName()+" - "+a.getDestination().getName());
                            if((originAirportBox.getValue().toString().equals(a.getOrigin().getName())) && (destinationAirportBox.getValue().toString().equals(a.getDestination().getName()))){
                                Start.route=a;
                                try{switchToRouteScreen();}
                                catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        }
                        System.out.println("Invalid route");
                    }
                    event.consume();
                    return;
                }else if(event.getCode() == KeyCode.UP || event.getCode() == KeyCode.DOWN || event.getCode() == KeyCode.LEFT || event.getCode() == KeyCode.RIGHT){
                    return;
                }
                else {
                    //After releasing a key in input the timer starts: after 1 second we can retrieve the menu. In the meanwhile,if the user inserts a new input the timer is restarted.
                    if(sesDestinationAirport!=null) {
                        schedFutureDestinationAirport.cancel(true);
                    }
                    Task<Void> longRunningTask = new Task<Void>() {
                        @Override
                        protected Void call() throws Exception {
                            Platform.runLater(() -> showDestinationAirportMenu());
                            return null;
                        }
                    };
                    Thread t=new Thread(longRunningTask);
                    sesDestinationAirport = Executors.newSingleThreadScheduledExecutor();
                    schedFutureDestinationAirport=sesDestinationAirport.schedule(t,1, TimeUnit.SECONDS);
                }
            }
        });
    }

    public void showAirlineMenu(){
        suggestedAirlines = Start.neoDbManager.searchAirlines_byString(airlineBox.getEditor().getText());
        airlineBox.objectChoices.clear();
        for (Airline object : suggestedAirlines) {
            airlineBox.objectChoices.add(object);
        }
        airlineBox.show();
        if (!airlineBox.objectChoices.isEmpty()) {
            airlineBox.show();
        } else {
            airlineBox.hide();
        }
    }

    public void showAirportMenu(){
        suggestedAirports = Start.neoDbManager.searchAirports_byString(airportBox.getEditor().getText());
        airportBox.objectChoices.clear();
        for (Airport object : suggestedAirports) {
            airportBox.objectChoices.add(object);
            System.out.println(object.getName());
        }
        airportBox.show();
        if (!airportBox.objectChoices.isEmpty()) {
            airportBox.show();
        } else {
            airportBox.hide();
        }
    }

    public void showOriginAirportMenu(){
        suggestedRoutes = Start.neoDbManager.searchRoutes_byObject(originAirportBox.getEditor().getText(),destinationAirportBox.getEditor().getText());
        originAirportBox.objectChoices.clear();
        for (Route object : suggestedRoutes) {
            originAirportBox.objectChoices.add(object.getOrigin());
            System.out.println(object.getOrigin().getName());
        }
        originAirportBox.show();
        if (!originAirportBox.objectChoices.isEmpty()) {
            originAirportBox.show();
        } else {
            originAirportBox.hide();
        }
    }

    public void showDestinationAirportMenu(){
        suggestedRoutes = Start.neoDbManager.searchRoutes_byObject(originAirportBox.getEditor().getText(),destinationAirportBox.getEditor().getText());
        destinationAirportBox.objectChoices.clear();
        for (Route object : suggestedRoutes) {
            destinationAirportBox.objectChoices.add(object.getDestination());
            System.out.println(object.getDestination().getName());
        }
        destinationAirportBox.show();
        if (!destinationAirportBox.objectChoices.isEmpty()) {
            destinationAirportBox.show();
        } else {
            destinationAirportBox.hide();
        }
    }


}
