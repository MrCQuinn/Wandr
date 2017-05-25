package com.example.charliequinn.thescene.ListItems;

/**
 * Created by charliequinn on 1/18/17.
 */

public class MapsItem {
    public String name;
    public String address;
    public String distance;
    public String rating;
    public String placeID;
    public String openTil;
    int usersHere;
    public MapsItem(String placeID, String name, String distance, String address, String rating){
        this.name = name;
        this.distance = distance;
        this.address = address;
        this.rating = rating + "/5";
        this.placeID = placeID;
    }
}
