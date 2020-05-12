package com.task2_3.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.*;

import java.io.IOException;
import java.util.ArrayList;

public class Start extends Application{
    public static Scene scene;
    public static Admin_Protocol_Client adminManager;
    public static Neo4jDBManager neoDbManager;
    //Entities that are selected by user to be analyzed.
    public static Route route;
    public static Airline airline;
    public static Airport airport;

    public void start(Stage stage)throws IOException{
        adminManager=new Admin_Protocol_Client("localhost",2020); //TODO insert correct server ip and port
        neoDbManager=Neo4jDBManager.getInstance();
        scene = new Scene(loadFXML("initialScreen"));
        stage.setWidth(840);
        stage.setHeight(630);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Start.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void main (String[] args) {
        //launch();
        /*    System.out.println("Hello my baby!");
        Admin_Protocol_Client client = new Admin_Protocol_Client("localhost",2020);
        client.startAuthHandshake("admin","ciaccio");
        Admin_Protocol_Client client2 = new Admin_Protocol_Client("localhost",2020);
        Admin_Protocol_Client client3 = new Admin_Protocol_Client("localhost",2020);
        client2.startAuthHandshake("admin","ciaccio");
        client3.startAuthHandshake("admin","fronchio");
        client.requestCheckout();
        client.close();
        client2.close();
        client3.close();
        client2 = new Admin_Protocol_Client("localhost",2020);
        client2.startAuthHandshake("admin","patacchio"); //wrong credentials
        client2.close();
        client3 = new Admin_Protocol_Client("localhost",2020);
        client3.startAuthHandshake("admin","ciaccio");
        client3.requestCredentials("admin", "patacchio");
        client3.requestScrape();
        client3.requestCheckout();
        client3.close();
        client2 = new Admin_Protocol_Client("localhost",2020);
        client2.startAuthHandshake("admin","patacchio");
        client2.requestCheckout();
        client2.close();*/


/*
        Neo4jDBManager graph = Neo4jDBManager.getInstance();
        Airport a = graph.getAirport_byIataCode("CLT");
        Airport b = graph.getAirport_byIataCode("OAJ");

        Airline air = graph.getAirline_byIdentifier("9E");
        System.out.println("Airline "+air.getIdentifier()+"has "+air.getFirstPlacesCount()+" first places over "+air.getTotalServedRoutes()+" total routes");
        System.out.println("Airport "+a.getName()+" can reach "+a.getTwoHopsDestinations()+" destinations in at most two hops");
        ArrayList<Route> list = graph.searchSimilarRoutes_byOriginAndDest(b,a);
        for (Route r : list) {
            System.out.println("I suggest "+r.getOrigin().getName()+", "+r.getOrigin().getState()+" --> "+r.getDestination().getName());
        }

        graph.close(); */

        /*
        graph.searchRoutes_byObject(a, b);
        ArrayList<Airport> tmpa = graph.searchAirports_byString("charlotte nc");

        ArrayList<Airline> tmp = graph.searchAirlines_byString("g4");

        Airline r = graph.getAirline_byIdentifier("OO");
        System.out.println("1");
        Airport ord = r.getStats().mostServedAirports.get(0).item;
        System.out.println("2");
        System.out.println(ord.getStats().toString());
        System.out.println("3");
        Airport ord2 = graph.getAirport_byIataCode("ORD");
        System.out.println("4");
        System.out.println(ord.getStats().toString());
        //graph.getRoute_byOriginAndDestinationIATACode("TYS", "ORD");
        //System.out.println(r.getStats().mostServedAirports.get(0).item.toString());

*/
        Neo4jDBManager graph = Neo4jDBManager.getInstance();
        graph.getOverallBestAirline();
    }
}