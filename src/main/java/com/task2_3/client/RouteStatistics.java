package com.task2_3.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RouteStatistics extends Statistics {
    private String mostLikelyCauseDelay;
    private String mostLikelyCauseCanc;
    private double meanDelay;
    private ArrayList<RankingItem<Airline>> bestAirlines; //each element is an array [QoS_indicator, Airline]


    //RouteStatistics constructor is never called, since we need two query methods to initialize all the attributes.
    public RouteStatistics(double cancellationProb, double fifteenDelayProb, String mostLikelyCauseDelay, String mostLikelyCauseCanc, double meanDelay, ArrayList<RankingItem<Airline>> bestAirlines) {
        super(cancellationProb, fifteenDelayProb);
        this.mostLikelyCauseDelay = mostLikelyCauseDelay;
        this.mostLikelyCauseCanc = mostLikelyCauseCanc;
        this.meanDelay = meanDelay;
        this.bestAirlines = new ArrayList<>(bestAirlines);
    }

    public RouteStatistics(double cancellationProb, double fifteenDelayProb, String mostLikelyCauseDelay, String mostLikelyCauseCanc, double meanDelay) {
        super(cancellationProb, fifteenDelayProb);
        this.mostLikelyCauseDelay = mostLikelyCauseDelay;
        this.mostLikelyCauseCanc = mostLikelyCauseCanc;
        this.meanDelay = meanDelay;
        this.bestAirlines = null;
    }

    public RouteStatistics(){
        super();
        this.bestAirlines = null;
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

    public double getMeanDelay() {
        return meanDelay;
    }

    public void setMeanDelay(double meanDelay) {
        this.meanDelay = meanDelay;
    }

    public ArrayList<RankingItem<Airline>> getBestAirlines() {
        return bestAirlines;
    }

    public void setBestAirlines(ArrayList<RankingItem<Airline>> bestAirlines) {
        this.bestAirlines = bestAirlines;
    }

    @Override
    public String toString() {
        return "RouteStatistics{" +
                "mostLikelyCauseDelay='" + mostLikelyCauseDelay + '\'' +
                ", mostLikelyCauseCanc='" + mostLikelyCauseCanc + '\'' +
                ", meanDelay=" + meanDelay +
                ", bestAirlines=" + bestAirlines +
                '}';
    }

    public boolean isComplete() {
        return (this.bestAirlines != null);
    }
}