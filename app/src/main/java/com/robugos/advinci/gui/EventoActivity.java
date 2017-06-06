package com.robugos.advinci.gui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.robugos.advinci.R;
import com.robugos.advinci.dominio.Evento;
import com.robugos.advinci.dominio.ImageLoadTask;
import com.robugos.advinci.dominio.ResizableImageView;

public class EventoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evento);

        Intent intent = getIntent();
        String data = intent.getExtras().getString("evento");
        Evento evento = new Evento(data);
        setTitle(evento.getNome());

        ResizableImageView imagemEvento = (ResizableImageView) findViewById(R.id.imagemEvento);
        /*WebView imageURL = (WebView) findViewById(R.id.imagemEventoURL);
        imageURL.setBackgroundColor(Color.TRANSPARENT);
        imageURL.loadData("<style>html,body{margin:0;}img{width: 100%;height: auto;}</style><img src=\""+evento.getUrlimg()+"\" alt=\"" + evento.getAdimg() + "\" title=\""+evento.getAdimg()+"\" />", "text/html; charset=utf-8", "utf-8");
        imageURL.setContentDescription(evento.getAdimg());*/
        TextView nomeEventoText = (TextView) findViewById(R.id.nomeEvento);
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
        notaEventoBar.setRating(Float.parseFloat(evento.getNota()));
        notaEventoBar.setContentDescription(evento.getNome()+" avaliado em: "+evento.getNota()+" estrelas");
        String url = evento.getUrlimg();
        imagemEvento.setContentDescription(evento.getAdimg());
        new ImageLoadTask(url, imagemEvento).execute();
    }
}
