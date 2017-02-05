package com.warkahot.backstagesupportersapp;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by warkahot on 05-Feb-17.
 */
public class API_Requesting_hospitals  {

    LatLng position;
    int radius ;
    String type = "hospital";
    String key = "AIzaSyAiYnplk7hHIuL9kr9kRY9ORTALOfb6TqI";
    ArrayList<Hospital_Item> hospitals_locations_global = new ArrayList<>();
    MapsActivity_for_API_Request_page mapsActivity_for_api_request_page ;
    int success = 0;
    ProgressDialog pd ;

    public API_Requesting_hospitals(LatLng position, int radius, MapsActivity_for_API_Request_page mapsActivity_for_api_request_page,ProgressDialog pd) {
        this.position = position;
        this.radius = radius;
        this.mapsActivity_for_api_request_page = mapsActivity_for_api_request_page;
        this.pd= pd;

        try {
            new API_Requesting_hospitals_Async_task().execute();
        } catch (Exception e) {
            System.out.println(" API_Requesting_hospitals_Async_task gadbad ho gayi");
            e.printStackTrace();
        }
    }

    public JSONObject sending_HTTP_request_and_returning_JSON_response() throws IOException
    {
        String data = "";
        InputStream iStream = null;
        JSONObject jo = null;
        HttpURLConnection urlConnection = null;
        try {
            String my_position = position.latitude+","+position.longitude;
            String strUrl = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+my_position+"&radius="+radius+"&type="+type+"&key="+key;
            System.out.println("Final Url = "+strUrl);
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("Order", "Response Recieved = " + data.toString());
            br.close();
            jo = new JSONObject(data);

            handling_JSON_response_and_retrieving_hospitals_locations(jo);
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return jo;
    }

    public void handling_JSON_response_and_retrieving_hospitals_locations(JSONObject jo)
    {
        System.out.println("Response = "+jo.toString());
        ArrayList<Hospital_Item> hospitals_locations = new ArrayList<>();
        try{
            JSONArray results = jo.getJSONArray("results");
            System.out.println("Results size  = "+results.length());

            for(int i = 0;i<results.length();i++)
            {
                JSONObject temp_jo = results.getJSONObject(i);
                String lat_temp = temp_jo.getJSONObject("geometry").getJSONObject("location").getString("lat");
                String lng_temp = temp_jo.getJSONObject("geometry").getJSONObject("location").getString("lng");
                String name_temp = temp_jo.getString("name");
                String icon_temp = temp_jo.getString("icon");
                System.out.println("Hospital name = "+name_temp+" Hospital icon = "+icon_temp+"Hospital Location = "+lat_temp+","+lng_temp);
                hospitals_locations.add(new Hospital_Item(name_temp, icon_temp, new LatLng(Double.parseDouble(lat_temp), Double.parseDouble(lng_temp))));
            }

        }
        catch (Exception e)
        {
            Log.d("Order","Exception in handling_JSON_response_and_retrieving_hospitals_locations in API_Requesting_hospitals = "+e.toString());
        }
        hospitals_locations_global =  hospitals_locations;
        if(hospitals_locations.size()!=0)
        success = 1;
    }

    public class API_Requesting_hospitals_Async_task extends AsyncTask<String ,String,String> {
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {


            try {
                mapsActivity_for_api_request_page.add_different_markers(hospitals_locations_global);
            } catch (JSONException e) {
                Log.d("Order", "Exception in onPostExecute in API_Requesting_hospitals = " + e.toString());
            }


            if(pd.isShowing())
                Log.d("Order", "Yeah pd is showing ...here also...");


            pd.dismiss();
            if (success==0)
                Toast.makeText(pd.getContext(),"Some error occurred please try after some time",Toast.LENGTH_LONG).show();


        }

        @Override
        protected String doInBackground(String... params) {
            try {
                sending_HTTP_request_and_returning_JSON_response();
            } catch (IOException e) {
                Log.d("Order", "Exception in doInBackground in API_Requesting_hospitals = " + e.toString());
            }
            return null;
        }
    }
}
