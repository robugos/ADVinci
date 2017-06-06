package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.View.OnClickListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A login screen that offers login via email/password.
 */
public class CreateUserActivity extends AppCompatActivity {

    private static final String TAG = "CreateUserActivity";
    private static Usuario user;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createuser);

        TextView nomeText = (TextView) findViewById(R.id.nomeUsuario);
        TextView sobrenomeText = (TextView) findViewById(R.id.sobrenomeUsuario);
        TextView emailText = (TextView) findViewById(R.id.email);
        TextView senhaText = (TextView) findViewById(R.id.password);
        TextView confirmasenhaText = (TextView) findViewById(R.id.passwordConfirm);
        final List<TextView> usuario = new ArrayList<>();
        usuario.add(nomeText);
        usuario.add(sobrenomeText);
        usuario.add(emailText);
        usuario.add(senhaText);
        usuario.add(confirmasenhaText);
        Button loginButton = (Button) findViewById(R.id.email_sign_in_button);
        final Button registerButton = (Button) findViewById(R.id.create_account);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        // Session manager
        session = new SessionManager(getApplicationContext());

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(CreateUserActivity.this,
                    MainActivity.class);
            startActivity(intent);
            finish();
        }

        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(getApplicationContext(), CreateUserActivity.class);
                //startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
            }
        });

        registerButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //register(registerButton, usuario);
                registerUser(registerButton, usuario);
            }
        });
    }

    private void registerUser(Button registerButton, final List<TextView> usuario) {
        // Tag used to cancel the request
        if (!validade(registerButton, usuario)){
            onRegisterFailed(registerButton);
            return;
        }else {
            String tag_string_req = "req_register";

            pDialog.setMessage("Criando conta");
            showDialog();

            StringRequest strReq = new StringRequest(Method.POST,
                    "http://robugos.com/advinci/db/register.php", new Response.Listener<String>() {

                @Override
                public void onResponse(String response) {
                    Log.d(TAG, "Register Response: " + response.toString());
                    hideDialog();

                    try {
                        JSONObject jObj = new JSONObject(response);
                        boolean error = jObj.getBoolean("error");
                        if (!error) {
                            // User successfully stored in MySQL
                            // Now store the user in sqlite
                            String uid = jObj.getString("uid");

                            JSONObject user = jObj.getJSONObject("usuario");
                            String nome = user.getString("nome");
                            String sobrenome = user.getString("sobrenome");
                            String email = user.getString("email");
                            String criado_em = user.getString("criado_em");

                            // Inserting row in users table
                            db.addUser(nome, sobrenome, email, uid, criado_em);

                            Toast.makeText(getApplicationContext(), "Conta criada com sucesso.", Toast.LENGTH_LONG).show();

                            // Launch login activity
                            Intent intent = new Intent(
                                    CreateUserActivity.this,
                                    LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {

                            // Error occurred in registration. Get the error
                            // message
                            String errorMsg = jObj.getString("error_msg");
                            Toast.makeText(getApplicationContext(),
                                    errorMsg, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e(TAG, "Registration Error: " + error.getMessage());
                    Toast.makeText(getApplicationContext(),
                            error.getMessage(), Toast.LENGTH_LONG).show();
                    hideDialog();
                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    // Posting params to register url
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("nome", usuario.get(0).getText().toString());
                    params.put("sobrenome", usuario.get(1).getText().toString());
                    params.put("email", usuario.get(2).getText().toString());
                    params.put("senha", usuario.get(3).getText().toString());

                    return params;
                }

            };

            // Adding request to request queue
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

     public void onRegisterFailed(Button registerButton){
        //Toast.makeText(getBaseContext(), "C")
        registerButton.setEnabled(true);
    }

    public boolean validade(Button registerButton, List<TextView> usuario){
        boolean valid = true;

        String nome = usuario.get(0).getText().toString();
        String sobrenome = usuario.get(1).getText().toString();
        String email = usuario.get(2).getText().toString();
        String senha = usuario.get(3).getText().toString();
        String confirmasenha = usuario.get(4).getText().toString();

        if (nome.isEmpty() || nome.length() < 2){
            usuario.get(0).setError("Nome precisa ser maior que 2 letras");
            valid = false;
        }else{
            usuario.get(0).setError(null);
        }

        if (sobrenome.isEmpty() || sobrenome.length() < 2){
            usuario.get(1).setError("Sobrenome precisa ser maior que 2 letras");
            valid = false;
        }else{
            usuario.get(1).setError(null);
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            usuario.get(2).setError("Email inválido");
            valid = false;
        }else{
            usuario.get(2).setError(null);
        }

        if (senha.isEmpty() || senha.length() < 4){
            usuario.get(3).setError("Senha deve ser maior que 4 dígitos");
            valid = false;
        }else {
            usuario.get(3).setError(null);
        }

        if (!senha.equals(confirmasenha)){
            System.out.println("Senha: "+senha);
            System.out.println("Confirma senha: "+confirmasenha);
            usuario.get(4).setError("Senhas não conferem");
            valid = false;
        }else {
            usuario.get(4).setError(null);
        }

        return valid;
    }
}

