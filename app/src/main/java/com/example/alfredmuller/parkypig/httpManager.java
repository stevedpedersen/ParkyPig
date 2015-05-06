package com.example.alfredmuller.parkypig;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by omer on 4/14/2015.
 */
public class httpManager extends AsyncTask<String, Integer, String>{

    public static String response = "crazy";
    public static String responseU = "def val";

    @Override
    protected String doInBackground(String... uri) {

        BufferedReader reader = null;
        try {
            URL url = new URL(uri[0]);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();

            String line = null;

            while ((line = reader.readLine()) != null){
                sb.append(line + "\n");
            }

            response = sb.toString();

            return null;


        }catch (Exception e){
            e.printStackTrace();
            response = "Null 1";
            return null;
        }finally {
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();

                    response = "this is null";
                    return null;
                }
            }
        }

    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        //MainActivity mainActivity = new MainActivity();
        MapsActivity.dropMarkers(response);
    }
}
