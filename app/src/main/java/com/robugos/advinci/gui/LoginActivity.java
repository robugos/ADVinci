package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.robugos.advinci.R;
import com.robugos.advinci.dao.SQLiteHandler;
import com.robugos.advinci.dao.SessionManager;
import com.robugos.advinci.dominio.AppController;
import com.robugos.advinci.dominio.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    HashMap<String, String> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView emailText = (TextView) findViewById(R.id.email);
        final TextView senhaText = (TextView) findViewById(R.id.password);

        final Button loginButton = (Button) findViewById(R.id.email_sign_in_button);
        Button registerButton = (Button) findViewById(R.id.create_account);
        TextView forgotPassword = (TextView) findViewById(R.id.forgotpassword);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        user = db.getUserDetails();
        session = new SessionManager(this);

        if (session.isLoggedIn()) {
            checkLogin();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login(loginButton, emailText, senhaText);
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateUserActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassActivity.class);
                startActivity(intent);
            }
        });
    }

    public void login(final Button loginButton, TextView emailText, TextView senhaText){
        Log.d(TAG, "Login");

        if (!validate(loginButton, emailText, senhaText)){
            onLoginFailed(loginButton);
            return;
        }

        //loginButton.setEnabled(false);

//        pDialog = new ProgressDialog(LoginActivity.this);
        pDialog.setMessage("Aguarde");
        pDialog.setCancelable(false);
        pDialog.show();

        String email = emailText.getText().toString();
        String senha = senhaText.getText().toString();
        Usuario usuario = new Usuario(email, senha);
        Usuario user = usuario;
        checkLogin(user);
    }

    private void checkLogin(){
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Method.POST,
                "http://robugos.com/advinci/db/login.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        session.setLogin(true);
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("usuario");
                        String nome = user.getString("nome");
                        String sobrenome = user.getString("sobrenome");

                        db.updateUser(uid, nome, sobrenome);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                System.out.println("userid: "+user.get("uid"));
                params.put("uid", user.get("uid"));

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void checkLogin(Usuario user){
        final String email = user.getEmail();
        final String senha = user.getSenha();
        String tag_string_req = "req_login";

        pDialog.setMessage("Entrando");
        showDialog();

        StringRequest strReq = new StringRequest(Method.POST,
                "http://robugos.com/advinci/db/login.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        session.setLogin(true);
                        String uid = jObj.getString("uid");

                        JSONObject user = jObj.getJSONObject("usuario");
                        String nome = user.getString("nome");
                        String sobrenome = user.getString("sobrenome");
                        String email = user.getString("email");
                        String criado_em = user.getString("criado_em");

                        db.addUser(nome, sobrenome, email, uid, criado_em);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("senha", senha);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == REQUEST_SIGNUP){
            if (resultCode == RESULT_OK){
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed(){
        moveTaskToBack(true);
    }

    public void onLoginFailed(Button loginButton){
        loginButton.setEnabled(true);
    }

    public boolean validate(Button loginButton, TextView emailText, TextView senhaText){
        boolean valid = true;

        String email = emailText.getText().toString();
        String senha = senhaText.getText().toString();

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailText.setError("Email inválido");
            valid = false;
        }else{
            emailText.setError(null);
        }

        if (senha.isEmpty() || senha.length() < 4) {
            senhaText.setError("Senha deve ser maior que 4 dígitos");
            valid = false;
        }else{
            senhaText.setError(null);
        }

        return valid;
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

