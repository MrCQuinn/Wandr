package com.example.charliequinn.thescene.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.example.charliequinn.thescene.Activities.OthersProfile;
import com.example.charliequinn.thescene.Helpers.Downloader;
import com.example.charliequinn.thescene.Helpers.Uploader;
import com.example.charliequinn.thescene.R;
import com.example.charliequinn.thescene.Adapters.StatusAdapter;
import com.example.charliequinn.thescene.ListItems.StatusItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public final static String USER_IDX = "com.example.charliequinn.thescene.USERIDX";
    public final static String OTHER_IDX = "com.example.charliequinn.thescene.OTHERIDX";
    public final static String FRIEND_LIST = "com.example.charliequinn.thescene.FRIENDLIST";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View myFragmentView;
    private ListView listView;
    private StatusAdapter adapter;
    private int userIDX;
    private int[] friendIDs;

    Button[] buttons = new Button[3];

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        myFragmentView = inflater.inflate(R.layout.fragment_home, container, false);
        listView = (ListView) myFragmentView.findViewById(R.id.statuses);


        //get user info
        MainActivity ma = (MainActivity) getActivity();
        userIDX = ma.getUserIDX();

        new downloadFriendIDs().execute(userIDX+"");

        createAdapter("all");

        buttons[0] = (Button) myFragmentView.findViewById(R.id.home_unfiltered);
        buttons[1] = (Button) myFragmentView.findViewById(R.id.friends);
        buttons[2] = (Button) myFragmentView.findViewById(R.id.promoters);

        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFocus(0);
                createAdapter("all");
            }
        });

        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFocus(1);
                createAdapter("friends");
            }
        });


        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFocus(2);
                createAdapter("promoters");
            }
        });


        return myFragmentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void switchFocus(int whichButton){
        for(int i = 0; i < 3; i++){
            buttons[i].setBackgroundColor(Color.TRANSPARENT);
            buttons[i].setTextColor(Color.BLACK);
        }

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            buttons[whichButton].setBackgroundDrawable( ContextCompat.getDrawable(getActivity(), R.drawable.round_button));
        } else {
            buttons[whichButton].setBackground( ContextCompat.getDrawable(getActivity(), R.drawable.round_button));
        }

        buttons[whichButton].setTextColor(Color.WHITE);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Activity a;

        if (context instanceof Activity){
            a=(Activity) context;
        }
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

    public void goToProfile(int otherIDX){
        Intent intent = new Intent(this.getActivity(), OthersProfile.class);
        intent.putExtra(USER_IDX, userIDX);
        intent.putExtra(OTHER_IDX,otherIDX);
        intent.putExtra(FRIEND_LIST,friendIDs);
        startActivity(intent);
    }

    public void createAdapter(String filter){
        final StatusAdapter adapter = new StatusAdapter(getActivity(),new ArrayList<StatusItem>());
        this.adapter = adapter;
        new downloadStatuses().execute(userIDX+"", filter);
    }

    public void createAdapterForReal(JSONArray jsonArray) throws JSONException {
        JSONObject job;
        for(int i = jsonArray.length()-1; i >= 0; i--){
            job = jsonArray.getJSONObject(i);
            adapter.add(new StatusItem(job.getString("username"), job.getString("placename"), job.getInt("useridx")));
        }
        createListView(adapter);
    }

    public void createListView(final StatusAdapter adapter){
        if(listView!=null){
            listView.setAdapter(adapter);
        }else{
            Log.i("ay","shit");
        }

        Log.i("HomeFragment", "listview created");

        //listens to user's pick
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("HomeFragment", "status clicked");
                StatusItem pick = adapter.getItem(position);
                goToProfile(pick.userIDX);
            }
        });


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
    }

    private class downloadStatuses extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("downloadStatuses","beginning status download");
            //progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"getStatuses","POST"};
            String[] keys = {"useridx","filter"};
            return Uploader.getInstance().genericUpload(param,keys,strings);
        }
        @Override
        protected void onPostExecute(String serverReply){
            //progress.setVisibility(View.GONE);
            if(serverReply.equals("error response")){
                Log.i("downloadStatus","Something messed up");
            }else{
                try{
                    JSONArray jarray = new JSONArray(serverReply);
                    createAdapterForReal(jarray);
                }catch (Exception e){
                    Log.e("downloadStatus", "Server error: "+serverReply+", "+e.toString() );
                }
            }
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
