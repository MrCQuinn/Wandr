package com.example.charliequinn.thescene.ListItems;

/**
 * Created by charliequinn on 1/18/17.
 */

public class PlaceItem {
    public String placeName;
    public String distance;
    public String placeID;
    public int imageIcon;
    public String rating;
    public String address;
    public PlaceItem(String placeID, String name, String address, String distance, int imageIcon, String rating){
        this.placeName = name;
        this.distance = distance;
        this.imageIcon = imageIcon;
        this.placeID = placeID;
        this.address = address;
        this.rating = rating;
    }
}
