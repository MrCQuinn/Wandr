package com.example.charliequinn.thescene.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.charliequinn.thescene.Activities.MainActivity;
import com.example.charliequinn.thescene.Activities.OthersProfile;
import com.example.charliequinn.thescene.Helpers.Uploader;
import com.example.charliequinn.thescene.R;
import com.example.charliequinn.thescene.Adapters.StatusAdapter;
import com.example.charliequinn.thescene.ListItems.StatusItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;


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
    public final static String OTHER_PROFILE_PIC = "com.example.charliequinn.thescene.OTHER_PROFILE_PIC";


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View myFragmentView;
    private ListView listView;
    private StatusAdapter adapter;
    private int userIDX;
    private int[] friendIDs;
    private ArrayList<StatusItem> statuses;
    private HashMap<Integer, Bitmap> photos;
    private ProgressBar progress;

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

        statuses = new ArrayList<>();
        photos = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        myFragmentView = inflater.inflate(R.layout.fragment_home, container, false);

        //initalize progressbar and hide it until loading begins
        progress = (ProgressBar) myFragmentView.findViewById(R.id.homeProgress);
        progress.setVisibility(View.GONE);

        //initialize status list
        listView = (ListView) myFragmentView.findViewById(R.id.statuses);

        //get user info
        MainActivity ma = (MainActivity) getActivity();
        userIDX = ma.getUserIDX();

        //download statuses
        new downloadStatuses().execute(userIDX+"", "all");

        //set up buttons
        buttons[0] = (Button) myFragmentView.findViewById(R.id.home_unfiltered);
        buttons[1] = (Button) myFragmentView.findViewById(R.id.friends);
        buttons[2] = (Button) myFragmentView.findViewById(R.id.promoters);

        buttons[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFocus(0);
                new downloadStatuses().execute(userIDX+"", "all");
            }
        });

        buttons[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFocus(1);
                new downloadStatuses().execute(userIDX+"", "friends");
            }
        });


        buttons[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchFocus(2);
                new downloadStatuses().execute(userIDX+"", "promoters");
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

    public void goToProfile(int otherIDX, Bitmap bm){
        Intent intent = new Intent(this.getActivity(), OthersProfile.class);
        intent.putExtra(USER_IDX, userIDX);
        intent.putExtra(OTHER_IDX,otherIDX);
        intent.putExtra(FRIEND_LIST,friendIDs);
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
//        byte[] byteArray = stream.toByteArray();
//        intent.putExtra(OTHER_PROFILE_PIC,byteArray);
        startActivity(intent);
    }

    public void createStatusList(JSONArray jsonArray) throws JSONException {
        JSONObject job;
        statuses.clear();
        for(int i = jsonArray.length()-1; i >= 0; i--){
            job = jsonArray.getJSONObject(i);
            statuses.add(new StatusItem(job.getString("username"), job.getString("placename"), job.getInt("useridx")));
        }
    }

    public void createAdapter() throws JSONException {
        final StatusAdapter adapter = new StatusAdapter(getActivity(),new ArrayList<StatusItem>(),photos);
        //this.adapter = adapter;
        for(int i = statuses.size()-1; i >= 0; i--){
            adapter.add(statuses.get(i));
        }
        createListView(adapter);
    }

    public void createListView(final StatusAdapter adapter){
        if(listView!=null){
            listView.setAdapter(adapter);
            progress.setVisibility(View.GONE);
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
                goToProfile(pick.userIDX,pick.profliePic);
            }
        });


    }

    public void setFriendIDs(int[] friendIDs){
        this.friendIDs = friendIDs;
    }

    private class downloadStatuses extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("downloadStatuses","beginning status download");
            progress.setVisibility(View.VISIBLE);
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
                    if(jarray.length()>0){
                        new downloadPhotos().execute(jarray);
                    }else{
                        statuses.clear();
                        createAdapter();
                    }

                }catch (Exception e){
                    Log.e("downloadStatus", "Server error: "+serverReply+", "+e.toString() );
                }
            }
        }

    }

    private class downloadPhotos extends AsyncTask<JSONArray, Void, String> {
        @Override
        protected void onPreExecute() {
            Log.i("getPhotos", "beginning photo download");
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(JSONArray... jarrays) {
            JSONArray jarray = jarrays[0];
            String query = "select idx, profilepic from users where idx = ";
            JSONObject job;
            try {
                createStatusList(jarray);
                for (int i = jarray.length() - 1; i > 0; i--) {
                    job = jarray.getJSONObject(i);
                    query = query + job.getInt("useridx") + " or idx = ";
                }
                job = jarray.getJSONObject(0);
                query = query + job.getInt("useridx");
                Log.i("getPhotos", "query = " + query);
            } catch (Exception e) {
                Log.e("getPhotos", e.toString());
            }

            String[] param = {"getPhotos", "POST"};
            String[] keys = {"query"};
            String[] values = {query};
            return Uploader.getInstance().genericUpload(param, keys, values);
        }

        @Override
        protected void onPostExecute(String serverReply) {
            //progress.setVisibility(View.GONE);
            if (serverReply.equals("error response")) {
                Log.i("getPhotos", "Something messed up");
            } else {
                try {
                    JSONArray jarray = new JSONArray(serverReply);
                    JSONObject job;
                    for (int i = 0; i < jarray.length(); i++) {
                        job = jarray.getJSONObject(i);
                        String picString = job.getString("profilepic");
                        if (picString.length() > 6) {
                            byte barray[] = Base64.decode(picString, 4);
                            photos.put(job.getInt("idx"), BitmapFactory.decodeByteArray(barray, 0, barray.length));
                        }
                    }
                    photos.put(-1, BitmapFactory.decodeResource(getResources(), R.drawable.missing_profile_pic));
                    createAdapter();
                } catch (Exception e) {
                    Log.e("getPhotos", "Server reply: " + serverReply);
                    Log.e("getPhotos", "Server error: " + e.toString());
                }
            }
        }

    }

}
