package com.example.charliequinn.thescene.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.charliequinn.thescene.Fragments.HomeFragment;
import com.example.charliequinn.thescene.Helpers.Uploader;
import com.example.charliequinn.thescene.R;

import org.json.JSONArray;

public class OthersProfile extends AppCompatActivity {

    int userIDX;
    int otherIDX;
    private Button addFriendButton;
    private boolean friends;
    private int friendIDs[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        Intent intent = getIntent();
        userIDX = intent.getIntExtra(HomeFragment.USER_IDX, -1);
        otherIDX = intent.getIntExtra(HomeFragment.OTHER_IDX,-1);
        friendIDs = intent.getIntArrayExtra(HomeFragment.FRIEND_LIST);

        Log.i("otherProfileCreate",friendIDs.length+"");

        friends = false;
        for(int i = 0; i < friendIDs.length; i++){
//            Log.i("friendIDs", friendIDs[i]+"=="+otherIDX);
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

}
