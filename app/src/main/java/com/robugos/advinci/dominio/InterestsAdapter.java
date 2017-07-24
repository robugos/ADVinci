package com.robugos.advinci.dominio;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.robugos.advinci.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Robson on 26/05/2017.
 */

public class InterestsAdapter extends BaseAdapter {
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public InterestsAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
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
            vi = inflater.inflate(R.layout.listview_lista_interesse, null);

        CheckBox interesse = (CheckBox)vi.findViewById(R.id.idInteresse);
        TextView id = (TextView)vi.findViewById(R.id.id);

        id.setVisibility(View.GONE);
        id.setText(data.get(position).get("id"));
        interesse.setText(data.get(position).get("nome"));

        return vi;
    }
}
