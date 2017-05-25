package com.example.charliequinn.thescene.Helpers;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by charliequinn on 4/26/17.
 */

public class CheckIn extends AsyncTask<String, String, String> {

    String serverReply;

    @Override
    protected String doInBackground(String... strings) {
        try {
            String[] param = {"checkin","POST"};
            String[] keys = {"useridx","placeidx","placename","placeaddress","placerating"};
            return Uploader.getInstance().genericUpload(param,keys,strings);



        } catch (Exception e) {
            Log.d("CheckInTask", e.toString());
        }
        return serverReply;
    }

    @Override
    protected void onPostExecute(String result) {

    }


}