package com.robugos.advinci.gui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.robugos.advinci.R;
import com.robugos.advinci.dao.SQLiteHandler;
import com.robugos.advinci.dominio.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PerfilActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private String TAG = PerfilActivity.class.getSimpleName();
    private static String userId;
    private SQLiteHandler db = new SQLiteHandler(this);
    private AlertDialog dialog;

    ListView listView;
    String[] values = new String[] { "Editar nome",
            "Editar localização",
            "Editar interesses",
            "Alterar senha"
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        userId = db.getUserDetails().get("uid");
        pDialog = new ProgressDialog(PerfilActivity.this);

    }

    public void editarInteresses(View view){
        Intent intent = new Intent(this, EditarInteressesActivity.class);
        startActivity(intent);
    }

    public void editarNome(View view){
        View mView = getLayoutInflater().inflate(R.layout.alertdialog_editar_nome, null);
        final TextView nomeDialog = (TextView) mView.findViewById(R.id.nomeUsuario);
        nomeDialog.setText(db.getUserDetails().get("nome"));
        final TextView sobrenomeDialog = (TextView) mView.findViewById(R.id.sobrenomeUsuario);
        sobrenomeDialog.setText(db.getUserDetails().get("sobrenome"));
        final List<TextView> usuario = new ArrayList<>();
        usuario.add(nomeDialog);
        usuario.add(sobrenomeDialog);
        final Button mEditar = (Button) mView.findViewById(R.id.salvarNome);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
        updateNome(mEditar, usuario);
    }

    public void alterarSenha(View view){
        View mView = getLayoutInflater().inflate(R.layout.alertdialog_alterar_senha, null);
        final TextView senhaAntigaDialog = (TextView) mView.findViewById(R.id.senhaAntiga);
        final TextView senhaNovaDialog = (TextView) mView.findViewById(R.id.senhaNova);
        final TextView confirmaNovaDialog = (TextView) mView.findViewById(R.id.confirmaSenha);
        final List<TextView> usuario = new ArrayList<>();
        usuario.add(senhaAntigaDialog);
        usuario.add(senhaNovaDialog);
        usuario.add(confirmaNovaDialog);
        final Button mAlterar = (Button) mView.findViewById(R.id.salvarSenha);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
        changeSenha(mAlterar, usuario);
    }

    public void updateNome(Button mEditar, final List<TextView> usuario) {
        if (!validate(mEditar, usuario, "nome")){
            onRegisterFailed(mEditar);
            return;
        }else {
            String tag_string_req = "req_register";

            dialog.dismiss();
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
                            db.updateUser(userId, usuario.get(0).getText().toString(), usuario.get(1).getText().toString());
                            Toast.makeText(getApplicationContext(), "Nome de exibição editado", Toast.LENGTH_LONG).show();
                        } else {
                            dialog.show();
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
                    params.put("uid", userId);
                    params.put("nome", usuario.get(0).getText().toString());
                    params.put("sobrenome", usuario.get(1).getText().toString());
                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

    }

    public void changeSenha(Button mAlterar, final List<TextView> usuario) {
        if (!validate(mAlterar, usuario, "senha")){
            onRegisterFailed(mAlterar);
            return;
        }else {
            String tag_string_req = "req_register";

            dialog.dismiss();
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
                            //session
                            Toast.makeText(getApplicationContext(), "Senha de acesso alterada", Toast.LENGTH_LONG).show();
                        } else {
                            dialog.show();
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
                    params.put("uid", userId);
                    params.put("nome", usuario.get(0).getText().toString());
                    params.put("sobrenome", usuario.get(1).getText().toString());
                    return params;
                }

            };

            AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
        }

    }

    public boolean validate(Button registerButton, List<TextView> usuario, String tipo){
        boolean valid = true;

        if (tipo == "nome") {
            String nome = usuario.get(0).getText().toString();
            String sobrenome = usuario.get(1).getText().toString();
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
        } else if (tipo == "senha"){
            String senhaAntiga = usuario.get(0).getText().toString();
            String senhaNova = usuario.get(1).getText().toString();
            String confirmasenha = usuario.get(2).getText().toString();
            if (senhaAntiga.isEmpty() || senhaAntiga.length() < 1){
                usuario.get(0).setError("Senha deve ser maior que 4 dígitos");
                valid = false;
            }else {
                usuario.get(0).setError(null);
            }

            if (senhaNova.isEmpty() || senhaNova.length() < 1){
                usuario.get(1).setError("Senha deve ser maior que 4 dígitos");
                valid = false;
            }else {
                usuario.get(1).setError(null);
            }

            if (!senhaNova.equals(confirmasenha)){
                System.out.println("Senha: "+senhaNova);
                System.out.println("Confirma senha: "+confirmasenha);
                usuario.get(2).setError("Senhas não conferem");
                valid = false;
            }else {
                usuario.get(2).setError(null);
            }
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

    public void onRegisterFailed(Button registerButton){
        //Toast.makeText(getBaseContext(), "C")
        registerButton.setEnabled(true);
    }


}
