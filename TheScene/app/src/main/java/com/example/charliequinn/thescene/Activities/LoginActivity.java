package com.example.charliequinn.thescene.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.charliequinn.thescene.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void logIn(View view){
        Intent intent = new Intent(this, Credentials.class);
        startActivity(intent);
    }

    public void createAccount(View view){
        Intent intent = new Intent(this, CreateAccount.class);
        startActivity(intent);
    }




}
