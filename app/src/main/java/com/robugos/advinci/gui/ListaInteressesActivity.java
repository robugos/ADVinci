package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListaInteressesActivity extends AppCompatActivity {

    private ListView listview;
    static ArrayList<String> itens = new ArrayList<String>();
    private int count;
    private boolean[] checkselect;
    private ArrayList<String> listaInteresses = new ArrayList<String>();
    private String TAG = ListaInteressesActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private static String url = "http://robugos.com/advinci/db/listainteresses.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interesses);

        itens.clear();
        new GetInteresses().execute();
    }

    public class InteresseAdapter extends BaseAdapter{
        private LayoutInflater intInflater;
        private Context intContext;
        public InteresseAdapter(Context context){
            intContext = context;
        }

        public  int getCount(){
            return count;
        }

        public Object getItem(int position){
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final ViewHolder holder;
            if (convertView == null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(intContext).inflate(R.layout.listview_interesses, null);
                holder.idInteresse = (TextView) convertView.findViewById(R.id.idInteresse);
                holder.checkInteresse = (CheckBox) convertView.findViewById(R.id.checkInteresse);
                convertView.setTag(holder);
                convertView.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        int id = holder.checkInteresse.getId();
                        if (checkselect[id]){
                            holder.checkInteresse.setChecked(false);
                            checkselect[id] = false;
                        }else{
                            holder.checkInteresse.setChecked(true);
                            checkselect[id] = true;
                        }

                    }
                });
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.idInteresse.setId(position);
            holder.checkInteresse.setId(position);
            holder.checkInteresse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    int id = cb.getId();
                    if (checkselect[id]){
                        cb.setChecked(false);
                        checkselect[id] = false;
                    }else{
                        cb.setChecked(true);
                        checkselect[id] = true;
                    }
                }
            });
            String interesse[] = new String[3];
            interesse = itens.get(position).split(";");
            holder.idInteresse.setVisibility(View.GONE);
            holder.idInteresse.setText(interesse[0]);
            holder.checkInteresse.setText(interesse[1]);
            holder.checkInteresse.setChecked(checkselect[position]);
            holder.id = position;
            return convertView;
        }
    }

    class ViewHolder {
        TextView idInteresse;
        CheckBox checkInteresse;
        int id;
    }

    public void click(View v){
        if (v.getId() == R.id.saveInterests) {
            final ArrayList<Integer> posSel = new ArrayList<Integer>();
            posSel.clear();
            listaInteresses.clear();
            boolean noSel = false;
            for (int i =0; i < checkselect.length; i++){
                if (checkselect[i] == true) {
                    noSel = true;
                    Log.e("sel pos thu -->", "" + i);
                    String[] interesse = itens.get(i).split(";");
                    listaInteresses.add(interesse[0]);
                    posSel.add(i);
                }
            }
            if (!noSel){
                Toast.makeText(ListaInteressesActivity.this, "Selecione ao menos um interesse", Toast.LENGTH_SHORT).show();
            }else{
                //Toast.makeText(ListaInteressesActivity.this, "Selecionados: "+posSel.toString(), Toast.LENGTH_LONG).show();
                Toast.makeText(ListaInteressesActivity.this, "Selecionados: "+listaInteresses.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    //Classe AsyncTask para pegar jSON chamando HTTP
    private class GetInteresses extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Mostra dialog de progresso
            pDialog = new ProgressDialog(ListaInteressesActivity.this);
            pDialog.setMessage("Aguarde");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler sh = new HttpHandler();
            // Faz request a URL e pega a resposta
            String jsonStr = sh.chamaServico(url);
            Log.e(TAG, "Respotas da URL: " + jsonStr);
            if (jsonStr != null){
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Pega Array node do JSON
                    JSONArray interesses = jsonObj.getJSONArray("interesses");
                    //loop de todos os eventos
                    for (int i = 0; i < interesses.length(); i++){
                        JSONObject interest = interesses.getJSONObject(i);
                        String id = interest.getString("id");
                        String nome = interest.getString("nome");
                        String categoria = interest.getString("categoria");

                        itens.add(id+";"+nome+";"+categoria);
                    }
                } catch (final JSONException e){
                    Log.e(TAG, "Erro do JSON parsing: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Erro: " + e.getMessage(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Sem conexão");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Sem conexão", Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }
        @Override
        protected  void onPostExecute(Void result){
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            count = itens.size();
            checkselect = new boolean[count];
            listview = (ListView) findViewById(R.id.listaInteresses);
            listview.setAdapter(new InteresseAdapter(ListaInteressesActivity.this));
        }

    }
}