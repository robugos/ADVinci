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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;
import com.robugos.advinci.dao.SQLiteHandler;
import com.robugos.advinci.dominio.AppController;
import com.robugos.advinci.dominio.CustomAdapter;
import com.robugos.advinci.dominio.DataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditarInteressesActivity extends AppCompatActivity {

    ArrayList<DataModel> dataModels;
    ListView listView;
    private CustomAdapter adapter;
    private String TAG = EditarInteressesActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private static String url = "http://robugos.com/advinci/db/listainteresses.php?uid=";
    private static String userId;
    private SQLiteHandler db = new SQLiteHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interesses);

        listView = (ListView) findViewById(R.id.listaInteresses);
        dataModels = new ArrayList<>();
        userId = db.getUserDetails().get("uid");
        new GetInteresses().execute();

    }

    //Classe AsyncTask para pegar jSON chamando HTTP
    private class GetInteresses extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Mostra dialog de progresso
            pDialog = new ProgressDialog(EditarInteressesActivity.this);
            pDialog.setMessage("Aguarde");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            ArrayList<String> itens = new ArrayList<>();
            String[] parts = {};
            //userId = "592dd7705cf739.39476295";
            HttpHandler sh = new HttpHandler();
            // Faz request a URL e pega a resposta
            String jsonStr = sh.chamaServico(url+userId);
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
                    parts = valores.split(",");

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
            for (int j = 0; j < parts.length; j++) {
                for (int i = 0; i < itens.size(); i++) {
                    String[] item;
                    item = itens.get(i).split(";");
                    DataModel model = getDataModel(dataModels, item[1]);
                    if (model==null){
                        if (item[0].equals(parts[j])){
                            dataModels.add(new DataModel(Integer.parseInt(item[0]), item[1], true));
                        }else{
                            dataModels.add(new DataModel(Integer.parseInt(item[0]), item[1], false));
                        }
                    }else{
                        if (item[0].equals(parts[j])){
                            model.setStatus(true);
                        }/*else{
                            //listaInteresses.remove(listaInteresses.indexOf(model.getIdString()));
                        }*/
                    }
                }
            }
            return null;
        }

        public DataModel getDataModel(ArrayList<DataModel> dataSet, String interesse) {
            for (DataModel item : dataSet) {
                if (item.getNome().equals(interesse)) {
                    return item;
                }
            }
            return null;
        }

        @Override
        protected  void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (pDialog.isShowing())
                pDialog.dismiss();
            adapter = new CustomAdapter(dataModels, getApplicationContext());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    DataModel dataModel = dataModels.get(position);
                    if (dataModel.getStatus()!=true){
                        dataModel.setStatus(true);
                    }else
                        dataModel.setStatus(false);
                    adapter.notifyDataSetChanged();


                }
            });
        }
    }

    public List<String> getInteresses(ArrayList<DataModel> dataSet, boolean status) {
        List<String> interesses = new ArrayList<>();
        for (DataModel item : dataSet) {
            if (item.getStatus()==status) {
                interesses.add(item.getIdString());
            }
        }
        return interesses;
    }

    public void saveInteresses(View view){
        if (getInteresses(dataModels, true).isEmpty()){
            Toast.makeText(this, "Selecione pelo menos um interesse", Toast.LENGTH_SHORT).show();
        }else{
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
                            Toast.makeText(getApplicationContext(), "Interesses salvos", Toast.LENGTH_LONG).show();
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
                    params.put("uid", userId);
                    params.put("interesses", getInteresses(dataModels, true).toString());
                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }
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