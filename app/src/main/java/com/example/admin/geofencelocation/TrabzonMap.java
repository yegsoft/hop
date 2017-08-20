/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.admin.geofencelocation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static android.R.attr.value;

/**
 * This shows how to close the info window when the currently selected marker is re-tapped.
 */
public class TrabzonMap extends AppCompatActivity implements
        OnMarkerClickListener,
        OnMapClickListener,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener {

    String track;
    String zone;
    float lat;
    float lng;
    public LatLng KONUM=new LatLng(lat,lng);

    private GoogleMap mMap = null;


    private Marker mSelectedMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trabzonmaplay);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        new OnMapAndViewReadyListener(mapFragment, this);
    }


    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // Hide the zoom controls.
        mMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        addMarkersToMap();

        // Set listener for marker click event.  See the bottom of this class for its behavior.
        mMap.setOnMarkerClickListener(this);

        // Set listener for map click event.  See the bottom of this class for its behavior.
        mMap.setOnMapClickListener(this);

        // Override the default content description on the view, for accessibility mode.
        // Ideally this string would be localized.
        map.setContentDescription("Demo showing how to close the info window when the currently"
            + " selected marker is re-tapped.");

    }

    public void addMarkersToMap() {
        if(getIntent().getExtras() != null){

            String location = getIntent().getExtras().getString("location");
            zone=location;
        }


        DatabaseReference oku = FirebaseDatabase.getInstance().getReference().child("konumlar");
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long size = dataSnapshot.getChildrenCount();
                int kayit=1;
                Log.d("ZONE"," "+zone);

                for (int i = 1; i <= size; i++) {
                    String holdName = dataSnapshot.child("" + i).child("isim").getValue(String.class);
                    if (holdName.equals(zone))  {
                        kayit=i;
                        break;
                    }
                }
                float holdLat = dataSnapshot.child("" + kayit).child("latitude").getValue(float.class);
                float holdLng = dataSnapshot.child("" + kayit).child("longitude").getValue(float.class);
                lat=holdLat;
                lng=holdLng;
                Log.d("holdlat"," "+holdLat);
                Log.d("holdlng"," "+holdLng);





                KONUM=new LatLng(lat,lng);
                Log.d("LAT"," "+lat);
                Log.d("LNG"," "+lng);
                mMap.addMarker(new MarkerOptions()
                        .position(KONUM)
                        .title("Akçaabat Horonu")
                        .snippet("Akçaabat")
                        .visible(true));




                LatLngBounds bounds = new LatLngBounds.Builder()
                        .include(KONUM)
                        .build();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

            };

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };oku.addListenerForSingleValueEvent(listener);



    }

    @Override
    public void onMapClick(final LatLng point) {
        // Any showing info window closes when the map is clicked.
        // Clear the currently selected marker.
        mSelectedMarker = null;
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        // The user has re-tapped on the marker which was already showing an info window.
        if (marker.equals(mSelectedMarker)) {

            track=marker.getSnippet();


            Intent in = new Intent(this, ActionBarDemoActivity.class);
            String intToSend = zone;
            in.putExtra("location", intToSend);
            startActivity(in);
            mSelectedMarker = null;
            return true;
        }

        mSelectedMarker = marker;

        // Return false to indicate that we have not consumed the event and that we wish
        // for the default behavior to occur.
        return false;
    }
}
