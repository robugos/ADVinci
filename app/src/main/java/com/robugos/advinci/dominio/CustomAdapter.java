package com.robugos.advinci.dominio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.robugos.advinci.R;

import java.util.ArrayList;

/**
 * Created by Robson on 09/08/2017.
 */
public class CustomAdapter extends ArrayAdapter<DataModel> {

    private ArrayList<DataModel> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView nomeInteresse;
        CheckBox checkInteresse;
    }

    public CustomAdapter(ArrayList<DataModel> data, Context context) {
        super(context, R.layout.listview_interesses, data);
        this.dataSet = data;
        this.mContext = context;

    }
    @Override
    public int getCount() {
        return dataSet.size();
    }

    @Override
    public DataModel getItem(int position) {
        return dataSet.get(position);
    }


    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        CustomAdapter.ViewHolder viewHolder;
        final View result;

        if (convertView == null) {
            viewHolder = new CustomAdapter.ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_interesses, parent, false);
            viewHolder.nomeInteresse = (TextView) convertView.findViewById(R.id.nomeInteresse);
            viewHolder.checkInteresse = (CheckBox) convertView.findViewById(R.id.checkInteresse);

            result=convertView;
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (CustomAdapter.ViewHolder) convertView.getTag();
            result=convertView;
        }

        DataModel item = getItem(position);


        viewHolder.nomeInteresse.setText(item.nome);
        viewHolder.checkInteresse.setChecked(item.checked);


        return result;
    }
}
