package com.robugos.advinci.dominio;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import com.robugos.advinci.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robson on 26/05/2017.
 */

public class ListViewAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;
    private boolean TAG;

    public ListViewAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ListViewAdapter(Activity a, ArrayList<HashMap<String, String>> d, boolean x) {
        activity = a;
        data = d;
        TAG = x;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if(convertView == null)
            if(TAG == true){
                vi = inflater.inflate(R.layout.listview_lista_evento_rec, null);
            }else {
                vi = inflater.inflate(R.layout.listview_lista_evento, null);
            }

        TextView nomeEvento = (TextView)vi.findViewById(R.id.nomeEvento);
        TextView dataEvento = (TextView)vi.findViewById(R.id.dataEvento);
        TextView localEvento = (TextView)vi.findViewById(R.id.localEvento);
        RatingBar ratingEvento= (RatingBar) vi.findViewById(R.id.ratingEvento);

        nomeEvento.setText(data.get(position).get("nome"));
        localEvento.setText(data.get(position).get("local"));
        dataEvento.setText(data.get(position).get("data"));
        ratingEvento.setRating(Float.parseFloat(data.get(position).get("nota")));
        ratingEvento.setContentDescription(data.get(position).get("nome")+" avaliado em: "+data.get(position).get("nota")+" estrelas");

        return vi;
    }
}
