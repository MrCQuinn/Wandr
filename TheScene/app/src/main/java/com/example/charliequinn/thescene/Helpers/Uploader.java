package com.example.charliequinn.thescene.Helpers;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

/**
 * Created by charliequinn on 1/31/17.
 */

public class Uploader {

    HttpConnection httpConnection;
    private static Uploader mInstance = null;

    private Uploader(){
        httpConnection = new HttpConnection();
    }

    public static Uploader getInstance(){
        if(mInstance == null)
        {
            mInstance = new Uploader();
        }
        return mInstance;
    }

    public String genericUpload(String[] param, String[] keys, String[] values){
        String jStringSend; //JSON string
        JSONObject jobject = new JSONObject();
        String reply = "";
        try{

            for(int i = 0; i < keys.length; i++){
                jobject.put(keys[i],values[i]);
            }
            jStringSend = jobject.toString();
            Log.i("upload", "JSON: " + jStringSend);

            reply = httpConnection.Upload("https://thesceneapp.herokuapp.com/"+param[0],param[1], jStringSend);

            Log.i("upload", "Initial Reply: " + reply);
        }catch(Exception e){
            Log.i("Upload", "exception thrown: "+e);
        }
        return reply;
    }

    public String addUser(String[] param, String[] keys, String[] values){
        String jStringSend; //JSON string
        JSONObject jobject = new JSONObject();
        String reply = "";
        try{
            for(int i = 0; i < keys.length; i++){
                jobject.put(keys[i],values[i]);
            }
            jStringSend = jobject.toString();
            Log.i("upload", "JSON: " + jStringSend);

            reply = httpConnection.Upload("https://thesceneapp.herokuapp.com/"+param[0],param[1], jStringSend);

            Log.i("upload", "Initial Reply: " + reply);
        }catch(Exception e){
            Log.i("Upload", "exception thrown: "+e);
        }
        return reply;
    }

//    public void upload(String... paramaters){
//        new uploadTask().execute(paramaters);
//    }
//
//    private class uploadTask extends AsyncTask<String, Void, int[]> {
//
//        @Override
//        protected  int[] doInBackground(String... strings){
//
//            String jStringSend; //JSON string
//            int[] initialReplyArray = new int[10]; //Parsed JSON turned into an int array
//            try{
//                Log.i("upload", "Initial JSON: " + strings[0]);
//                String reply = httpConnection.Upload("https://pacific-fjord-59927.herokuapp.com/thisTest",strings[0]);
////                String reply = httpConnection.Upload("http://localhost:5000/thisTest",strings[0]);
//
//
////                if (reply == "server error"){
////                    initialReplyArray[0] = 2;
////                    return initialReplyArray;
////                }
//                Log.i("upload", "Initial Reply: " + reply);
//
//                //initialReplyArray = new JSONParser().parseInitialResponse(new JSONObject(reply));
//
//            }catch(Exception e){
//                Log.i("Upload", "exception thrown: "+e);
//            }
//
//            return initialReplyArray;
//        }
//        @Override
//        protected void onPostExecute(int[] replyArray){
//            if(replyArray[0]==1){// if status == 1, MAC address is valid and uploading can begin.
//                Log.i("upload","Beginning individual tables' upload processes...");
//            }else{
//                Log.i("upload","server error status: "+replyArray[0]);
//            }
//        }
//
//    }

}
