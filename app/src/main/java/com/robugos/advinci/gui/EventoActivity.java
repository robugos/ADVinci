package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.robugos.advinci.R;
import com.robugos.advinci.dao.HttpHandler;
import com.robugos.advinci.dominio.Evento;
import com.robugos.advinci.dominio.ImageLoadTask;
import com.robugos.advinci.dominio.ResizableImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EventoActivity extends AppCompatActivity {

    private static final String TAG = "EventoActivity";
    private ProgressDialog pDialog;
    private static String url = "http://robugos.com/advinci/db/evento.php?id=";
    private String id;
    private String userId;
    private Evento evento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        Intent intent = getIntent();
        id = intent.getExtras().getString("id");
        userId = intent.getExtras().getString("uid");
        new GetEvento().execute();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        returnIntent.putExtra("uid", userId);
        setResult(RESULT_OK, returnIntent);
        finish();
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
            String jsonStr = sh.chamaServico(url+id);
            Log.e(TAG, "Respotas da URL: " + jsonStr);
            if (jsonStr != null){
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    //Pega Array node do JSON
                    JSONArray e = jsonObj.getJSONArray("evento");

                        JSONObject eve = e.getJSONObject(0);
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
            System.out.println(evento.getNota());
            notaEventoBar.setRating(Float.parseFloat(evento.getNota()));
            notaEventoBar.setContentDescription(evento.getNome()+" avaliado em: "+evento.getNota()+" estrelas");
            String url = evento.getUrlimg();
            imagemEvento.setContentDescription(evento.getAdimg());
            new ImageLoadTask(url, imagemEvento).execute();
        }

    }
}


