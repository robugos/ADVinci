package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;
import com.robugos.advinci.dao.SQLiteHandler;
import com.robugos.advinci.dominio.AppController;
import com.robugos.advinci.dominio.Evento;
import com.robugos.advinci.dominio.ImageLoadTask;
import com.robugos.advinci.dominio.ResizableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EventoActivity extends AppCompatActivity {

    private static final String TAG = "EventoActivity";
    private ProgressDialog pDialog;
    private static String url = "http://robugos.com/advinci/db/evento.php";
    private String id;
    private Evento evento;
    private static String userId;
    private static String notauser;
    private SQLiteHandler db = new SQLiteHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        userId = db.getUserDetails().get("uid");
        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        new GetEvento().execute();

    }

    public void avaliarEvento(View view){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EventoActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.alertdialog_avaliar_evento, null);
        final RatingBar notaEventoDialog = (RatingBar) mView.findViewById(R.id.ratingEvento);
        if (notauser==null || notauser.equals("null")){
            notaEventoDialog.setRating(Float.parseFloat(evento.getNota()));
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
                                new GetEvento().execute();
                                Toast.makeText(getApplicationContext(), "Evento avaliado", Toast.LENGTH_LONG).show();
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
                        params.put("id", userId);
                        params.put("evento", evento.getId());
                        params.put("nota", String.valueOf(notaEventoDialog.getRating()));
                        return params;
                    }

                };

                AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    //Classe AsyncTask para pegar jSON chamando HTTP
    private class GetEvento extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            //Mostra dialog de progresso
            pDialog = new ProgressDialog(EventoActivity.this);
            pDialog.setMessage("Aguarde");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){
            HttpHandler sh = new HttpHandler();
            // Faz request a URL e pega a resposta
            String jsonStr = sh.chamaServico(url+"?id="+id+"&uid="+userId);
            Log.e(TAG, "Respotas da URL: " + jsonStr);
            if (jsonStr != null){
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Pega Array node do JSON
                    JSONArray e = jsonObj.getJSONArray("evento");

                        JSONObject eve = e.getJSONObject(0);
                    notauser = eve.getString("notauser");
                    //System.out.println("nota user: "+notauser);
                    String data = eve.getString("data");
                    data = (data.substring(8, 10))+"/"+(data.substring(5, 7))+
                            "/"+(data.substring(0, 4))+" às "+(data.substring(11, 13))+"h"+(data.substring(14, 16));
                    Evento event = new Evento(eve.getString("id"), eve.getString("nome"), eve.getString("local"), data, eve.getString("descricao"), eve.getString("urlimg"), eve.getString("adimg"), eve.getString("nota"));
                    evento = event;

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
            setTitle(evento.getNome());
            TextView nomeEventoText = (TextView) findViewById(R.id.nomeEvento);
            ResizableImageView imagemEvento = (ResizableImageView) findViewById(R.id.imagemEvento);
            TextView localEventoText = (TextView) findViewById(R.id.localEvento);
            TextView dataEventoText = (TextView) findViewById(R.id.dataEvento);
            WebView descricaoEventoText = (WebView) findViewById(R.id.descricaoEvento);
            WebSettings settings = descricaoEventoText.getSettings();
            settings.setDefaultTextEncodingName("utf-8");
            descricaoEventoText.setBackgroundColor(Color.TRANSPARENT);
            RatingBar notaEventoBar = (RatingBar) findViewById(R.id.ratingEvento);
            nomeEventoText.setText(evento.getNome());
            localEventoText.setText(evento.getLocal());
            dataEventoText.setText(evento.getData());
            descricaoEventoText.loadData("<style>html,body{margin:0; color:#737373;}</style><div style=\"text-align: justify;\n\">"+evento.getDescricao()+"</div>", "text/html; charset=utf-8", "utf-8");
            //System.out.println(evento.getNota());
            notaEventoBar.setRating(Float.parseFloat(evento.getNota()));
            notaEventoBar.setContentDescription(evento.getNome()+" avaliado em: "+evento.getNota()+" estrelas");
            String url = evento.getUrlimg();
            imagemEvento.setContentDescription(evento.getAdimg());
            new ImageLoadTask(url, imagemEvento).execute();
        }

    }
}


