package com.task2_3.client;

import java.util.ArrayList;
import java.util.HashMap;

public class AirportStatistics extends Statistics {

    public double importance;
    public double qosIndicator;
    public String mostLikelyCauseDelay;
    public String mostLikelyCauseCanc;
    public ArrayList<RankingItem<Airline>> mostServedAirlines; //each element is an array [Percentage, Airline]
    public ArrayList<RankingItem<Route>> mostServedRoutes; //each element is an array [Percentage, Route]
    //public HashMap<Double, Route> mostServedRoutes;
    //public HashMap<Double, Airline> mostServedAirlines;

    public AirportStatistics(double cancellationProb, double fifteenDelayProb, double importance, String mostLikelyCauseDelay, String mostLikelyCauseCanc, ArrayList<RankingItem<Route>> mostServedRoutes, ArrayList<RankingItem<Airline>> mostServedAirlines) {
        super(cancellationProb, fifteenDelayProb);
        this.importance = importance;
        this.mostLikelyCauseDelay = mostLikelyCauseDelay;
        this.mostLikelyCauseCanc = mostLikelyCauseCanc;
        this.mostServedRoutes = new ArrayList<>(mostServedRoutes);
        this.mostServedAirlines = new ArrayList<>(mostServedAirlines);
    }

    public AirportStatistics(){
        super();
        mostServedAirlines = null;
        mostServedRoutes = null;
    }
}