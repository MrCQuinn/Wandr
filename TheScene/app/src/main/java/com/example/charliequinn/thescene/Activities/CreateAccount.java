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

public class CreateAccount extends AppCompatActivity {

    private ProgressBar loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        loading = (ProgressBar) findViewById(R.id.progressBar);
        loading.setVisibility(View.GONE);
    }

    public void usernameExists(){
        Toast.makeText(this, "Username already exists", Toast.LENGTH_LONG).show();
    }

    public void emailInUse(){
        Toast.makeText(this.getApplicationContext(), "Email is already in use", Toast.LENGTH_LONG).show();
    }

    public void signIn(int idx) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("idx",idx);
        startActivity(intent);
    }

    public void actionCreateAccount(View view){
        EditText usernameTV = (EditText) findViewById(R.id.createUsernameEntry);
        String username = usernameTV.getText().toString();
        EditText emailTV = (EditText) findViewById(R.id.createEmailEntry);
        String email = emailTV.getText().toString();
        EditText passwordTV = (EditText) findViewById(R.id.createPasswordEntry);
        String password = passwordTV.getText().toString();
        EditText passwordConfirmTV = (EditText) findViewById(R.id.createConfirmPasswordEntry);
        String passwordConfirm = passwordConfirmTV.getText().toString();

        if(password.equals(passwordConfirm)){
            new createUser().execute(username,email,password);
        }else if(username.length() == 0 || password.length() == 0){
            Log.i("create account","missing field");
            Toast.makeText(this.getApplicationContext(), "Missing a field", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this.getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
            Log.i("create account","passwords don't match");
        }
    }

//    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
//            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
//
//    public static boolean validate(String emailStr) {
//        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(emailStr);
//        return matcher.find();
//    }

    private class createUser extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute(){
            loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected  String doInBackground(String... strings){
            String[] param = {"newUser","POST"};
            String[] keys = {"username","email","password"};
            return Uploader.getInstance().addUser(param,keys,strings);
        }
        @Override
        protected void onPostExecute(String serverReply){
            loading.setVisibility(View.GONE);
            if(serverReply.equals("user exists")){
                Log.i("createUser","User exists");
                usernameExists();
            }else if(serverReply.equals("email is in use")){
                Log.i("createUser","Email exists");
                emailInUse();
            }else{
                try{
                    JSONArray jarray = new JSONArray(serverReply);
                    JSONObject job = jarray.getJSONObject(0);
                    int idx = (Integer) job.get("idx");
                    Log.i("isUser","User created with idx: "+idx);
                    //TODO save authentication token
                    signIn(idx);
                }catch (Exception e){
                    Log.e("isUser", "Server error: "+serverReply+", "+e.toString() );
                }
            }
        }

    }
}
