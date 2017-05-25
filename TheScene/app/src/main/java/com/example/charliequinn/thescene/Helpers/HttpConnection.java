package com.example.charliequinn.thescene.Helpers;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by charliequinn on 1/31/17.
 */

public class HttpConnection {

    public String Upload(String urlString, String type, String JSONstr) throws IOException {
        String data = "";
        try {
            URL url = new URL(urlString);
            Log.i("upload", "URL is " + url.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestMethod(type);
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStreamWriter os = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            os.write(JSONstr);
            os.close();

            conn.connect();
//            int code = conn.getResponseCode();
//            Log.i("upload", "code: " + code);
//            if(code!=200){
//                return "server error";
//            }

            BufferedReader br = new BufferedReader((new InputStreamReader(
                    conn.getInputStream())));
            StringBuffer b = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                b.append(line);
            }
            data = b.toString(); //response from server to string
        }catch (Exception e){
            Log.i("upload", "error here "+ e.toString()+ " "+ e.getCause() );
        }
        return data;
    }



}
