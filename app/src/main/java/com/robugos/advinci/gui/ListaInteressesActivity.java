package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;
import com.robugos.advinci.dominio.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ListaInteressesActivity extends AppCompatActivity {

    private ListView listview;
    static ArrayList<String> itens = new ArrayList<String>();
    private int count;
    private boolean[] checkselect;
    private ArrayList<String> listaInteresses = new ArrayList<String>();
    private String TAG = ListaInteressesActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private static String url = "http://robugos.com/advinci/db/listainteresses.php?uid=";
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interesses);

        Intent intent = getIntent();
        idUser = intent.getExtras().getString("uid");
        itens.clear();
        final Button saveButton = (Button) findViewById(R.id.saveInterests);

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
                saveInterests(listaInteresses.toString());
                //Toast.makeText(ListaInteressesActivity.this, "Selecionados: "+listaInteresses.toString(), Toast.LENGTH_LONG).show();
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
            String jsonStr = sh.chamaServico(url+idUser);
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
                    String valores = jsonObj.getString("userinteresses");
                    valores = valores.substring(1, valores.length()-1).replaceAll(" ","");
                    String[] parts = valores.split(",");
                    //Collections.addAll(userinteresses, parts);

                } catch (final JSONException e){
                    Log.e(TAG, "Erro do JSON parsing: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Sem conexão");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {Toast.makeText(getApplicationContext(),"Não foi possível conectar com o servidor", Toast.LENGTH_LONG).show();
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
            showAviso();
        }

    }

    private void showAviso() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Selecionar interesses")
                .setMessage("Selecione um ou mais tópicos que sejam de seu interesse para uma melhor recomendação de eventos")
                .setIcon(0)
                .setPositiveButton("Continuar", null)
                .setNegativeButton("Voltar", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ListaInteressesActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }

                })
                .show();
    }

    private void saveInterests(final String interesses) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Salvando interesses");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, "http://robugos.com/advinci/db/update.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Interesses salvos.", Toast.LENGTH_LONG).show();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", idUser);
                params.put("interesses", interesses);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
