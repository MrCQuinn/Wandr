package com.example.charliequinn.thescene.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.GoogleMap;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link CheckInFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link CheckInFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CheckInFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View myFragmentView;
    private ListView listView;
    private Location l;
    private int userIDX;

    Button[] buttons = new Button[3];

    public CheckInFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CheckInFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CheckInFragment newInstance(String param1, String param2) {
        CheckInFragment fragment = new CheckInFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_check_in, container, false);
        listView = (ListView) myFragmentView.findViewById(R.id.places);

        MainActivity ma = (MainActivity) getActivity();
        userIDX = ma.getUserIDX();

        nearbyPlacesGetter("establishment");

        //Get buttons
        buttons[0] = (Button) myFragmentView.findViewById(R.id.near_me);
        buttons[1] = (Button) myFragmentView.findViewById(R.id.recent);
        buttons[2] = (Button) myFragmentView.findViewById(R.id.favorites);

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
                nearbyPlacesGetter("establishment");
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

        return myFragmentView;
    }

    private void switchFocus(int whichButton){
        for(int i = 0; i < 3; i++){
            buttons[i].setBackgroundColor(Color.TRANSPARENT);
            buttons[i].setTextColor(Color.BLACK);
        }

//        final int sdk = android.os.Build.VERSION.SDK_INT;
//        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
//            buttons[whichButton].setBackgroundDrawable( ContextCompat.getDrawable(getActivity(), R.drawable.round_button));
//        } else {
            buttons[whichButton].setBackground( ContextCompat.getDrawable(getActivity(), R.drawable.round_button));
       // }

        buttons[whichButton].setTextColor(Color.WHITE);
    }

    public void locationChanged(Location location){
        l = location;
    }

    public void nearbyPlacesGetter(String placeType){
        Log.i("NearbyPlacesGetter", "getting");
        final PlaceAdapter adapter = new PlaceAdapter(getActivity(),new ArrayList<PlaceItem>());
        if(l!=null){
            String url = getUrl(l.getLatitude(),l.getLongitude(),placeType);
            Object[] DataTransfer = new Object[7];
            DataTransfer[0] = "checkin";
            DataTransfer[1] = url;
            DataTransfer[2] = listView;
            DataTransfer[3] = adapter;
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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

}
