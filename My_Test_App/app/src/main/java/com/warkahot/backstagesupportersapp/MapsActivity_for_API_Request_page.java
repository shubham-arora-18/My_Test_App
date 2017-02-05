package com.warkahot.backstagesupportersapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MapsActivity_for_API_Request_page extends Fragment implements OnMapReadyCallback,GoogleApiClient.OnConnectionFailedListener,GoogleApiClient.ConnectionCallbacks, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap;

    private GoogleApiClient mGoogleApiClient;
    private final int Acess_loc_req_code = 69;
    final int REQUEST_CHECK_SETTINGS = 1000;
    LocationRequest mLocationRequest;
    Marker temp ;
    ArrayList<Hospital_Item> hospitals_list;
    AlertDialog specific_person_details_ad;
    ProgressDialog pd;
    Map_Page map_page_object;
    LatLng my_last_position;



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        System.out.println("Attaching now");
    }

    @Override
    public void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
        System.out.println("Dettaching now");
    }

    @Override
    public View onCreateView(LayoutInflater li,ViewGroup vg,Bundle b) {

        View v = li.inflate(R.layout.activity_maps_match_parent,vg,false);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        return v;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        connect_to_google_services();
        mGoogleApiClient.connect();

    }









    public void connect_to_google_services()
    {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    //You may also specify an optional implementation for the ConnectionCallbacks interface(listner) if your app needs to know when the automatically managed connection is established or suspended.
    // For example if your app makes calls to write data to Google APIs, these should be invoked only after the onConnected() method has been called.
    @Override
    public void onConnected(Bundle bundle) {

        System.out.println("you are now connected");
        checking_for_location_permission();


    }
    //You may also specify an optional implementation for the ConnectionCallbacks interface if your app needs to know when the automatically managed connection is established or suspended.
    // For example if your app makes calls to write data to Google APIs, these should be invoked only after the onConnected() method has been called.
    @Override
    public void onConnectionSuspended(int i) {
        System.out.println("your connection is suspended");
    }
















    public void checking_for_location_permission()
    {
        //if the location permission is already given to the app
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            click_listeners();
            set_up_location_request();
           // Toast.makeText(getContext(),"Permission granted",Toast.LENGTH_LONG).show();
        }
        //if the location permission is not given ask for it in run time
        else {

          //  Toast.makeText(getContext(),"Please grant the Location Permission",Toast.LENGTH_LONG).show();
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Acess_loc_req_code);// from this the control goes to the main activity's(i.e. here send_detail/ carry detail) onRequestPermissionsResult
        }
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Acess_loc_req_code) {
            if (permissions.length == 1 &&
                    permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                set_up_location_request();
                click_listeners();

            } else {
                checking_for_location_permission();
            }
        }
    }

























    public void click_listeners(){
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                System.out.println("mMap button clicked");
                moving_to_last_known_position();

                return true;
            }
        });

        map_page_object.search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                moving_to_last_known_position();
                String radius = map_page_object.radius_spinner.getText().toString();

                if(radius.equals(""))
                    Toast.makeText(getContext(),"Please select a Radius value ",Toast.LENGTH_SHORT).show();
                else
                {

                    if(new Network_available().isNetworkAvailable(getContext()))
                    {

                        if(my_last_position!=null)
                        {
                            pd.show();
                            API_Requesting_hospitals api_requesting_hospitals = new API_Requesting_hospitals(my_last_position,Integer.parseInt(radius)*1000,MapsActivity_for_API_Request_page.this,pd);

                        }
                        else
                        {
                            Toast.makeText(getContext(),"Your position not Fetched please try again in some time",Toast.LENGTH_LONG).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(),"Please check your network connectivity ",Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });

    }

    public void moving_to_last_known_position()
    {
        //setCustomInfowindow();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if(mLastLocation !=null)
            {

                LatLng loc = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                my_last_position = loc;
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc,15));//16 level zoom
                if(temp!=null)
                    temp.remove();// this code removes the previous added markers so that markers are not added one over another
              //  temp = mMap.addMarker(new MarkerOptions().position(loc).title("My Position"));

            }
            else
                System.out.println("mlast location is null");
        }

    }
























    //initializes location request which will further call the method turn_gps_on() if needed
    public void set_up_location_request()
    {
        // setting up the location setting that we want for our app
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);//interval in which or app will recieve location updates
        mLocationRequest.setFastestInterval(5000);//fastest interval in which or app will recieve location updates
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//how accurate do you want your position to be
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        builder.setAlwaysShow(true);

        // checking if the setting that we need match with the current system settings
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());
        // its result will be handled by following code
        //To determine whether the location settings are appropriate for the location request, check the status code from the LocationSettingsResult object.
        // A status code of RESOLUTION_REQUIRED indicates that the settings must be changed.
        // To prompt the user for permission to modify the location settings, call startResolutionForResult(Activity, int).
        // This method brings up a dialog asking for the user's permission to modify location settings.

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        System.out.println("Locations setting have already been granted");
                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult()in the main activity (i.e. send/ carry pages in our case)
                            status.startResolutionForResult(
                                    getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });

    }














    public void startLocationUpdates() {

        System.out.println("inside start LocationUpdates");
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            System.out.println("inside LocationUpdates if satisfied");
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    mGoogleApiClient, mLocationRequest, this);//this means calls the onLocationChanged method of LocationListener
        }
    }

    //whenever our position changes this happens interface(Listener) = Location Listener
    @Override
    public void onLocationChanged(Location location) {
        System.out.println("inside onLocationChanged");
       // update_location_automatically(location);
        //we can also get the date here as
        // String  mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());

    }












    public void add_different_markers(ArrayList<Hospital_Item> hospitals_list) throws JSONException {

        this.hospitals_list = hospitals_list;

        for(int i= 0;i< hospitals_list.size();i++)
        {

            Double lat,longi;
            int index = i;


            Hospital_Item temp_Hospital_item = hospitals_list.get(i);


            lat = temp_Hospital_item.position.latitude;
            longi = temp_Hospital_item.position.longitude;


            Marker m = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(lat, longi))
                    .title("" + index)
                    .icon(BitmapDescriptorFactory.fromBitmap(setup_marker())));


            Log.d("MapsActivity_request","Lat = "+lat+" Long = "+longi);
        }


        marker_on_click_listener();


    }



    public Bitmap setup_marker()
    {
        Bitmap marker_bitmap = null;
        try {



        View v1 = LayoutInflater.from(getContext()).inflate(R.layout.map_marker, null);

        LinearLayout ll_marker;
        ll_marker = (LinearLayout)v1.findViewById(R.id.ll_marker);



        ll_marker.setDrawingCacheEnabled(true);
        ll_marker.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        ll_marker.layout(0, 0, ll_marker.getMeasuredWidth(),ll_marker.getMeasuredHeight());
        ll_marker.buildDrawingCache();

        // System.out.println("Linear layout height = "+ll_marker.getLayoutParams().height);
        marker_bitmap = Bitmap.createBitmap(ll_marker.getDrawingCache());
        ll_marker.setDrawingCacheEnabled(false);


        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return marker_bitmap;

    }



    public void marker_on_click_listener()
    {
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                final  Hospital_Item temp_Hospital_item = hospitals_list.get(Integer.parseInt(marker.getTitle()));

                    View v1 = LayoutInflater.from(getContext()).inflate(R.layout.drop_down_menu_page_send_request, null);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setView(v1);
                    final AlertDialog ad = builder.create();
                    //   ad.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    ad.setCancelable(true);
                    ad.show();
                    specific_person_details_ad = ad;


                    TextView hosp_name;
                    ImageView hosp_img;

                    hosp_name = (TextView)v1.findViewById(R.id.hosp_name);
                    hosp_img = (ImageView)v1.findViewById(R.id.hosp_image);

                    Picasso.with(getContext()).load(temp_Hospital_item.icon).into(hosp_img);
                    hosp_name.setText(temp_Hospital_item.name);



                return true;
            }
        });
    }








 


    //if an error occurs that cannot be resolved, you will receive a call to onConnectionFailed(). interface(listener) = GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        System.out.println("unresolvable error in connection");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}
