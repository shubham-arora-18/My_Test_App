package com.warkahot.backstagesupportersapp;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.BetterSpinner;

/**
 * Created by warkahot on 05-Feb-17.
 */
public class Map_Page extends AppCompatActivity {

    RelativeLayout map_here;
    BetterSpinner radius_spinner;
    Button search_button;
    MapsActivity_for_API_Request_page map_fragment;
    private final int Acess_loc_req_code = 69;
    final int REQUEST_CHECK_SETTINGS = 1000;
    ProgressDialog pd ;
    NotificationCompat.Builder noti;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.hospitals_near_me);


        show_notification();
        initialize_ui_variables();
        on_click_events();
        add_map_fragment();

    }

    public void initialize_ui_variables() {
        map_here = (RelativeLayout) findViewById(R.id.map_here);
        radius_spinner = (BetterSpinner) findViewById(R.id.radius);
        search_button = (Button) findViewById(R.id.search_but);

        String[] radius = new String[]{
                "5", "6", "7", "8", "9", "10"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(Map_Page.this,
                android.R.layout.simple_dropdown_item_1line, radius);
        radius_spinner.setAdapter(adapter);
        pd = new ProgressDialog(Map_Page.this);
        pd.setMessage("Please wait...");
    }

    public void add_map_fragment() {
        MapsActivity_for_API_Request_page frag2 = new MapsActivity_for_API_Request_page();
        frag2.map_page_object = Map_Page.this;
        frag2.pd = pd;
        map_fragment = frag2;
        android.support.v4.app.FragmentTransaction frag_transac2 = getSupportFragmentManager().beginTransaction();
        frag_transac2.replace(R.id.map_here, frag2);
        frag_transac2.commit();


    }

    public void on_click_events()
    {
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Please Wait", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent returned_intent) {
        super.onActivityResult(requestCode, resultCode, returned_intent);
        Log.d("Navi Drawer result", "Got in 1");


        switch (requestCode) {
            // to check the request made to turn on the gps in set_up_location_request() method in (for the MapsActivity)
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        System.out.println("Gps is now turned on");
                        map_fragment.startLocationUpdates();

                        break;
                    case Activity.RESULT_CANCELED:
                        System.out.println("your request got cancelled");
                        map_fragment.set_up_location_request();//keep asking if imp or do whatever
                        break;
                }
                break;
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == Acess_loc_req_code) {
            if (permissions.length == 1 &&
                    permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                map_fragment.set_up_location_request();
                map_fragment.click_listeners();

            } else {
                map_fragment.checking_for_location_permission();
            }
        }
    }

    public void show_notification()
    {
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.clinic_icon);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        noti = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle("Backstage Supporters")
                .setContentText("App is running")
                .setSmallIcon(R.drawable.clinic_icon)
                .setLargeIcon(largeIcon)
                .setOngoing(true) // Again, THIS is the important line
                .setSound(defaultSoundUri)
                ;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, noti.build());


    }



    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("App was stopped");
        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("App was destroyed");
    }
}
