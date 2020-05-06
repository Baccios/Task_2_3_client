package com.task2_3.client;

import java.util.HashMap;
import java.util.Objects;

public class Airline {
    private String identifier;
    private String name;
    private AirlineStatistics stats;

    public Airline(String id){
        this.identifier = id;
        this.name = null;
        this.stats = null;
    }

    public Airline(String id, String name){
        this.identifier = id;
        this.name = name;
        stats = null;
    }

    public Airline(String id, String name, AirlineStatistics stats) {
        this.identifier = id;
        this.name = name;
        this.stats = stats;
    }

    private boolean isComplete(){
        return (this.identifier != null)&&
                (this.name != null)&&
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
            graph.getAirline_byAirline(this);
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        checkCompleteAndFetch();
        return name;
    }

    public AirlineStatistics getStats() {
        checkCompleteAndFetch();
        return stats;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStats(AirlineStatistics stats) {
        this.stats = stats;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Airline airline = (Airline) o;
        return identifier.equals(airline.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    public void setFields(String identifier, String name, AirlineStatistics tmpAirlineStats) {
        this.identifier = identifier;
        this.name = name;
        this.stats = tmpAirlineStats;
    }

    @Override
    public String toString() {
        return "Airline{" +
                "identifier='" + identifier + '\'' +
                ", name='" + name + '\'' +
                ", stats=" + stats +
                '}';
    }

    public void setFields(Airline tmp) {
        this.identifier = tmp.getIdentifier();
        this.name = tmp.getName();
        this.stats = tmp.getStats();
    }
}