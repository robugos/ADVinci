package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;
import com.robugos.advinci.dominio.InterestsAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;


public class InteressesActivity extends AppCompatActivity {

    private String TAG = InteressesActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lView;
    private static String url = "http://robugos.com/advinci/db/listainteresses.php";
    ArrayList<HashMap<String, String>> listaInteresses;
    InterestsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interesses);

        listaInteresses = new ArrayList<>();
        lView = (ListView) findViewById(R.id.listaInteresses);

        new GetInteresses().execute();

    }

    //Classe AsyncTask para pegar jSON chamando HTTP
    private class GetInteresses extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Mostra dialog de progresso
            pDialog = new ProgressDialog(InteressesActivity.this);
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
                    String secao = "";
                    for (int i = 0; i < interesses.length(); i++){
                        JSONObject interests = interesses.getJSONObject(i);
                        String id = interests.getString("id");
                        String nome = interests.getString("nome");
                        String categoria = interests.getString("categoria");

                        //hashmap temporario
                        HashMap<String, String> interesse = new HashMap<>();
                        interesse.put("id", id);
                        interesse.put("nome", nome);
                        interesse.put("categoria", categoria);

                        listaInteresses.add(interesse);
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
            adapter = new InterestsAdapter(InteressesActivity.this, listaInteresses);
            sortListByCategoria();
            lView.setAdapter(adapter);
            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getApplicationContext(), listaInteresses.get(position).get("id"), Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void sortListByCategoria() {
        Collections.sort(listaInteresses, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                String categoria1 = lhs.get("categoria").toString();
                String categoria2 = rhs.get("categoria").toString();
                if(null != lhs.get("categoria") && null != rhs.get("categoria")){
                    return categoria1.compareTo(categoria2);
                }else if(null != lhs.get("categoria")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }
}

