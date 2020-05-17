package com.task2_3.client;

import java.util.Objects;

public class Airport {
    private String IATA_code;
    private String name;
    private String city;
    private String state;
    private AirportStatistics stats;

    //this field is an information retrieved from graph oriented queries, and not from MongoDB analytics
    private int twoHopsDestinations;

    public Airport(String IATA_code){
        this.IATA_code = IATA_code;
        this.name = null;
        this.city = null;
        this.state = null;
        this.stats = null;
        this.twoHopsDestinations = -1;
    }

    public Airport(String IATA_code, String name, String city, String state) {
        this.IATA_code = IATA_code;
        this.name = name;
        this.city = city;
        this.state = state;
        this.stats = null;
        this.twoHopsDestinations = -1;
    }

    public Airport(String IATA_code, String name, String city, String state, AirportStatistics stats) {
        this.IATA_code = IATA_code;
        this.name = name;
        this.city = city;
        this.state = state;
        this.stats = stats;
        this.twoHopsDestinations = -1;
    }

    private boolean isComplete(){
        return (this.IATA_code != null)&&
                (this.name != null)&&
                (this.city != null)&&
                (this.state != null)&&
                (this.stats != null)&&
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
        if(name == null)
            checkCompleteAndFetch();
        return name;
    }

    public String getCity() {
        if(city == null)
            checkCompleteAndFetch();
        return city;
    }

    public String getState() {
        if(state == null)
            checkCompleteAndFetch();
        return state;
    }

    public AirportStatistics getStats() {
        if(stats == null || !stats.isComplete())
            checkCompleteAndFetch();
        return stats;
    }

    @Override
    public String toString() {
        /*return "Airport{" +
                "IATA_code='" + IATA_code + '\'' +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", stats=" + stats +
                '}';*/
        return this.name + ", " + this.IATA_code;
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

    public int getTwoHopsDestinations() {
        if(twoHopsDestinations != -1) {
            return twoHopsDestinations;
        }
        twoHopsDestinations = Neo4jDBManager.getInstance().getTwoHopsDestinationsCount(this);
        return twoHopsDestinations;
    }
}