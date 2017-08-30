package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;
import com.robugos.advinci.dominio.AppController;
import com.robugos.advinci.dominio.ListViewAdapter;
import com.robugos.advinci.dominio.tfidf.TfIdfMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TabProgramacaoRec extends Fragment {
    ArrayList<HashMap<String, String>> listaEventos;
    private ListView lView;
    private String TAG = TabProgramacaoRec.class.getSimpleName();
    private static String url = "http://robugos.com/advinci/db/listaeventos.php?uid=";
    private ProgressDialog pDialog;
    private GetEventos loader = new GetEventos();
    ListViewAdapter adapter;
    ProgramacaoActivity programacao = new ProgramacaoActivity();
    TfIdfMain tfidf = new TfIdfMain();
    List<String> interesses = new ArrayList<>();
    List<String> savedfiles = new ArrayList<>();
    private static String notauser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_programacao_rec, container, false);
        setHasOptionsMenu(true);
        listaEventos = new ArrayList<>();
        lView = (ListView) rootView.findViewById(R.id.listaEventos);
        loader.execute();
        return rootView;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getActivity().getMenuInflater().inflate(R.menu.actionbar, menu);
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
                listaEventos = sortListByNome(listaEventos);
                adapter.notifyDataSetChanged();
                return true;

            case R.id.action_order_rating:
                listaEventos = sortListByRating(listaEventos);
                adapter.notifyDataSetChanged();
                return true;

            case R.id.action_order_data:
                listaEventos = sortListByData(listaEventos);
                adapter.notifyDataSetChanged();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.add(0, v.getId(), 0, "Avaliar");
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    //Classe AsyncTask para pegar jSON chamando HTTP
    class GetEventos extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Mostra dialog de progresso
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Aguarde");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            String[] parts = {};
            HttpHandler sh = new HttpHandler();
            // Faz request a URL e pega a resposta
            String jsonStr = sh.chamaServico(url+programacao.getUserId());
            Log.e(TAG, "Respotas da URL: " + jsonStr);
            if (jsonStr != null){
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Pega Array node do JSON
                    JSONArray eventos = jsonObj.getJSONArray("eventos");

                    for (int i = 0; i < eventos.length(); i++){
                        JSONObject eve = eventos.getJSONObject(i);
                        String id = eve.getString("id");
                        String nome = eve.getString("nome");
                        String descricao = eve.getString("descricao");
                        String data = eve.getString("data");

                        data = (data.substring(8, 10))+"/"+(data.substring(5, 7))+
                                "/"+(data.substring(0, 4))+" às "+(data.substring(11, 13))+"h"+(data.substring(14, 16));
                        String local = eve.getString("local");
                        String urlimg = eve.getString("urlimg");
                        String adimg = eve.getString("adimg");
                        String categoria = eve.getString("categoria");
                        String peso = eve.getString("peso");
                        notauser = eve.getString("notauser");
                        //System.out.println("Categoria JSON: "+categoria);
                        String nota = eve.getString("nota");

                        //hashmap temporario
                        HashMap<String, String> evento = new HashMap<>();
                        evento.put("id", id);
                        evento.put("nome", nome);
                        evento.put("descricao", descricao);
                        evento.put("data", data);
                        evento.put("local", local);
                        evento.put("urlimg", urlimg);
                        evento.put("adimg", adimg);
                        evento.put("categoria", categoria);
                        evento.put("peso", peso);
                        //System.out.println("Categoria HASHMAP: "+evento.get("categoria"));
                        evento.put("nota", nota);


                        listaEventos.add(evento);
                    }
                    interesses = stringToList(jsonObj.getString("userinteresses"));

                } catch (final JSONException e) {
                    Log.e(TAG, "Erro do JSON parsing: " + e.getMessage());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    "Erro: " + e.getMessage(), Toast.LENGTH_LONG)
                                    .show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Sem conexão");
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(),
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
            /*saveArrayList();
            try {
                tfidf.calculaRecomendacao(getActivity().getApplicationInfo().dataDir+"/files", savedfiles);
                System.out.println("depois do try: "+tfidf.getLista().toString());
                listaEventos = listaToEvento(tfidf.getLista());
            } catch (IOException e) {
                e.printStackTrace();
            }*/
            listaEventos = sortListByPeso(listaEventos);
            adapter = new ListViewAdapter(getActivity(), listaEventos, true);
            adapter.notifyDataSetChanged();
            lView.setAdapter(adapter);
            registerForContextMenu(lView);
            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), EventoActivity.class);
                    intent.putExtra("id", listaEventos.get(position).get("id"));
                    startActivityForResult(intent, 1);
                }
            });

            /*lView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(getActivity().getApplicationContext(),listaEventos.get(position).get("id"), Toast.LENGTH_LONG).show();
                    Log.v("long clicked","pos: " + position);

                    return true;
                }
            });*/
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (loader != null) {
                loader.cancel(true);
                loader = null;
            }
        } catch (Exception e) {

        }

    }

    public void avaliarEvento(final int position){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        View mView = getActivity().getLayoutInflater().inflate(R.layout.alertdialog_avaliar_evento, null);
        final RatingBar notaEventoDialog = (RatingBar) mView.findViewById(R.id.ratingEvento);
        if (notauser==null || notauser.equals("null")){
            System.out.println("position :"+position);
            notaEventoDialog.setRating(Float.parseFloat(listaEventos.get(position).get("nota")));
        }else{
            notaEventoDialog.setRating(Float.parseFloat(notauser));
        }
        Button mAvaliar = (Button) mView.findViewById(R.id.avaliarEvento);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        mAvaliar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tag_string_req = "req_register";

                pDialog.setMessage("Aguarde");
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
                                dialog.dismiss();
                                new GetEventos().execute();
                                Toast.makeText(getActivity().getApplicationContext(), "Evento avaliado", Toast.LENGTH_LONG).show();
                            } else {
                                String errorMsg = jObj.getString("error_msg");
                                Toast.makeText(getActivity().getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Registration Error: " + error.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        hideDialog();
                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() {

                        Map<String, String> params = new HashMap<String, String>();
                        params.put("id", programacao.getUserId());
                        params.put("evento", listaEventos.get(position).get("id"));
                        params.put("nota", String.valueOf(notaEventoDialog.getRating()));
                        return params;
                    }

                };

                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });
    }

    private List<String> stringToList(String valores){
        List<String> list;
        valores = valores.substring(1, valores.length()-1).replaceAll(" ","");
        list = Arrays.asList(valores.split(","));
        return list;
    }

    private ArrayList<HashMap<String,String>> sortListByPeso(ArrayList<HashMap<String,String>> list) {
        Collections.sort(list, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                Float peso1 = Float.parseFloat(lhs.get("peso").toString());
                Float peso2 = Float.parseFloat(rhs.get("peso").toString());
                if(null != lhs.get("peso") && null != rhs.get("peso")){
                    return peso2.compareTo(peso1);
                }else if(null != lhs.get("peso")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        return list;
        //adapter.notifyDataSetChanged();
    }

    private ArrayList<HashMap<String, String>> sortListByNome(ArrayList<HashMap<String, String>> list) {
        Collections.sort(list, new Comparator<HashMap< String,String >>() {
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
        return list;
    }

    private ArrayList<HashMap<String, String>> sortListByData(ArrayList<HashMap<String, String>> list) {
        Collections.sort(list, new Comparator<HashMap< String,String >>() {
            @Override
            public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
                Date data1 = programacao.stringToDate(lhs.get("data"));
                Date data2 = programacao.stringToDate(rhs.get("data"));
                if(null != lhs.get("data") && null != rhs.get("data")){
                    return data1.compareTo(data2);
                }else if(null != lhs.get("data")){
                    return 1;
                }else{
                    return -1;
                }
            }
        });
        return list;
    }

    private ArrayList<HashMap<String, String>> sortListByRating(ArrayList<HashMap<String, String>> list) {
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
        return list;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /*private void saveArrayList() {
        for (int i = 0; i < listaEventos.size(); i++) {

            try {
                FileOutputStream fileOutputStream = getActivity().openFileOutput(String.format("%02d", Integer.parseInt(listaEventos.get(i).get("id")))+"-evento"+String.format("%02d", i)+".txt", getActivity().MODE_PRIVATE);
                ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
                //System.out.println(arrayList.get(i).get("id")+"\n"+arrayList.get(i).get("nome")+"\n"+arrayList.get(i).get("descricao"));
                out.writeObject(listaEventos.get(i).get("descricao"));
                out.close();
                fileOutputStream.close();
                savedfiles.add(String.format("%02d", Integer.parseInt(listaEventos.get(i).get("id")))+"-evento"+String.format("%02d", i)+".txt");

            } catch (IOException e) {
                e.printStackTrace();
            }

            for (int j=0; j<savedfiles.size(); j++){
                String ret = "";

                try {
                    InputStream inputStream = getActivity().openFileInput(savedfiles.get(i));

                    if ( inputStream != null ) {
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString = "";
                        StringBuilder stringBuilder = new StringBuilder();

                        while ( (receiveString = bufferedReader.readLine()) != null ) {
                            stringBuilder.append(receiveString);
                        }

                        inputStream.close();
                        ret = stringBuilder.toString();
                    }
                }
                catch (FileNotFoundException e) {
                    Log.e("login activity", "File not found: " + e.toString());
                } catch (IOException e) {
                    Log.e("login activity", "Can not read file: " + e.toString());
                }

                //System.out.println("ANTES DE LER, SALVANDO: "+ret);
            }
        }
    }*/


}
