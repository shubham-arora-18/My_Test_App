package com.warkahot.backstagesupportersapp;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by warkahot on 05-Feb-17.
 */
public class Hospital_Item {

    String name,icon;
    LatLng position;

    public Hospital_Item(String name,String icon, LatLng position) {
        this.name = name;
        this.position = position;
        this.icon = icon;
    }
}
