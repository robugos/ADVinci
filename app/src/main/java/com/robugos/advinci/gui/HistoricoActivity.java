package com.robugos.advinci.gui;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.robugos.advinci.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class HistoricoActivity extends AppCompatActivity {

    TextView latTextView;
    TextView lonTextView;
    TextView locTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historico);

        latTextView = (TextView) findViewById(R.id.latitude);
        lonTextView = (TextView) findViewById(R.id.longitude);
        locTextView = (TextView) findViewById(R.id.local);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        locationManager.getBestProvider(criteria, true);

        Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, true));

        Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
        latTextView.setText(String.valueOf(location.getLatitude()));
        lonTextView.setText(String.valueOf(location.getLongitude()));
        Log.d("Tag", "1");
        List<Address> addresses;

        try{
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses.size()>0){
                System.out.println("endereco "+addresses.get(0).getLocality());
                locTextView.setText(addresses.get(0).getLocality());
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}