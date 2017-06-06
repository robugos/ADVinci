package com.robugos.advinci.dao;

import android.util.Log;
import android.util.StringBuilderPrinter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by Robson on 25/05/2017.
 */

public class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {}

    public String chamaServico(String reqUrl){
        String resposta = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection conec = (HttpURLConnection) url.openConnection();
            conec.setRequestMethod("GET");
            //le a resposta
            InputStream in = new BufferedInputStream(conec.getInputStream());
            resposta = convertStreamToString(in);
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
        return resposta;
    }

    private String convertStreamToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder stringb = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                stringb.append(line).append("\n");
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return stringb.toString();
    }
}
