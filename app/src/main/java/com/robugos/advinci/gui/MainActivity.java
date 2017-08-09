package com.robugos.advinci.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.robugos.advinci.R;
import com.robugos.advinci.dao.SQLiteHandler;
import com.robugos.advinci.dao.SessionManager;

import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;
    private String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SqLite database handler
        db = new SQLiteHandler(this);

        // session manager
        session = new SessionManager(this);

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        final String nome = user.get("nome");
        final String email = user.get("email");
        idUser = user.get("uid");
        /*TextView perfilText = (TextView) findViewById(R.id.welcome_user);
        perfilText.setText(mensagemBoasVindas()+", "+nome+"!");*/
        setTitle(mensagemBoasVindas()+", "+nome+"!");
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Dosis-Bold.ttf");



        Button btnHistorico = (Button) findViewById(R.id.button_historico);
        Button btnProgramacao = (Button) findViewById(R.id.button_programacao);
        Button btnConfig = (Button) findViewById(R.id.button_config);
        Button btnPerfil = (Button) findViewById(R.id.button_perfil);

        btnHistorico.setTypeface(typeface);
        btnProgramacao.setTypeface(typeface);
        btnConfig.setTypeface(typeface);
        btnPerfil.setTypeface(typeface);


        /*btnHistorico.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(),
                        "Exibe o histórico de eventos", Toast.LENGTH_SHORT).show();
            }
        });*/

        /*Button btnOuvirAD = (Button) findViewById(R.id.button_ad);
        btnOuvirAD.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(),
                        "Busca redes para transmissão de AD", Toast.LENGTH_SHORT).show();
            }
        });*/

        /*Button btnPerfil = (Button) findViewById(R.id.button_perfil);
        btnPerfil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Toast.makeText(getApplicationContext(), "Exibe o perfil de "+email, Toast.LENGTH_SHORT).show();
                logoutUser();
            }
        });*/


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logoutUser();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public String mensagemBoasVindas(){
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String msg = "";
        if(timeOfDay >= 0 && timeOfDay < 12){
            msg = "Bom dia";
        }else if(timeOfDay >= 12 && timeOfDay < 16){
            msg = "Boa tarde";
        }else if(timeOfDay >= 16 && timeOfDay < 24) {
            msg = "Boa noite";
        }

        return msg;
    }

    public void verProgramacao(View view){
        Intent intent = new Intent(this, ListaEventosActivity.class);
        startActivity(intent);
    }

    public void verSplash(View view){
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
    }

    public void verInteresses(View view){
        Intent intent = new Intent(this, ListaInteressesActivity.class);
        intent.putExtra("uid", idUser);
        startActivity(intent);
    }

    private void logoutUser() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Sair da conta")
                .setMessage("Deseja realmente sair da conta atual?")
                .setIcon(0)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        session.setLogin(false);
                        db.deleteUsers();
                        // Launching the login activity
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Fechar ADVinci")
                .setMessage("Deseja realmente sair do ADVinci?")
                .setIcon(0)
                .setPositiveButton("Sim", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("Não", null)
                .show();
    }
}
