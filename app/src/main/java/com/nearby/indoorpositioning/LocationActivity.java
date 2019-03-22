package com.nearby.indoorpositioning;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.Gson;

import java.util.List;

public class LocationActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Gson gson = new Gson();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        List<String> locationList = Utils.getCachedMessages(this);

        if(!locationList.isEmpty()) {
            pinLocation(locationList);
        }
        else
        {
            final LayeredImageView v = new LayeredImageView(this);
            v.setImageResource(R.drawable.floor_plan);
            setContentView(v);
        }
    }

    private void pinLocation(List<String> locationList) {
        final LayeredImageView v = new LayeredImageView(this);
        Resources res = v.getResources();

        v.setImageResource(R.drawable.floor_plan);
        Matrix m;
        m = new Matrix();
        BeaconData data = getMeanLocation(locationList);
        m.preTranslate(data.getX(), data.getY()); // pixels to offset

        final LayeredImageView.Layer layer1 = v.addLayer(ResourcesCompat.getDrawable(getResources(), R.drawable.location_pin, null), m);
        setContentView(v);
    }

    private BeaconData getMeanLocation(List<String> locationList) {
        BeaconData meanBeaconData = new BeaconData();
        int x=0,y = 0;
        for (String location: locationList){
            BeaconData data = mapToBeaconData(location);
            x+=data.getX();
            y+=data.getY();
        }
        meanBeaconData.setX(x/locationList.size());
        meanBeaconData.setY(y/locationList.size());
        return meanBeaconData;
    }

    private BeaconData mapToBeaconData(String location){
        return gson.fromJson(location,BeaconData.class);
    }

    @Override
    protected void onPause() {
        getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getSharedPreferences(getApplicationContext().getPackageName(), Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        pinLocation(Utils.getCachedMessages(this));
    }

}
