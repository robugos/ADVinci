package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;
import com.robugos.advinci.dominio.ListViewAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class ListaEventosActivity extends AppCompatActivity {

    private String TAG = ListaEventosActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private ListView lView;
    private static String url = "http://robugos.com/advinci/db/listaeventos.php";
    ArrayList<HashMap<String, String>> listaEventos;
    ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventos);

        listaEventos = new ArrayList<>();
        lView = (ListView) findViewById(R.id.listaEventos);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
 +        toolbar.setTitle(getString(R.string.title_listaeventos));
 +        setSupportActionBar(toolbar);
 +        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
 +        getSupportActionBar().setDisplayShowHomeEnabled(true);*/
        new GetEventos().execute();
    }

    //Classe AsyncTask para pegar jSON chamando HTTP
    private class GetEventos extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Mostra dialog de progresso
            pDialog = new ProgressDialog(ListaEventosActivity.this);
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
                    JSONArray eventos = jsonObj.getJSONArray("eventos");
                    //loop de todos os eventos
                    for (int i = 0; i < eventos.length(); i++){
                        JSONObject eve = eventos.getJSONObject(i);
                        String id = eve.getString("id");
                        String nome = eve.getString("nome");
                        String data = eve.getString("data");

                        data = (data.substring(8, 10))+"/"+(data.substring(5, 7))+
                                "/"+(data.substring(0, 4))+" às "+(data.substring(11, 13))+"h"+(data.substring(14, 16));
                        String local = eve.getString("local");
                        String urlimg = eve.getString("urlimg");
                        String adimg = eve.getString("adimg");
                        String nota = eve.getString("nota");

                        //hashmap temporario
                        HashMap<String, String> evento = new HashMap<>();
                        evento.put("id", id);
                        evento.put("nome", nome);
                        evento.put("data", data);
                        evento.put("local", local);
                        evento.put("urlimg", urlimg);
                        evento.put("adimg", adimg);
                        evento.put("nota", nota);


                        listaEventos.add(evento);
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
            adapter = new ListViewAdapter(ListaEventosActivity.this, listaEventos);
            //removeAntigos(311040000);
            sortListByData();
            lView.setAdapter(adapter);
            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(ListaEventosActivity.this, EventoActivity.class);
                    intent.putExtra("id", listaEventos.get(position).get("id"));
                    startActivity(intent);
                }
            });
        }

    }

    /*private void removeAntigos(int tolerancia) {
        Date hoje = new Date(System.currentTimeMillis() - tolerancia * 1000);
        for (Iterator<HashMap<String,String>> iterator = listaEventos.iterator(); iterator.hasNext(); ) {
            HashMap<String,String> evento = iterator.next();
            if(stringToDate(evento.get("data")).before(hoje)){
                iterator.remove();
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                listaEventos.clear();
                new GetEventos().execute();
                return true;

            case R.id.action_order_nome:
                sortListByNome();
                return true;

            case R.id.action_order_rating:
                sortListByRating();
                return true;

            case R.id.action_order_data:
                sortListByData();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void sortListByRating() {
        Collections.sort(listaEventos, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                Float nota1 = Float.parseFloat(lhs.get("nota").toString());
                Float nota2 = Float.parseFloat(rhs.get("nota").toString());
                if(null != lhs.get("nota") && null != rhs.get("nota")){
                    return nota2.compareTo(nota1);
                }else if(null != lhs.get("nota")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void sortListByData() {
        Collections.sort(listaEventos, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                Date data1 = stringToDate(lhs.get("data"));
                Date data2 = stringToDate(rhs.get("data"));
                if(null != lhs.get("data") && null != rhs.get("data")){
                    return data1.compareTo(data2);
                }else if(null != lhs.get("data")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    private void sortListByNome() {
        Collections.sort(listaEventos, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                String nome1 = lhs.get("nome").toString();
                String nome2 = rhs.get("nome").toString();
                if(null != lhs.get("nome") && null != rhs.get("nome")){
                    return nome1.compareTo(nome2);
                }else if(null != lhs.get("nome")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        adapter.notifyDataSetChanged();
    }

    public Date stringToDate(String data){
        String dia = data.substring(0,11);
        String hora = data.substring(14,16)+":"+data.substring(17,19);
        //System.out.println(dia);
        //System.out.println(hora);
        data = dia+hora;
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(data);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return convertedDate;
    }
    /*@Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }*/
}
