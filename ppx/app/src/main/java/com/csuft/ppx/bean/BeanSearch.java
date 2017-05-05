package com.csuft.ppx.bean;

/**
 * Created by ling on 2017/5/2.
 */

public class BeanSearch {
    private String names;
    private String place;
    public BeanSearch(String names, String place){
        this.names = names;
        this.place = place;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }
    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }
}
