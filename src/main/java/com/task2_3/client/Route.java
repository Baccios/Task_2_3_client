package com.task2_3.client;

import java.util.Objects;

public class Route {
    private Airport origin;
    private Airport destination;
    private RouteStatistics stats;

    public Route(Airport origin, Airport destination){
        this.origin = origin;
        this.destination = destination;
        this.stats = null;
    }

    public Route(Airport origin, Airport destination, RouteStatistics stats) {
        this.origin = origin;
        this.destination = destination;
        this.stats = stats;
    }

    private boolean isComplete(){
        return (this.origin != null)&&
                (this.destination != null)&&
                (this.stats != null)&&
                (this.stats.isComplete());
    }

    private void checkCompleteAndFetch(){
        System.out.println("controllo");
        if(!this.isComplete()){
            System.out.println("cerco");
            Neo4jDBManager graph = Neo4jDBManager.getInstance();
            graph.getRoute_byRoute(this);
        }
    }

    /**
     * Generate the route map identifier
     * @return the identifier
     */
    public String toCode() {
        return this.origin.getIATA_code()+this.destination.getIATA_code();
    }

    @Override
    public String toString() {
        return "Route{" +
                "origin=" + origin.getIATA_code() +
                ", destination=" + destination.getIATA_code() +
                '}';
    }

    public Airport getOrigin() {
        return origin;
    }

    public void setOrigin(Airport origin) {
        this.origin = origin;
    }

    public Airport getDestination() {
        return destination;
    }

    public void setDestination(Airport destination) {
        this.destination = destination;
    }

    public RouteStatistics getStats() {
        if(stats == null || !stats.isComplete())
            checkCompleteAndFetch();
        return stats;
    }

    public void setStats(RouteStatistics stats) {
        this.stats = stats;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return origin.equals(route.origin) &&
                destination.equals(route.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(origin, destination);
    }

    public void setFields(Airport origin, Airport destination, RouteStatistics tmpRouteStats) {
        this.origin = origin;
        this.destination = destination;
        this.stats = tmpRouteStats;
    }

    public void setFields(Route route) {
        this.origin = route.origin;
        this.destination = route.destination;
        this.stats = route.stats;
    }
}