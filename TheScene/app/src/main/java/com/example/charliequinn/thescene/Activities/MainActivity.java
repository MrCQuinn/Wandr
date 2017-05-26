package com.example.charliequinn.thescene.Activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.charliequinn.thescene.Fragments.CheckInFragment;
import com.example.charliequinn.thescene.Fragments.HomeFragment;
import com.example.charliequinn.thescene.Fragments.MapFragment;
import com.example.charliequinn.thescene.Fragments.ProfileFragment;
import com.example.charliequinn.thescene.Helpers.Uploader;
import com.example.charliequinn.thescene.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

//    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    HomeFragment homeReference;
    MapFragment mapReference;
    CheckInFragment checkinReference;
    ProfileFragment profileReference;

    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;

    private GoogleApiClient mGoogleApiClient;
    private int[] friendIDs;

    int userIDX;
    String username;
    String profilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        //maybe do this in main activity??
        buildGoogleApiClient();

        Intent intent = getIntent();
        userIDX = intent.getIntExtra(Credentials.USER_IDX_KEY, -1);
        username = intent.getStringExtra(Credentials.USER_NAME_KEY);
        profilePic = intent.getStringExtra(Credentials.PROFILE_PIC_KEY);

        new downloadFriendIDs().execute(userIDX+"");


//        Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId)
//                .setResultCallback(new ResultCallback<PlaceBuffer>() {
//                    @Override
//                    public void onResult(PlaceBuffer places) {
//                        if (places.getStatus().isSuccess()) {
//                            final Place myPlace = places.get(0);
//                            LatLng queriedLocation = myPlace.getLatLng();
//                            Log.v("Latitude is", "" + queriedLocation.latitude);
//                            Log.v("Longitude is", "" + queriedLocation.longitude);
//                        }
//                        places.release();
//                    }
//                });


    }

    public int getUserIDX(){
        return userIDX;
    }

    public int[] getFriendIDs(){
        return friendIDs;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mapReference.locationChanged(location);
        checkinReference.locationChanged(location);
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (com.google.android.gms.location.LocationListener) this);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("Gplay","connected");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, (com.google.android.gms.location.LocationListener) this);
        }


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        homeReference = new HomeFragment();
        adapter.addFragment(homeReference, "Home");
        mapReference = new MapFragment();
        adapter.addFragment(mapReference, "Map");
        checkinReference = new CheckInFragment();
        adapter.addFragment(checkinReference, "Check in");
        profileReference = new ProfileFragment();
        adapter.addFragment(profileReference, "Profile");
        viewPager.setAdapter(adapter);
    }

    public void findPlace(View view) {
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
        } catch (GooglePlayServicesRepairableException e) {
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
        }
    }

    private void setFriendIDs(JSONArray jarray){
        friendIDs = new int[jarray.length()];

        try{
            for(int i = 0; i < jarray.length(); i++){
                friendIDs[i] = jarray.getJSONObject(i).getInt("friendidx");
            }
        }catch (Exception e){
            Log.e("downloadFriends",e.toString());
        }

        homeReference.setFriendIDs(friendIDs);
        profileReference.setFriendIDs(friendIDs);
    }

    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i("TAG", "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i("TAG", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }else if(requestCode == ProfileFragment.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.i("profile pic","inside onactivityresult and result ok");
            profileReference.changeProfilePicture();

        }
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    private class downloadFriendIDs extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("downloadFriendIDs","beginning friend ids download");
            //progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"getFriendIDs","POST"};
            String[] keys = {"useridx"};
            return Uploader.getInstance().genericUpload(param,keys,strings);
        }
        @Override
        protected void onPostExecute(String serverReply){
            //progress.setVisibility(View.GONE);
            if(serverReply.equals("error response")){
                Log.i("downloadFriendIDs","Something messed up");
            }else{
                try{
                    JSONArray jarray = new JSONArray(serverReply);
                    setFriendIDs(jarray);
                }catch (Exception e){
                    Log.e("downloadFriendIDs", "Server error: "+serverReply+", "+e.toString() );
                }
            }
        }

    }


}
