package com.example.charliequinn.thescene.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.charliequinn.thescene.Adapters.StatusAdapter;
import com.example.charliequinn.thescene.Fragments.HomeFragment;
import com.example.charliequinn.thescene.Fragments.ProfileFragment;
import com.example.charliequinn.thescene.Helpers.Uploader;
import com.example.charliequinn.thescene.ListItems.StatusItem;
import com.example.charliequinn.thescene.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class OthersProfile extends AppCompatActivity {

    int userIDX;
    int otherIDX;
    private Button addFriendButton;
    private boolean friends;
    private int friendIDs[];
    private Bitmap profilePic;
    private TextView usernameTV;
    private ProgressBar progress;
    private ImageButton profilePicButton;
    private Bitmap photo;
    private StatusAdapter adapter;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        progress = (ProgressBar) findViewById(R.id.progressBarProfile);
        usernameTV = (TextView) findViewById(R.id.usernameDisplay);
        listView = (ListView) findViewById(R.id.my_status);
        progress.setVisibility(View.GONE);

        Intent intent = getIntent();
        userIDX = intent.getIntExtra(HomeFragment.USER_IDX, -1);
        otherIDX = intent.getIntExtra(HomeFragment.OTHER_IDX,-1);
        friendIDs = intent.getIntArrayExtra(HomeFragment.FRIEND_LIST);
        //byte[] barray = intent.getByteArrayExtra(HomeFragment.OTHER_PROFILE_PIC);

        profilePicButton = (ImageButton) findViewById(R.id.profilepic);
        //profilePicButton.setImageBitmap(BitmapFactory.decodeByteArray(barray, 0, barray.length));


        Log.i("otherProfileCreate",friendIDs.length+"");

        new DownloadProfileInfo().execute(otherIDX+"");

        friendButtonStuff();

    }

    public void friendButtonStuff(){
        friends = false;
        for(int i = 0; i < friendIDs.length; i++){
            if(friendIDs[i]==otherIDX){
                friends = true;
            }
        }

        addFriendButton = (Button) findViewById(R.id.add_friend_button);
        if(userIDX == otherIDX){
            addFriendButton.setVisibility(View.GONE);
        }

        if(friends){
            addFriendButton.setText("Unfriend");
        }
    }

    public void addFriend(View view){
        if(!friends){
            new uploadFriendship().execute(userIDX+"", otherIDX+"");
        }else{
            new deleteFriendship().execute(userIDX+"", otherIDX+"");
        }

    }

    public void toastFriendship(){
        if(!friends){
            Toast.makeText(this, "Friendship created!", Toast.LENGTH_LONG).show();
            addFriendButton.setText("Unfriend");
        }else{
            Toast.makeText(this, "Unfriended", Toast.LENGTH_LONG).show();
            addFriendButton.setText("+ Add Friend");
            friends = false;
            for(int i = 0; i < friendIDs.length; i++){
                if(friendIDs[i]==otherIDX){
                    friendIDs[i] = -1;
                }
            }

        }

    }


    private class uploadFriendship extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            addFriendButton.setText("Unfriend");
            Log.i("addFriendship","beginning friendship upload");
            //progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"addFriend","POST"};
            String[] keys = {"useridx","friendidx"};
            return Uploader.getInstance().genericUpload(param,keys,strings);
        }
        @Override
        protected void onPostExecute(String serverReply){
            //progress.setVisibility(View.GONE);
            if(serverReply.equals("error response")){
                Log.i("addFriendship","Server messed up");
            }else{
                toastFriendship();
            }
        }

    }

    private class deleteFriendship extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("deleteFriendship","beginning friendship deletion");
            //progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"deleteFriend","POST"};
            String[] keys = {"useridx","friendidx"};
            return Uploader.getInstance().genericUpload(param,keys,strings);
        }
        @Override
        protected void onPostExecute(String serverReply){
            //progress.setVisibility(View.GONE);
            if(serverReply.equals("error response")){
                Log.i("deleteFriendship","Server messed up");
            }else{
                toastFriendship();
            }
        }

    }

    public void createAdapter(){
        HashMap<Integer, Bitmap> hm = new HashMap<>();
        hm.put(otherIDX, photo);
        adapter = new StatusAdapter(this,new ArrayList<StatusItem>(),hm);

        new downloadStatuses().execute(otherIDX+"", "self");
    }

    public void createAdapterForReal(JSONArray jsonArray) throws JSONException {
        JSONObject job;
        for(int i = jsonArray.length()-1; i >= 0; i--){
            job = jsonArray.getJSONObject(i);
            adapter.add(new StatusItem(job.getString("username"), job.getString("placename"), job.getInt("useridx")));
        }
        createListView(adapter);
    }

    public void createListView(final StatusAdapter adapter) {
        if (listView != null) {
            listView.setAdapter(adapter);
        } else {
            Log.i("ay", "shit");
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
            progress.setVisibility(View.GONE);
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

    private class DownloadProfileInfo extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            Log.i("downloadPicture","beginning picture download");
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"profileInfoGetter","POST"};
            String[] keys = {"useridx"};
            return Uploader.getInstance().genericUpload(param,keys,strings);
        }
        @Override
        protected void onPostExecute(String serverReply){
            if(serverReply.equals("error response")){
                Log.i("getPicture","Something messed up");
            }else{
                try{
                    JSONArray jarray = new JSONArray(serverReply);
                    JSONObject job = jarray.getJSONObject(0);

                    String username = job.getString("username");
                    usernameTV.setText(username);

                    String picString = job.getString("profilepic");
                    if(picString.length() > 6){
                        Log.i("getPicture",picString.length()+ " a "+picString);
                        byte barray[] = Base64.decode(picString,4);
                        photo = BitmapFactory.decodeByteArray(barray,0,barray.length);
                        profilePicButton.setImageBitmap(photo);
                    }else{
                        Log.i("getPicture","no profile picture");
                    }

                    createAdapter();

                }catch (Exception e){
                    Log.e("downloadStatus", "Server error: "+serverReply+", "+e.toString() );
                }
            }
        }

    }

}
