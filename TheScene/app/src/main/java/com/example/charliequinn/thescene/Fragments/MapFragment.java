package com.example.charliequinn.thescene.Fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.charliequinn.thescene.Activities.MainActivity;
import com.example.charliequinn.thescene.Adapters.MapsAdapter;
import com.example.charliequinn.thescene.Adapters.PlaceAdapter;
import com.example.charliequinn.thescene.Adapters.StatusAdapter;
import com.example.charliequinn.thescene.Helpers.GetNearbyPlacesData;
import com.example.charliequinn.thescene.ListItems.MapsItem;
import com.example.charliequinn.thescene.ListItems.PlaceItem;
import com.example.charliequinn.thescene.ListItems.StatusItem;
import com.example.charliequinn.thescene.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;


import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MapFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment
{
    Location l;
    Marker mCurrLocationMarker;

    private GoogleMap googleMap;
    Activity thisActivity;
    ListView nearPlacesList;
    private boolean loaded;

    private int userIDX;

    Button[] buttons = new Button[4];

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myFragmentView = inflater.inflate(R.layout.fragment_map, container, false);

        loaded = false;

        MainActivity ma = (MainActivity) getActivity();
        userIDX = ma.getUserIDX();

        //Get buttons
        buttons[0] = (Button) myFragmentView.findViewById(R.id.map_unfiltered);
        buttons[1] = (Button) myFragmentView.findViewById(R.id.restaurant_button);
        buttons[2] = (Button) myFragmentView.findViewById(R.id.bar_button);
        buttons[3] = (Button) myFragmentView.findViewById(R.id.shop_button);
        //Get nearby list
        nearPlacesList = (ListView) myFragmentView.findViewById(R.id.near_places);

        //set up code for buttons
        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearbyPlacesGetter("establishment");
                switchFocus(0);
            }
        });

        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearbyPlacesGetter("restaurant");
                switchFocus(1);
            }
        });


        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearbyPlacesGetter("bar");
                switchFocus(2);
            }
        });

        buttons[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nearbyPlacesGetter("shopping_mall");
                switchFocus(3);
            }
        });

        //make this reference for permission checking
        thisActivity = this.getActivity();

        // new thread to load map
        setUpMap();

        return myFragmentView;
    }

    public void locationChanged(Location newLocation){
        l = newLocation;

        //Place current location marker
        LatLng latLng = new LatLng(l.getLatitude(), l.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = googleMap.addMarker(markerOptions);

        //move map camera
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        if(!loaded){
            nearbyPlacesGetter("establishment");




            loaded = true;


        }


    }

    private void switchFocus(int whichButton){
        for(int i = 0; i < 4; i++){
            buttons[i].setBackgroundColor(Color.TRANSPARENT);
            buttons[i].setTextColor(Color.BLACK);
        }

//        final int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            buttons[whichButton].setBackgroundDrawable( ContextCompat.getDrawable(getActivity(), R.drawable.round_button));
//        } else {
            buttons[whichButton].setBackground( ContextCompat.getDrawable(getActivity(), R.drawable.round_button));
        //}

        buttons[whichButton].setTextColor(Color.WHITE);
    }

    private void setUpMap() {
        SupportMapFragment mSupportMapFragment;

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapview);
        if (mSupportMapFragment == null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            mSupportMapFragment = SupportMapFragment.newInstance();
            fragmentTransaction.replace(R.id.mapview, mSupportMapFragment).commit();
        }

        if (mSupportMapFragment != null) {
            mSupportMapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap gMap) {
                    if (gMap != null) {
                        googleMap = gMap;

                        googleMap.getUiSettings().setAllGesturesEnabled(true);


                        if (ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(thisActivity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // Asking user if explanation is needed
                            if (ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                                // Show an explanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.

                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(thisActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);


                            } else {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(thisActivity,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                            return;
                        }
                        googleMap.setMyLocationEnabled(true);

                        CameraPosition cameraPosition = new CameraPosition.Builder().target(new LatLng(44,22)).zoom(15.0f).build();
                        CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                        googleMap.moveCamera(cameraUpdate);

                    }

                }
            });
        }
    }


    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public void nearbyPlacesGetter(String placeType){
        Log.i("NearbyPlacesGetter", "getting");
        final MapsAdapter adapter = new MapsAdapter(getActivity(),new ArrayList<MapsItem>());
        if(l!=null){
            String url = getUrl(l.getLatitude(),l.getLongitude(),placeType);
            Object[] DataTransfer = new Object[7];
            DataTransfer[0] = "map";
            DataTransfer[1] = url;
            DataTransfer[2] = nearPlacesList;
            DataTransfer[3] = adapter;
            DataTransfer[4] = googleMap;
            DataTransfer[5] = getActivity();
            DataTransfer[6] = userIDX;
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);

        }else{
            Toast.makeText(getActivity(), "Unable to get your location", Toast.LENGTH_LONG).show();
        }

    }

    private String PROXIMITY_RADIUS = "5000";

    private String getUrl(double latitude, double longitude, String nearbyPlace) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=AIzaSyAZq9nEBpZHm1m5oFMqGHfmrQR3_QIStWQ");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


}


//
//    public void createListView(final MapsAdapter adapter){
//        //ListView listView = (ListView) getActivity().findViewById(R.id.statuses);
//        if(listView!=null){
//            listView.setAdapter(adapter);
//        }else{
//            Log.i("ay","shit");
//        }
//
//
////        //listens to user's pick
////        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
////            @Override
////            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                WalkListItem pick = adapter.getItem(position);
////                DB.recordEvent(pick._pathIdx,-1,login,new Date(),PATH_OPTIONS_SELECT_EVENT);
////                pickWalk(pick._pathIdx);
////            }
////        });
//    }
//}
