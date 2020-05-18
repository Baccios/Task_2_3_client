package com.task2_3.client;

import org.neo4j.driver.*;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

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
                System.out.println(route.getDestination().getIATA_code()+route.getDestination().getIATA_code());
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
        String mostServedAirlineQuery = "match(origin:Airport)-[p:POSSIBLE_DEPARTURE]->(route:Route)-[:DESTINATION]->(dest: Airport) where origin.IATA_code=$iata_code return properties(route), properties(p), dest.IATA_code order by p.percentage desc limit 6";
        Result res = tx.run(mostServedAirlineQuery, parameters("iata_code",iataCode));
        return decompressMostServedRoute(res, iataCode);
    }

    private ArrayList<RankingItem<Airline>> fetchMostServedAirline_byAirport(Transaction tx, String iataCode){
        /*
         * Using properties(airport) it returns every property of the node
         * Using only "return airport" would return only the key
         * */
        String mostServedAirlineQuery = "MATCH (airline:Airline)-[s:SERVES]->(airport:Airport) where airport.IATA_code=$iata_code RETURN properties(airline), properties(s) order by s.percentage desc limit 6";
        Result res = tx.run(mostServedAirlineQuery, parameters("iata_code",iataCode));
        return decompressMostServedAirline(res);
    }

    private ArrayList<RankingItem<Airport>> fetchMostServedAirport_byAirline(Transaction tx, String identifier){
        String mostServedAirportQuery = "MATCH (airline:Airline)-[s:SERVES]->(airport:Airport) where airline.identifier=$identifier RETURN properties(airport), properties(s) order by s.percentage desc limit 6";
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

    /**
    * Retrieve hint to show to the user based on partial inputs. So for example
    * an input origin = "char" can retrieve the "Charlotte, NC" airport. It can takes in input String and
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
    * @param origin Origin airport as String or Airport Object
    * @param destination Destination airport as String or Airport Object
    * */

    public ArrayList<Route> searchRoutes_byObject(Object origin, Object destination){

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                ArrayList<Route> tmpRoute = searchRoute_byKeywords(tx, origin, destination);

                return tmpRoute;
            });
        }
    }

    /**
     * Given an origin and destination airport, this method returns an array of routes having the given destination airport and
     * an origin airport different from the one specified but placed in the same U.S. state.
     * Useful to suggest a user alternatives for not existent routes.
     * @param origin The origin airport
     * @param destination The destination airport
     * @return array of routes ordered by mean delay
     */
    public ArrayList<Route> searchSimilarRoutes_byOriginAndDest(Airport origin, Airport destination){

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                return searchSimilarRoutes_byOriginAndDest_query(tx, origin, destination);
            });
        }
    }

    /**
     * Given an airport, return the number of reachable airports with at most two route hops
     * @param airport the origin airport
     * @return the number of reachable airports. -1 in case of error
     */
    public int getTwoHopsDestinationsCount(Airport airport) {
        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                return getTwoHopsDestinationsCount_query(tx, airport);
            });
        }
    }

    /**
     * Get the number of times the airline is the best (in terms of QoS) for a route, and the total number of routes the airline serves
     * @param airline the specified airline
     * @return an array of two integers: the first is the number of first places, the second is total number of routes.
     *         Returns null in case of error
     */
    public int[] getFirstPlacesCount_ByAirline(Airline airline) {
        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                return getFirstPlacesCount_ByAirline_query(tx, airline);
            });
        }
    }

    private ArrayList<Route> searchSimilarRoutes_byOriginAndDest_query(Transaction tx, Airport origin, Airport dest) {
        String query =
                "MATCH (origin:Airport)<-[:ORIGIN]-(r:Route)-[:DESTINATION]->(dest:Airport {IATA_code: $iata_dest})\n" +
                "WHERE origin.state = $state AND origin.IATA_code <> $iata_origin\n" +
                "RETURN r,origin\n" +
                "ORDER BY r.meanDelay";
        HashMap<String, Object> params = new HashMap<>();
        params.put("iata_origin", origin.getIATA_code());
        params.put("iata_dest", dest.getIATA_code());
        params.put("state", origin.getState());
        Result res = tx.run(query, params);
        Record rec;
        ArrayList<Route> similarRoutes = new ArrayList<>();
        while (res.hasNext()) {
            rec = res.next();
            Node origin_node = rec.get("origin").asNode();
            Node route_node = rec.get("r").asNode();

            Airport currentOrigin = new Airport(
                    origin_node.get("IATA_code").asString(),
                    origin_node.get("name").asString(),
                    origin_node.get("city").asString(),
                    origin_node.get("state").asString(),
                    new AirportStatistics(
                            origin_node.get("cancellationProb").asDouble(),
                            origin_node.get("fifteenDelayProb").asDouble(),
                            origin_node.get("qosIndicator").asDouble(),
                            origin_node.get("mostLikelyCauseDelay").asString(),
                            origin_node.get("mostLikelyCauseCanc").asString()
                    )
            );


            Route currentRoute = new Route(
                    currentOrigin,
                    dest,
                    new RouteStatistics(
                            route_node.get("cancellationProb").asDouble(),
                            route_node.get("fifteenDelayProb").asDouble(),
                            route_node.get("mostLikelyCauseDelay").asString(),
                            route_node.get("mostLikelyCauseCanc").asString(),
                            route_node.get("meanDelay").asDouble()
                    )
            );

            similarRoutes.add(currentRoute);
        }
        return similarRoutes;
    }

    private int getTwoHopsDestinationsCount_query(Transaction tx, Airport airport) {
        String query =
                "MATCH (airport:Airport {IATA_code: $iata})\n" +
                "MATCH (airport)<-[:ORIGIN]-(:Route)-[:DESTINATION]->(:Airport)<-[:ORIGIN]-(:Route)-[:DESTINATION]->(target:Airport)\n" +
                "WHERE target.IATA_code <> airport.IATA_code\n" +
                "RETURN count(distinct target) AS ReachableAirports";
        HashMap<String, Object> params = new HashMap<>();
        params.put("iata", airport.getIATA_code());
        Result res = tx.run(query, params);
        if(!res.hasNext()) {
            System.err.println("An error occurred in two hops count query");
            return -1;
        }
        Record rec = res.single();
        return rec.get(0).asInt();
    }

    private int[] getFirstPlacesCount_ByAirline_query(Transaction tx, Airline airline) {
        String query =
                "MATCH (airline:Airline {identifier: $id})\n" +
                "MATCH (airline)<-[:SERVED_BY]-(r:Route)\n" +
                "WITH airline, count(r) AS totalRoutesServed\n" +
                "\n" +
                "MATCH (airline)<-[sb:SERVED_BY]-(r:Route)\n" +
                "OPTIONAL MATCH (r)-[otherSb:SERVED_BY]->(other:Airline)\n" +
                "WHERE otherSb.qosIndicator > sb.qosIndicator\n" +
                "WITH airline, totalRoutesServed ,sb AS candidateRoute, size(collect(otherSb)) AS betterAirlines\n" +
                "\n" +
                "MATCH (airline)<-[candidateRoute]-()\n" +
                "WHERE betterAirlines = 0\n" +
                "RETURN airline, count(betterAirlines) AS bestCarrierCount, totalRoutesServed";
        HashMap<String, Object> params = new HashMap<>();
        params.put("id", airline.getIdentifier());
        Result res = tx.run(query, params);
        if(!res.hasNext()) {
            System.err.println("An error occurred in first places count query");
            return null;
        }
        Record rec = res.single();
        return new int[] {rec.get("bestCarrierCount").asInt(), rec.get("totalRoutesServed").asInt()};
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
            rec = res.next();
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
                "limit 6";
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

        searchRouteQuery += "return properties(originAir), properties(destinationAir) limit 6";

        System.out.println(searchRouteQuery);

        Result res = tx.run(searchRouteQuery, params);

        ArrayList<Route> tmpRoute = new ArrayList<>();
        Record tmpRouteRecord;
        Map tmpOriginMap;
        Map tmpDestinationMap;
        while(res.hasNext()){
            tmpRouteRecord = res.next();

            System.out.println(tmpRouteRecord.toString());

            tmpOriginMap = tmpRouteRecord.values().get(0).asMap();
            tmpDestinationMap = tmpRouteRecord.values().get(1).asMap();

            tmpRoute.add(new Route(
                    new Airport(tmpOriginMap.get("IATA_code").toString()),
                    new Airport(tmpDestinationMap.get("IATA_code").toString())
            ));
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

    public ArrayList<RankingItem<Airport>> getOverallBestAirport(){
        ArrayList<RankingItem<Airport>> tmp = new ArrayList<>();

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                String query = "match(airport:Airport) " +
                        "return airport.IATA_code, airport.city, airport.name, airport.state,airport.qosIndicator " +
                        "order by airport.qosIndicator desc limit 6";
                Result res = tx.run(query);
                /*
                 * asMap will permit to access the values by using "fieldName"
                 * */
                Map rec = null;
                Airport tmpAirport = null;
                while(res.hasNext()){
                    rec = res.next().asMap();
                    tmp.add(new RankingItem<Airport>((Double)rec.get("airport.qosIndicator"),new Airport(
                            rec.get("airport.IATA_code").toString(),
                            rec.get("airport.name").toString(),
                            rec.get("airport.city").toString(),
                            rec.get("airport.state").toString()
                    )));
                }

                return tmp;
            });
        }
    }

    public ArrayList<RankingItem<Airline>> getOverallBestAirline(){

        try(Session session = driver.session()){
            return session.readTransaction(tx -> {
                String query = "match(airline:Airline) " +
                        "return airline.identifier, airline.name, airline.qosIndicator " +
                        "order by airline.qosIndicator desc limit 6";
                Result res = tx.run(query);
                /*
                 * asMap will permit to access the values by using "fieldName"
                 * */
                Map rec = null;
                Airport tmpAirline = null;
                ArrayList<RankingItem<Airline>> tmp = new ArrayList<>();
                while(res.hasNext()){
                    rec = res.next().asMap();
                    System.out.println(rec);
                    tmp.add(new RankingItem<>((double)rec.get("airline.qosIndicator"),new Airline(
                            rec.get("airline.identifier").toString(),
                            rec.get("airline.name").toString()
                    )));
                }

                return tmp;
            });
        }
    }
}