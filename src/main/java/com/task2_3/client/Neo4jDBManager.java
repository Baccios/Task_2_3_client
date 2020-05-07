package com.task2_3.client;

import org.neo4j.driver.*;

import java.util.*;

import static org.neo4j.driver.Values.parameters;


public class Neo4jDBManager implements AutoCloseable {
    private final Driver driver;
    private Cache<Airport> airportCache;
    private Cache<Airline> airlineCache;
    private Cache<Route> routeCache;
    public static Neo4jDBManager graphInstance = null;

    private Neo4jDBManager(){
        //driver initialization using user and password of the neo4jdatabase
        driver = GraphDatabase.driver( "bolt://172.16.1.15:7687", AuthTokens.basic( "user", "userpass" ) );
        this.airportCache = new Cache<>();
        this.airlineCache = new Cache<>();
        this.routeCache = new Cache<>();
    }

    public static Neo4jDBManager getInstance(){
        if(graphInstance == null)
            graphInstance = new Neo4jDBManager();
        return graphInstance;
    }

    @Override
    public void close() {
        driver.close();
    }

    public Route getRoute_byOriginAndDestinationIATACode(String originIATACode, String destinationIATACode){
        return getRoute_byOriginAndDestinationAirport(
                new Airport(originIATACode),
                new Airport(destinationIATACode)
        );
    }

    public Route getRoute_byOriginAndDestinationAirport(Airport origin, Airport destination){
        return getRoute_byRoute(new Route(origin, destination));
    }

