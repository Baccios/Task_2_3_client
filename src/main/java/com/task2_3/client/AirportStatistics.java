package com.task2_3.client;

import java.util.ArrayList;
import java.util.HashMap;

public class AirportStatistics extends Statistics {

    private double qosIndicator;
    private String mostLikelyCauseDelay;
    private String mostLikelyCauseCanc;
    private ArrayList<RankingItem<Airline>> mostServedAirlines; //each element is an array [Percentage, Airline]
    private ArrayList<RankingItem<Route>> mostServedRoutes; //each element is an array [Percentage, Route]

    public AirportStatistics(double cancellationProb, double fifteenDelayProb, double qosIndicator, String mostLikelyCauseDelay, String mostLikelyCauseCanc) {
        super(cancellationProb, fifteenDelayProb);
        this.qosIndicator = qosIndicator;
        this.mostLikelyCauseDelay = mostLikelyCauseDelay;
        this.mostLikelyCauseCanc = mostLikelyCauseCanc;
        this.mostServedAirlines = null;
        this.mostServedRoutes = null;
    }

    public AirportStatistics(double cancellationProb, double fifteenDelayProb, double qos, String mostLikelyCauseDelay, String mostLikelyCauseCanc, ArrayList<RankingItem<Route>> mostServedRoutes, ArrayList<RankingItem<Airline>> mostServedAirlines) {
        super(cancellationProb, fifteenDelayProb);
        this.qosIndicator = qos;
        this.mostLikelyCauseDelay = mostLikelyCauseDelay;
        this.mostLikelyCauseCanc = mostLikelyCauseCanc;
        this.mostServedRoutes = new ArrayList<>(mostServedRoutes);
        this.mostServedAirlines = new ArrayList<>(mostServedAirlines);
    }

    public boolean isComplete(){
        return (this.mostServedAirlines != null)&&
                (this.mostServedRoutes != null);
    }

    /*private void checkCompleteAndFetch() {
        System.out.println("controllo stats");
        if(!this.isComplete()){
            System.out.println("cerco stats");
            Neo4jDBManager graph = Neo4jDBManager.getInstance();
            graph.fetchMostServedAirline_byAirport(null, this.);
        }
    }*/

    public double getQosIndicator() {
        return qosIndicator;
    }

    public void setQosIndicator(double qosIndicator) {
        this.qosIndicator = qosIndicator;
    }

    public String getMostLikelyCauseDelay() {
        return mostLikelyCauseDelay;
    }

    public void setMostLikelyCauseDelay(String mostLikelyCauseDelay) {
        this.mostLikelyCauseDelay = mostLikelyCauseDelay;
    }

    public String getMostLikelyCauseCanc() {
        return mostLikelyCauseCanc;
    }

    public void setMostLikelyCauseCanc(String mostLikelyCauseCanc) {
        this.mostLikelyCauseCanc = mostLikelyCauseCanc;
    }

    public ArrayList<RankingItem<Airline>> getMostServedAirlines() {
        return mostServedAirlines;
    }

    public void setMostServedAirlines(ArrayList<RankingItem<Airline>> mostServedAirlines) {
        this.mostServedAirlines = mostServedAirlines;
    }

    public ArrayList<RankingItem<Route>> getMostServedRoutes() {
        return mostServedRoutes;
    }

    public void setMostServedRoutes(ArrayList<RankingItem<Route>> mostServedRoutes) {
        this.mostServedRoutes = mostServedRoutes;
    }

    @Override
    public String toString() {
        return "AirportStatistics{" +
                "qosIndicator=" + qosIndicator +
                ", mostLikelyCauseDelay='" + mostLikelyCauseDelay + '\'' +
                ", mostLikelyCauseCanc='" + mostLikelyCauseCanc + '\'' +
                ", mostServedAirlines=" + mostServedAirlines +
                ", mostServedRoutes=" + mostServedRoutes +
                ", cancellationProb=" + cancellationProb +
                ", fifteenDelayProb=" + fifteenDelayProb +
                '}';
    }
}