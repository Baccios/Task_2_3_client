package com.task2_3.client;

import java.util.Objects;

public class Airport {
    private String IATA_code;
    private String name;
    private String city;
    private String state;
    private AirportStatistics stats;

    public Airport(){}

    public Airport(String IATA_code){
        this.IATA_code = IATA_code;
        this.name = null;
        this.city = null;
        this.state = null;
        this.stats = null;
    }

    public Airport(String IATA_code, String name, String city, String state) {
        this.IATA_code = IATA_code;
        this.name = name;
        this.city = city;
        this.state = state;
        this.stats = null;
    }

    public Airport(String IATA_code, String name, String city, String state, AirportStatistics stats) {
        this.IATA_code = IATA_code;
        this.name = name;
        this.city = city;
        this.state = state;
        this.stats = stats;
    }

    private boolean isComplete(){
        return (this.IATA_code != null)&&
                (this.name != null)&&
                (this.city != null)&&
                (this.state != null)&&
                (this.stats.isComplete());
    }


    /*
    * Check if all the fields of the object are initialized, in case fetch the database to retrieve them
    * */
    private void checkCompleteAndFetch(){
        System.out.println("controllo");
        if(!this.isComplete()){
            System.out.println("cerco");
            Neo4jDBManager graph = Neo4jDBManager.getInstance();
            graph.getAirport_byAirport(this);
        }
    }

    public void setIATA_code(String IATA_code) {
        this.IATA_code = IATA_code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setStats(AirportStatistics stats) {
        this.stats = stats;
    }

    public String getIATA_code() {
        return IATA_code;
    }

    public String getName() {
        checkCompleteAndFetch();
        return name;
    }

    public String getCity() {
        checkCompleteAndFetch();
        return city;
    }

    public String getState() {
        checkCompleteAndFetch();
        return state;
    }

    public AirportStatistics getStats() {
        checkCompleteAndFetch();
        return stats;
    }

    @Override
    public String toString() {
        return "Airport{" +
                "IATA_code='" + IATA_code + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", stats=" + stats +
                '}';
    }

    /*
    * Override equals in order to use native "indexOf" of ArrayList structure
    * */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Airport a = (Airport) o;

        return a.IATA_code.equals(this.IATA_code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(IATA_code, name, city, state, stats);
    }

    public void setFields(String IATA_code, String name, String city, String state, AirportStatistics stats) {
        this.IATA_code = IATA_code;
        this.name = name;
        this.city = city;
        this.state = state;
        this.stats = stats;
    }

    public void setFields(Airport airport){
        this.IATA_code = airport.IATA_code;
        this.name = airport.name;
        this.city = airport.city;
        this.state = airport.state;
        this.stats = airport.stats;
    }
}