    public Route getRoute_byRoute(Route route) {
        Route tmp = route;

        if((tmp = routeCache.checkCache(route)) != null){
            route.setFields(tmp);
            return tmp;
        }

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                //fetch most served Airline
                ArrayList<RankingItem<Airline>> mostServedAirline = fetchMostServedAirline_byRoute(tx, route.getOrigin(), route.getDestination());

                Result resRoute = matchRouteNode_byOriginAirport(tx, route.getOrigin(), route.getDestination());
                /*
                 * asMap will permit to access the values by using "fieldName"
                 * */
                Map rec = resRoute.single().values().get(0).asMap();

                RouteStatistics tmpRouteStats = new RouteStatistics(
                        (Double)rec.get("cancellationProb"),
                        (Double)rec.get("fifteenDelayProb"),
                        (String)rec.get("mostLikelyCauseDelay"),
                        (String)rec.get("mostLikelyCauseCanc"),
                        (Double)rec.get("meanDelay"),
                        mostServedAirline
                );

                route.setFields(
                        route.getOrigin(),
                        route.getDestination(),
                        tmpRouteStats
                );

                routeCache.updateCache(route);

                return route;
            });
        }
    }

    public Airport getAirport_byIataCode(String iataCode){
        return getAirport_byAirport(new Airport(iataCode));
    }

    /*
    * Useful to mantain reference to the airport object
    * */
    public Airport getAirport_byAirport(Airport airport){
        Airport tmp = airport;

        if((tmp = airportCache.checkCache(tmp))!=null){
            System.out.println(airport.getIATA_code() + " trovato in cache!");
            airport.setFields(tmp);
            return airport;
        }

        String iataCode = airport.getIATA_code();

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                //fetch most served Airline
                ArrayList<RankingItem<Airline>> mostServedAirline = fetchMostServedAirline_byAirport(tx, iataCode);

                //fetch most server Route
                ArrayList<RankingItem<Route>> mostServedRoute = fetchMostServedRoute_byAirport(tx, iataCode);

                Result resAirport = matchAirportNode_byIataCode(tx, iataCode);
                /*
                 * asMap will permit to access the values by using "fieldName"
                 * */
                Map rec = resAirport.single().values().get(0).asMap();

                AirportStatistics tmpAirportStats = new AirportStatistics(
                        (Double)rec.get("cancellationProb"),
                        (Double)rec.get("fifteenDelayProb"),
                        (Double)rec.get("qosIndicator"),
                        (String)rec.get("mostLikelyCauseDelay"),
                        (String)rec.get("mostLikelyCauseCanc"),
                        mostServedRoute,
                        mostServedAirline
                );

                airport.setFields(
                        (String)rec.get("IATA_code"),
                        (String)rec.get("name"),
                        (String)rec.get("city"),
                        (String)rec.get("state"),
                        tmpAirportStats
                );

                airportCache.updateCache(airport);

                return airport;
            });
        }
    }

    public Airline getAirline_byIdentifier(String identifier){
        return getAirline_byAirline(new Airline(identifier));
    }

    public Airline getAirline_byAirline(Airline airline){
        Airline tmp = airline;

        if((tmp = airlineCache.checkCache(tmp)) != null){
            airline.setFields(tmp);
            return tmp;
        }

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                ArrayList<RankingItem<Airport>> mostServedAirport = fetchMostServedAirport_byAirline(tx, airline.getIdentifier());

                Result resAirline = matchAirlineNode_byIdentifier(tx, airline.getIdentifier());
                /*
                 * asMap will permit to access the values by using "fieldName"
                 * */
                Map rec = resAirline.single().values().get(0).asMap();

                AirlineStatistics tmpAirlineStats = new AirlineStatistics(
                        (Double)rec.get("cancellationProb"),
                        (Double)rec.get("fifteenDelayProb"),
                        (Double)rec.get("qosIndicator"),
                        mostServedAirport
                );

                airline.setFields(
                        (String)rec.get("identifier"),
                        (String)rec.get("name"),
                        tmpAirlineStats
                );

                airlineCache.updateCache(airline);

                return airline;
            });
        }
    }

    private ArrayList<RankingItem<Airport>> decompressMostServedAirport(Result resAirport) {
        Record tmpAirportRecord;
        Double percentage;
        Airport tmpAirport;
        Map tmpAirportMap;
        ArrayList<RankingItem<Airport>> mostServedAirport = new ArrayList<>();
        while(resAirport.hasNext()){
            tmpAirportRecord = resAirport.next();

            percentage = (Double) tmpAirportRecord.values().get(1).asMap().get("percentage");

            tmpAirportMap = tmpAirportRecord.values().get(0).asMap();
            tmpAirport = new Airport(
                    (String) tmpAirportMap.get("IATA_code"),
                    (String) tmpAirportMap.get("name"),
                    (String) tmpAirportMap.get("city"),
                    (String) tmpAirportMap.get("state"),
                    new AirportStatistics(
                            (Double) tmpAirportMap.get("cancellationProb"),
                            (Double) tmpAirportMap.get("fifteenDelayProb"),
                            (Double) tmpAirportMap.get("qosIndicator"),
                            (String) tmpAirportMap.get("mostLikelyCauseDelay"),
                            (String) tmpAirportMap.get("mostLikelyCauseCanc")
                    )
            );

            /*if(airportCache.checkCache(tmpAirport) == null)
                airportCache.updateCache(tmpAirport);*/

            mostServedAirport.add(new RankingItem<>(percentage, tmpAirport));
        }
        return mostServedAirport;
    }

    private ArrayList<RankingItem<Airline>> decompressMostServedAirline(Result resAirline){
        Record tmpAirlineRecord;
        Double value;
        Airline tmpAirline;
        Map tmpAirlineMap;
        ArrayList<RankingItem<Airline>> mostServedAirline = new ArrayList<>();
        while(resAirline.hasNext()){
            tmpAirlineRecord = resAirline.next();

            if((value =(Double) tmpAirlineRecord.values().get(1).asMap().get("percentage")) == null){
                value = (Double) tmpAirlineRecord.values().get(1).asMap().get("qosIndicator");
            }

            tmpAirlineMap = tmpAirlineRecord.values().get(0).asMap();

            tmpAirline = new Airline(
                    (String) tmpAirlineMap.get("identifier"),
                    (String) tmpAirlineMap.get("name"),
                    new AirlineStatistics(
                            (Double) tmpAirlineMap.get("cancellationProb"),
                            (Double) tmpAirlineMap.get("fifteenDelayProb"),
                            (Double) tmpAirlineMap.get("qosIndicator")
                    )
            );

            /*if(airlineCache.checkCache(tmpAirline) == null)
                airlineCache.updateCache(tmpAirline);*/

            mostServedAirline.add(new RankingItem<>(value, tmpAirline));
        }
        return mostServedAirline;
    }

    private ArrayList<RankingItem<Route>> decompressMostServedRoute(Result resRoute, String iataCode) {
        Record tmpRouteRecord;
        Double percentage;
        Route tmpRoute;
        Map tmpRouteMap;
        ArrayList<RankingItem<Route>> mostServedRoute = new ArrayList<>();
        while(resRoute.hasNext()){
            tmpRouteRecord = resRoute.next();

            percentage = (Double) tmpRouteRecord.values().get(1).asMap().get("percentage");

            tmpRouteMap = tmpRouteRecord.values().get(0).asMap();

            tmpRoute = new Route(
                    new Airport(iataCode),
                    new Airport(tmpRouteRecord.values().get(2).asString()),
                    new RouteStatistics(
                            (Double) tmpRouteMap.get("cancellationProb"),
                            (Double) tmpRouteMap.get("fifteenDelayProb"),
                            (String) tmpRouteMap.get("mostLikelyCauseDelay"),
                            (String) tmpRouteMap.get("mostLikelyCauseCanc"),
                            (Double) tmpRouteMap.get("meanDelay")
                    )
                    );

            /*if(routeCache.checkCache(tmpRoute) == null)
                routeCache.updateCache(tmpRoute);*/

            mostServedRoute.add(new RankingItem<>(percentage, tmpRoute));
        }

        return mostServedRoute;
    }

    private ArrayList<RankingItem<Route>> fetchMostServedRoute_byAirport(Transaction tx, String iataCode) {
        String mostServedAirlineQuery = "match(origin:Airport)-[p:POSSIBLE_DEPARTURE]->(route:Route)-[:DESTINATION]->(dest: Airport) where origin.IATA_code=\"DFW\" return properties(route), properties(p), dest.IATA_code order by p.percentage desc limit 10";
        Result res = tx.run(mostServedAirlineQuery, parameters("iata_code",iataCode));
        return decompressMostServedRoute(res, iataCode);
    }

    private ArrayList<RankingItem<Airline>> fetchMostServedAirline_byAirport(Transaction tx, String iataCode){
        /*
         * Using properties(airport) it returns every property of the node
         * Using only "return airport" would return only the key
         * */
        String mostServedAirlineQuery = "MATCH (airline:Airline)-[s:SERVES]->(airport:Airport) where airport.IATA_code=$iata_code RETURN properties(airline), properties(s) order by s.percentage desc limit 10";
        Result res = tx.run(mostServedAirlineQuery, parameters("iata_code",iataCode));
        return decompressMostServedAirline(res);
    }

    private ArrayList<RankingItem<Airport>> fetchMostServedAirport_byAirline(Transaction tx, String identifier){
        String mostServedAirportQuery = "MATCH (airline:Airline)-[s:SERVES]->(airport:Airport) where airline.identifier=$identifier RETURN properties(airport), properties(s) order by s.percentage desc limit 10";
        Result res = tx.run(mostServedAirportQuery, parameters("identifier",identifier));
        return decompressMostServedAirport(res);
    }

    private ArrayList<RankingItem<Airline>> fetchMostServedAirline_byRoute(Transaction tx, Airport originAirport, Airport destinationAirport) {
        String mostServedAirlineQuery = "match (route:Route)-[:ORIGIN]->(origin:Airport)\n" +
                "match (route)-[:DESTINATION]->(destination:Airport)\n" +
                "match (route)-[s:SERVED_BY]->(airline:Airline)\n" +
                "where origin.IATA_code = $origin_iata_code and destination.IATA_code = $destination_iata_code\n" +
                "return properties(airline), properties(s)";

        Map<String, Object> params = new HashMap<>();
        params.put("origin_iata_code", originAirport.getIATA_code());
        params.put("destination_iata_code", destinationAirport.getIATA_code());

        Result res = tx.run(mostServedAirlineQuery, params);
        return decompressMostServedAirline(res);
    }

    public ArrayList<Airport> searchAirports_byString(String searchStr){
        String[] searchSubstr = searchStr.split(" ");

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                ArrayList<Airport> tmpAirport = searchAirport_byKeywords(tx, searchSubstr);

                return tmpAirport;
            });
        }
    }

    public ArrayList<Airline> searchAirlines_byString(String searchStr){
        String[] searchSubstr = searchStr.split(" ");

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                ArrayList<Airline> tmpAirline = searchAirline_byKeywords(tx, searchSubstr);

                return tmpAirline;
            });
        }
    }

    /*
    * This function retrieves hint to show to the user based on partial inputs. So for example
    * an input origin = "char" can retrive the "Charlotte, NC" airport. It can takes in input String and
    * Airport object:
    * <ul>
    *   <li>Strings: partial input of the User who has NOT YET selected an airport
    *   <li>Airport Object: when the user click an hint the choice is definitive, no smart-search is exploited.
    *</ul>
    *
    * Note that the choices are correlated between each other. Even if the two input, i.e. <String, String>
    * are two valid IATA_code, no hint is retrieved if there is no route between the two airport.
    *
    * WHEN THE USER HAS SELECTED BOTH AIRPORTS {@link #getRoute_byRoute(Route route)} HAS TO BE INVOKED
    *
    * @params origin Origin airport as String or Airport Object
    * @params destination Destination airport as String or Airport Object
    * */

    public ArrayList<Route> searchRoutes_byObject(Object origin, Object destination){

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                ArrayList<Route> tmpRoute = searchRoute_byKeywords(tx, origin, destination);

                return tmpRoute;
            });
        }
    }

    private ArrayList<Airport> searchAirport_byKeywords(Transaction tx, String[] keywords){
        String searchAirportQuery = "match(a:Airport) with " +
                "a.IATA_code+\" \"+a.name+\" \"+a.city+\" \"+a.state as cond, " +
                "a.IATA_code as IATA_code, " +
                "a.city as city, " +
                "a.state as state, " +
                "a.name as name " +
                "where cond =~ $regexp_pattern " +
                "return IATA_code, state, name, city " +
                "limit 10";
        String airportRegExpr = "(?i).*";
        for(String tmp: keywords){
            airportRegExpr += "(?=.*"+ tmp +".*)";
        }
        airportRegExpr += ".*";
        Result res = tx.run(searchAirportQuery, parameters("regexp_pattern", airportRegExpr));

        ArrayList<Airport> tmpAirport = new ArrayList<>();
        Record rec;
        while(res.hasNext()){
            rec = res.single();
            tmpAirport.add(new Airport(
                    rec.get("IATA_code").asString(),
                    rec.get("name").asString(),
                    rec.get("city").asString(),
                    rec.get("state").asString()
            ));
        }

        return tmpAirport;
    }

    private ArrayList<Airline> searchAirline_byKeywords(Transaction tx, String[] keywords){
        String searchAirlineQuery = "match(a:Airline) with " +
                "a.name+\" \"+a.identifier as pattern, " +
                "a.identifier as identifier, " +
                "a.name as name " +
                "where pattern =~ $regexp_pattern " +
                "return identifier, name " +
                "limit 10";
        String airportRegExpr = "(?i).*";
        for(String tmp: keywords){
            airportRegExpr += "(?=.*"+ tmp +".*)";
        }
        airportRegExpr += ".*";
        Result res = tx.run(searchAirlineQuery, parameters("regexp_pattern", airportRegExpr));

        ArrayList<Airline> tmpAirline = new ArrayList<>();
        Record rec;
        while(res.hasNext()){
            rec = res.single();
            tmpAirline.add(new Airline(
                    rec.get("identifier").asString(),
                    rec.get("name").asString()
                    ));
        }

        return tmpAirline;
    }

    private ArrayList<Route> searchRoute_byKeywords(Transaction tx, Object origin, Object destination){
        String originStr = null;
        Airport originAirport = null;
        Airport destinationAirport = null;
        String destinationStr = null;
        String[] originKeyword = null;
        String[] destinationKeyword = null;
        Map<String, Object> params = new HashMap<>();

        if(destination instanceof String){
            destinationStr = (String) destination;
            if(destinationStr != null && !destinationStr.equals(""))
                destinationKeyword= destinationStr.split(" ");
        }

        String searchRouteQuery = "match (d: Airport)<-[:DESTINATION]-(route: Route)-[:ORIGIN]->(o: Airport) with " +
                "o.IATA_code+\" \"+o.name+\" \"+o.city+\" \"+o.state as originCond, " +
                "d.IATA_code+\" \"+d.name+\" \"+d.city+\" \"+d.state as destinationCond, " +
                "o as originAir, " +
                "d as destinationAir where ";

        if(origin instanceof Airport){
            searchRouteQuery += "originAir.IATA_code = $origin_iata_code ";
            params.put("origin_iata_code", ((Airport) origin).getIATA_code());

            if(origin != null) searchRouteQuery += "and ";
        }
        else if(origin instanceof String){
            originStr = (String) origin;
            if(originStr != null && !originStr.equals(""))
                originKeyword = originStr.split(" ");

            if(originKeyword != null){
                searchRouteQuery += "originCond =~ $origin_regexp ";

                String originRegExpr = "(?i).*";
                for(String tmp: originKeyword){
                    originRegExpr += "(?=.*"+ tmp +".*)";
                }
                originRegExpr += ".*";

                params.put("origin_regexp", originRegExpr);

                if(destination != null) searchRouteQuery += "and ";
            }
        }

        if(destination instanceof Airport){
            searchRouteQuery += "destinationAir.IATA_code = $destination_iata_code ";
            params.put("destination_iata_code", ((Airport)destination).getIATA_code());
        }
        else if(destination instanceof String){
            destinationStr = (String) destination;
            if(destinationStr != null && !destinationStr.equals(""))
                destinationKeyword = destinationStr.split(" ");

            if(destinationKeyword != null){
                searchRouteQuery += "destinationCond =~ $destination_regexp ";

                String destinationRegExpr = "(?i).*";
                for(String tmp: destinationKeyword){
                    destinationRegExpr += "(?=.*" + tmp + ".*)";
                }
                destinationRegExpr += ".*";

                params.put("destination_regexp", destinationRegExpr);
            }
        }

        searchRouteQuery += "return properties(originAir), properties(destinationAir) limit 10";

        System.out.println(searchRouteQuery);

        Result res = tx.run(searchRouteQuery, params);

        ArrayList<Route> tmpRoute = new ArrayList<>();
        Record tmpRouteRecord;
        Map tmpOriginMap;
        while(res.hasNext()){
            tmpRouteRecord = res.next();

            System.out.println(tmpRouteRecord.toString());

            tmpOriginMap = tmpRouteRecord.values().get(0).asMap();

            //TODO
        }

        return tmpRoute;
    }

    private Result matchAirportNode_byIataCode(Transaction tx, String iataCode) {
        /*
        * Using properties(airport) it returns every property of the node
        * Using only "return airport" would return only the key
        * */
        String matchAirportQuery = "MATCH(airport:Airport) WHERE airport.IATA_code = $iata_code return properties(airport)";
        return tx.run(matchAirportQuery, parameters("iata_code",iataCode));

    }

    private Result matchRouteNode_byOriginAirport(Transaction tx, Airport originAirport, Airport destinationAirport){
        String matchAirportQuery = "match (destination: Airport)<-[d:DESTINATION]-(route: Route)-[o:ORIGIN]->(origin: Airport) where origin.IATA_code = $origin_iata_code and destination.IATA_code = $destination_iata_code return properties(route), origin.IATA_code, destination.IATA_code";

        Map<String, Object> params = new HashMap<>();
        params.put("origin_iata_code", originAirport.getIATA_code());
        params.put("destination_iata_code", destinationAirport.getIATA_code());

        return tx.run(matchAirportQuery, params);
    }

    private Result matchAirlineNode_byIdentifier(Transaction tx, String id){
        String matchAirportQuery = "MATCH(airline: Airline) WHERE airline.identifier = $identifier return properties(airline)";
        return tx.run(matchAirportQuery, parameters("identifier", id));
    }


}