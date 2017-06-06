package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.robugos.advinci.R;
import com.robugos.advinci.dominio.Usuario;


public class ForgotPassActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPassActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog pDialog;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        /*ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/
        final TextView emailText = (TextView) findViewById(R.id.email);
        final Button recoverButton = (Button) findViewById(R.id.email_recover_button);
    }

    public void login(final Button recoverButton, TextView emailText){
        Log.d(TAG, "Recuperar");

        if (!validate(recoverButton, emailText)){
            onLoginFailed(recoverButton);
            return;
        }

        recoverButton.setEnabled(false);

        /*final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Autenticando");
        progressDialog.show();*/
        pDialog = new ProgressDialog(ForgotPassActivity.this);
        pDialog.setMessage("Aguarde");
        pDialog.setCancelable(false);
        pDialog.show();

        //AUTENTICACAO

        String email = emailText.getText().toString();

        //user.setEmail(email);
        //user.setSenha(senha);
        /*LoginActivity.AsyncT asyncT = new LoginActivity.AsyncT();
        asyncT.execute();*/

        new android.os.Handler().postDelayed(
                new Runnable(){
                    public void run(){
                        onLoginSuccess(recoverButton);
                        pDialog.dismiss();
                    }
                }, 300);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_SIGNUP){
            if (resultCode == RESULT_OK){
                //SUCCESFUL SINGUP
                this.finish();
            }
        }
    }

    /*@Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }*/

    public void onLoginSuccess(Button recoverButton){
        recoverButton.setEnabled(true);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void onLoginFailed(Button recoverButton){
        //Toast.makeText(getBaseContext(), "Login falhou", Toast.LENGTH_LONG).show();
        recoverButton.setEnabled(true);
    }

    public boolean validate(Button recoverButton, TextView emailText){
        boolean valid = true;

        String email = emailText.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email inválido");
            valid = false;
        }else{
            emailText.setError(null);
        }

        return valid;
    }

    public Usuario getUser(){
        return user;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivityForResult(myIntent, 0);
        return true;

    }


}

