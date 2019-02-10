package com.nearby.indoorpositioning;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.List;

public class LocationActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        final LayeredImageView v = new LayeredImageView(this);
        Resources res = v.getResources();

        v.setImageResource(R.drawable.floor_plan);

        List<String> messages = Utils.getCachedMessages(this);
        Matrix m;

        m = new Matrix();
        m.preTranslate(81, 146); // pixels to offset

        final LayeredImageView.Layer layer1 = v.addLayer(ResourcesCompat.getDrawable(getResources(), R.drawable.location_pin, null), m);
        setContentView(v);
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

    }

}
