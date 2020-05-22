package com.task2_3.client;

import java.util.HashMap;
import java.util.Objects;

public class Airline {
    private String identifier;
    private String name;
    private AirlineStatistics stats;

    //These two fields are information retrieved from graph oriented queries on the database, and not from MongoDB analytics
    private int totalServedRoutes;
    private int firstPlacesCount;

    public Airline(String id){
        this.identifier = id;
        this.name = null;
        this.stats = null;
        this.totalServedRoutes = -1;
        this.firstPlacesCount = -1;
    }

    public Airline(String id, String name){
        this.identifier = id;
        this.name = name;
        stats = null;
        this.totalServedRoutes = -1;
        this.firstPlacesCount = -1;
    }

    public Airline(String id, String name, AirlineStatistics stats) {
        this.identifier = id;
        this.name = name;
        this.stats = stats;
        this.totalServedRoutes = -1;
        this.firstPlacesCount = -1;
    }

    private boolean isComplete(){
        return (this.identifier != null)&&
                (this.name != null)&&
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
            graph.getAirline_byAirline(this);
        }
    }

    public String getIdentifier() {
        return identifier;
    }

    public String getName() {
        if(name == null)
            checkCompleteAndFetch();
        return name;
    }

    public AirlineStatistics getStats() {
        if(stats == null || !stats.isComplete())
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
        return this.getName()+", "+this.getIdentifier();
    }

    public void setFields(Airline tmp) {
        this.identifier = tmp.getIdentifier();
        this.name = tmp.getName();
        this.stats = tmp.getStats();
    }

    private void loadGlobalRoutesStats() {
        int[] tmp = Neo4jDBManager.getInstance().getFirstPlacesCount_ByAirline(this);
        if(tmp == null) {
            return;
        }
        firstPlacesCount = tmp[0];
        totalServedRoutes = tmp[1];
    }

    public int getTotalServedRoutes() {
        if(totalServedRoutes == -1) {
            loadGlobalRoutesStats();
        }
        return totalServedRoutes;
    }

    public int getFirstPlacesCount() {
        if(firstPlacesCount == -1) {
            loadGlobalRoutesStats();
        }
        return firstPlacesCount;
    }
}