package com.task2_3.client;

import java.util.ArrayList;

public class  Cache<T> extends ArrayList<T> {

    /**
     * Check if there is already an Airport/Airline/Route with a specific identifier into the cache, in that case also update
     * @param tmp Object of type Airport/Airline/Route containing ONLY the identifier
     * @return    Complete  Object if hit, null if miss
     **/
    public T checkCache(T tmp){
        int ind;

        //check for a hit on the cache, and update
        if((ind = this.indexOf(tmp)) != -1) {
            tmp = this.remove(ind);
            this.add(tmp);
            return tmp;
        }

        return null;
    }


    public void updateCache(T tmp){
        if(
                (tmp instanceof Airport && this.size() >= 10) ||
                (tmp instanceof Airline && this.size() >= 10)||
                (tmp instanceof Route && this.size() >= 30))
        {
            this.remove(0);
        }
        this.add(tmp);
    }
}
