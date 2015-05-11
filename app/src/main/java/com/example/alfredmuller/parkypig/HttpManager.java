package com.example.alfredmuller.parkypig;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * <h1>httpManager is a helper class which extends AsyncTask class. This class communicates with the SFPark API.</h1>
 * <p>The class contains various methods used to send location coordinates of the current marker and retrieve the nearby parking
 * spots. An instance of this class is invoked by calling the httpManager.execute() function when the "Find Nearby Parking"
 * button is clicked on the UI screen.</p><b>Authors are defined as anyone who wrote code for the class.</b>
 *
 * @author Syed Khureshi
 * @version Milestone3
 * @see android.os.AsyncTask
 */
public class HttpManager extends AsyncTask<String, Integer, String>{

    public static String response;

    /**
     * Default constructor for HttpManager, not used.
     */
    public HttpManager(){}

    /**
     *doInBackground creates and opens HttpURLConnection to send the current marker coordinates and receive
     * the nearby parking information.
     *
     * @param uri used to send to the SFPark API
     * @return response String of SFPark API call.
     */
    @Override
    protected String doInBackground(String... uri) {

        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            URL url = new URL(uri[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = bufferedReader.readLine()) != null){
                stringBuilder.append(line + "\n");
            }
            response = stringBuilder.toString();
        }catch (Exception e){
            e.printStackTrace();
            return response;
        }finally {
            if(bufferedReader != null){
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }

    /**
     * onPostExecute method is executed on completion of doInBackground method.
     * This method receives the response of SFPark API call as a parameter, result and
     * passes it to the MapsActivity.dropMarkers method.
     * @param result, response from SFPark API.
     */
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        MapsActivity.dropMarkers(result);
    }
}