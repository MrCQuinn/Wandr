package com.example.charliequinn.thescene.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.example.charliequinn.thescene.Adapters.MapsAdapter;
import com.example.charliequinn.thescene.Adapters.PlaceAdapter;
import com.example.charliequinn.thescene.Adapters.StatusAdapter;
import com.example.charliequinn.thescene.ListItems.MapsItem;
import com.example.charliequinn.thescene.ListItems.PlaceItem;
import com.example.charliequinn.thescene.ListItems.StatusItem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;

/**
 * Created by charliequinn on 4/8/17.
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {
    String googlePlacesData;
    GoogleMap googleMap;
    String url;
    ListView lv;
    MapsAdapter ma;
    PlaceAdapter pa;
    String type;
    Context context;
    int userIDX;

    @Override
    protected  String doInBackground(Object... params){
        try{
            type = (String) params[0];
            if(type.equals("map")) {
                googleMap = (GoogleMap) params[4];
                ma = (MapsAdapter) params[3];
            }else{
                pa = (PlaceAdapter) params[3];
            }
            url = (String) params[1];
            lv = (ListView) params[2];
            context = (Context) params[5];
            userIDX = (int) params[6];

            Downloader downloadUrl = new Downloader();
            googlePlacesData = downloadUrl.readUrl(url);
        }catch(Exception e){
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);

        if(type.equals("map")){
            ShowNearbyPlaces(nearbyPlacesList);
            createListView(createMapsAdapter(nearbyPlacesList));
        }else{
            createListView(createPlaceAdapter(nearbyPlacesList));
        }



        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            Log.d("onPostExecute","Entered into showing locations");
            MarkerOptions markerOptions = new MarkerOptions();
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
            Log.i("PostNearbys",googlePlace.toString());
            double lat = Double.parseDouble(googlePlace.get("lat"));
            double lng = Double.parseDouble(googlePlace.get("lng"));
            String placeName = googlePlace.get("place_name");
            String vicinity = googlePlace.get("vicinity");
            LatLng latLng = new LatLng(lat, lng);
            markerOptions.position(latLng);
            markerOptions.title(placeName + " : " + vicinity);
            googleMap.addMarker(markerOptions);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            //move map camera
//            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
//            googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

            //populate stuff with data collected
        }
    }

    private MapsAdapter createMapsAdapter(List<HashMap<String, String>> nearbyPlacesList){
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            ma.add(new MapsItem(googlePlace.get("place_id"), googlePlace.get("place_name"), "time away" , googlePlace.get("vicinity"),googlePlace.get("rating")));
        }

        //listens to user's pick
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MapsItem pick = ma.getItem(position);
                checkIn(pick.placeID, pick.name, pick.address,pick.rating);
            }
        });


        return ma;
    }

    private PlaceAdapter createPlaceAdapter(List<HashMap<String, String>> nearbyPlacesList){
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            HashMap<String, String> googlePlace = nearbyPlacesList.get(i);

            pa.add(new PlaceItem(googlePlace.get("place_id"),
                    googlePlace.get("place_name"),googlePlace.get("vicinity"), "distance", 0,googlePlace.get("rating")));
        }

        //listens to user's pick
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PlaceItem pick = pa.getItem(position);
                checkIn(pick.placeID, pick.placeName, pick.address, pick.rating);
            }
        });

        return pa;
    }

    public void createListView(final ListAdapter adapter) {
        if (lv != null) {
            lv.setAdapter(adapter);
        } else {
            Log.i("ay", "shit");
        }
    }

    public void checkIn(final String placeID, final String placeName, final String placeAddress, final String placerating){
        //will also need user ID
        new AlertDialog.Builder(context)
                .setTitle("Check In")
                .setMessage("Would you like to check in to "+placeName)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //Needs User IDX but check In at this point
                        new CheckIn().execute(userIDX+"",placeID,placeName,placeAddress,placerating);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // dissmiss window
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}
