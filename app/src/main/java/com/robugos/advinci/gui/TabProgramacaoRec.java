package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;
import com.robugos.advinci.dominio.ListViewAdapter;
import com.robugos.advinci.dominio.tfidf.TfIdfMain;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_programacao_rec, container, false);
        listaEventos = new ArrayList<>();
        lView = (ListView) rootView.findViewById(R.id.listaEventos);
        loader.execute();
        return rootView;
    }

    //Classe AsyncTask para pegar jSON chamando HTTP
    private class GetEventos extends AsyncTask<Void, Void, Void> {
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
            saveArrayList();
            try {
                tfidf.calculaRecomendacao(getActivity().getApplicationInfo().dataDir+"/files", savedfiles);
            } catch (IOException e) {
                e.printStackTrace();
            }
            adapter = new ListViewAdapter(getActivity(), listaEventos, true);
            adapter.notifyDataSetChanged();
            lView.setAdapter(adapter);
            lView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), EventoActivity.class);
                    intent.putExtra("id", listaEventos.get(position).get("id"));
                    startActivityForResult(intent, 1);
                }
            });
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

    private List<String> stringToList(String valores){
        List<String> lista = new ArrayList<>();
        valores = valores.substring(1, valores.length()-1).replaceAll(" ","");
        lista = Arrays.asList(valores.split(","));
        return lista;
    }

    private void saveArrayList() {
        for (int i = 0; i < listaEventos.size(); i++) {

            try {
                FileOutputStream fileOutputStream = getActivity().openFileOutput(listaEventos.get(i).get("id")+"-evento"+i+".txt", getActivity().MODE_PRIVATE);
                ObjectOutputStream out = new ObjectOutputStream(fileOutputStream);
                //System.out.println(arrayList.get(i).get("id")+"\n"+arrayList.get(i).get("nome")+"\n"+arrayList.get(i).get("descricao"));
                out.writeObject(listaEventos.get(i).get("descricao"));
                out.close();
                fileOutputStream.close();
                savedfiles.add(listaEventos.get(i).get("id")+"-evento"+i+".txt");

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
    }


}
