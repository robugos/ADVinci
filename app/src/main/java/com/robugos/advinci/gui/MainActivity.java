package com.robugos.advinci.gui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.robugos.advinci.R;
import com.robugos.advinci.dao.SQLiteHandler;
import com.robugos.advinci.dao.SessionManager;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private SQLiteHandler db;
    private SessionManager session;

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
        Button perfilText = (Button) findViewById(R.id.button_perfil);
        perfilText.setText(nome);


        Button btnHistorico = (Button) findViewById(R.id.button_historico);
        btnHistorico.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(),
                        "Exibe o histórico de eventos", Toast.LENGTH_SHORT).show();
            }
        });

        Button btnOuvirAD = (Button) findViewById(R.id.button_ad);
        btnOuvirAD.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(),
                        "Busca redes para transmissão de AD", Toast.LENGTH_SHORT).show();
            }
        });

        /*Button btnAjustes = (Button) findViewById(R.id.button_config);
        btnAjustes.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Toast.makeText(getApplicationContext(),
                        "Exibe o menu de configuração do app", Toast.LENGTH_SHORT).show();
            }
        });*/

        Button btnPerfil = (Button) findViewById(R.id.button_perfil);
        btnPerfil.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Toast.makeText(getApplicationContext(), "Exibe o perfil de "+email, Toast.LENGTH_SHORT).show();
                logoutUser();
            }
        });
    }

    public void verProgramacao(View view){
        Intent intent = new Intent(this, ListaEventosActivity.class);
        startActivity(intent);
    }

    public void verInteresses(View view){
        Intent intent = new Intent(this, InteressesActivity.class);
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
