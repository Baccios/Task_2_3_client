package com.task2_3.client;
import javafx.beans.property.SimpleStringProperty;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RouteBean {
    private SimpleStringProperty originAirport;
    private SimpleStringProperty destinationAirport;


    public RouteBean(String originAirport,String destinationAirport){
        this.originAirport=new SimpleStringProperty(originAirport);
        this.destinationAirport=new SimpleStringProperty(destinationAirport);
    }
    public String getOriginAirport(){  return originAirport.get(); }
    public String getDestinationAirport(){  return destinationAirport.get(); }

}
