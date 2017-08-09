package com.robugos.advinci.gui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.robugos.advinci.R;
import com.robugos.advinci.dao.SessionManager;

public class SplashScreen extends AppCompatActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        /*TextView logoText = (TextView) findViewById(R.id.logo_advinci);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Dosis-Regular.ttf");
        logoText.setTypeface(typeface);*/

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                mostrarLogin();
            }
        }, 2000);

        session = new SessionManager(this);
    }

    private void mostrarLogin(){
        if (session.isLoggedIn()) {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
