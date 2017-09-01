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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.robugos.advinci.R;
import com.robugos.advinci.dominio.AppController;
import com.robugos.advinci.dominio.Usuario;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ForgotPassActivity extends AppCompatActivity {

    private static final String TAG = "ForgotPassActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog pDialog;
    private Usuario user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpass);

        final TextView emailText = (TextView) findViewById(R.id.email);
        final Button recoverButton = (Button) findViewById(R.id.email_recover_button);

        recoverButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recuperar(recoverButton, emailText);
            }
        });
    }

    public void recuperar(final Button recoverButton, TextView emailText){
        Log.d(TAG, "Recuperar");

        if (!validate(recoverButton, emailText)){
            return;
        }

        pDialog = new ProgressDialog(ForgotPassActivity.this);
        pDialog.setMessage("Aguarde");
        pDialog.setCancelable(false);
        pDialog.show();

        String email = emailText.getText().toString();
        recuperarSenha(email);

    }

    private void recuperarSenha(final String email){
        String tag_string_req = "req_login";

        pDialog.setMessage("Aguarde");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://robugos.com/advinci/db/mail.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        Toast.makeText(getApplicationContext(), "Email de recuperação enviado", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(ForgotPassActivity.this, LoginActivity.class);
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

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    public void onLoginFailed(Button loginButton){
        loginButton.setEnabled(true);
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

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }


}

