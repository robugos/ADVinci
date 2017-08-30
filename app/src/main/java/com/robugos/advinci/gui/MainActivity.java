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

        System.out.println("PATH: "+getApplicationInfo().dataDir);

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
        idUser = user.get("uid");
        /*TextView perfilText = (TextView) findViewById(R.id.welcome_user);
        perfilText.setText(mensagemBoasVindas()+", "+nome+"!");*/
        setTitle(mensagemBoasVindas()+", "+nome+"!");
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Dosis-Bold.ttf");

        Button btnPerfil = (Button) findViewById(R.id.button_perfil);
        Button btnProgramacao = (Button) findViewById(R.id.button_programacao);
        Button btnHistorico = (Button) findViewById(R.id.button_historico);
        Button btnConfig = (Button) findViewById(R.id.button_config);

        btnPerfil.setTypeface(typeface);
        btnProgramacao.setTypeface(typeface);
        btnHistorico.setTypeface(typeface);
        btnConfig.setTypeface(typeface);
    }

    public SessionManager getSession(){
        return session;
    }

    public SQLiteHandler getDb(){
        return db;
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
        Intent intent = new Intent(this, ProgramacaoActivity.class);
        startActivity(intent);
    }

    public void verSplash(View view){
        Intent intent = new Intent(this, SplashScreen.class);
        startActivity(intent);
    }

    public void verAjustes(View view){
    }

    public void verPerfil(View view){
        Intent intent = new Intent(this, PerfilActivity.class);
        startActivity(intent);
    }

    public void verHistorico(View view){
        Intent intent = new Intent(this, HistoricoActivity.class);
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
