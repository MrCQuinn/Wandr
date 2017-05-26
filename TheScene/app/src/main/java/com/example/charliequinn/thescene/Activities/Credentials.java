package com.example.charliequinn.thescene.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.charliequinn.thescene.Helpers.Uploader;
import com.example.charliequinn.thescene.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class Credentials extends AppCompatActivity {
    private ProgressBar progress;
    private String usernameStr;
    public final static String USER_IDX_KEY = "idx";
    public final static String USER_NAME_KEY = "username";
    public final static String PROFILE_PIC_KEY = "username";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credentials);

        progress = (ProgressBar) findViewById(R.id.progressBar2);
        progress.setVisibility(View.GONE);
    }

    public void signIn(int idx){
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(USER_IDX_KEY,idx);
        intent.putExtra(USER_NAME_KEY,usernameStr);
        startActivity(intent);
    }

    public void wrongPassword(){
        Toast.makeText(this.getApplicationContext(), "Password is incorrect", Toast.LENGTH_LONG).show();
    }

    public void userDoesNotExist(){
        Toast.makeText(this.getApplicationContext(), "Username does not exist. Please create account.", Toast.LENGTH_LONG).show();
    }

    public void logIn(View view){
        EditText usernameET = (EditText) findViewById(R.id.usernameEntry);
        EditText passwordET = (EditText) findViewById(R.id.passwordEntry);

        usernameStr = usernameET.getText().toString();
        String passwordStr = passwordET.getText().toString();

        if(usernameStr.length() == 0) {
            Toast.makeText(this.getApplicationContext(), "Enter a username or email", Toast.LENGTH_LONG).show();
        }else if(passwordStr.length() == 0){
            Toast.makeText(this.getApplicationContext(), "Enter a password", Toast.LENGTH_LONG).show();
        }else{
            new isUser().execute(usernameStr,passwordStr);
        }
    }

    private class isUser extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"login","POST"};
            String[] keys = {"username","password"};
            String[] values = {strings[0],strings[1]};
            return Uploader.getInstance().genericUpload(param,keys,values);
        }
        @Override
        protected void onPostExecute(String serverReply){
            progress.setVisibility(View.GONE);
            if(serverReply.equals("wrong password")){
                Log.i("isUser","Wrong password");
                wrongPassword();
            }else if(serverReply.equals("user does not exist")){
                Log.i("isUser","User does not exist");
                userDoesNotExist();
            }else{
                try{
                    JSONArray jarray = new JSONArray(serverReply);
                    JSONObject job = jarray.getJSONObject(0);
                    int idx = (Integer) job.get("idx");
                    Log.i("isUser","This is a user with idx: "+idx);
                    //TODO save authentication token
                    signIn(idx);
                }catch (Exception e){
                    Log.e("isUser", "Server error: "+serverReply);
                    Log.e("isUser", e.toString() );

                }
            }
        }

    }
}
