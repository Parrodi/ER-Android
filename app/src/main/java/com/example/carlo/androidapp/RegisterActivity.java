package com.example.carlo.androidapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener{
    private static final int SIGN_IN_CODE = 777;
    private GoogleApiClient googleApiClient;
    public String answer;
    Button fb, gg;
    EditText name, email, phone, pswd, pswdc;
    TextView em, nom, tel, psw1, psw2, CO;
    CheckBox terminosycondiciones;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fb = (Button)findViewById(R.id.inicioFb);
        gg = (Button)findViewById(R.id.inicioG);
        name = (EditText)findViewById(R.id.nombreCompleto);
        email = (EditText)findViewById(R.id.correoElectrónico);
        phone = (EditText)findViewById(R.id.numeroTelefono);
        pswd = (EditText)findViewById(R.id.password);
        pswdc = (EditText)findViewById(R.id.passwordConfirm);
        em = (TextView) findViewById(R.id.textViewem);
        nom = (TextView) findViewById(R.id.textViewnom);
        tel = (TextView) findViewById(R.id.textViewtel);
        psw1 = (TextView) findViewById(R.id.textViewpsw1);
        psw2 = (TextView) findViewById(R.id.textViewpsw2);
        terminosycondiciones = (CheckBox) findViewById(R.id.terminosCondiciones);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {

                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String password = object.getString("id");
                                    new RegisterActivity.RegisterUserManager().execute("http://ertourister.appspot.com/user/add", name, email, password);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(RegisterActivity.this, "no se pudieron los datos de fb",
                                            Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(RegisterActivity.this, "FacebookCancel",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(RegisterActivity.this, "Facebook Error",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        GoogleSignInOptions GSO = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
            /*Toast.makeText(LoginActivity.this, result.getSignInAccount().getEmail(),
                    Toast.LENGTH_SHORT).show();*/


            Toast.makeText(RegisterActivity.this, "Google Success!",
                    Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(RegisterActivity.this, "Google Fail",
                    Toast.LENGTH_SHORT).show();
        }
    }


    private class  RegisterUserManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            CO = (TextView) findViewById(R.id.textViewCO);
            StringBuffer response = new StringBuffer();
            try{
                URL url = new URL(strings[0]);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                HashMap<String, String> postParams = new HashMap<>();
                postParams.put("name", strings[1]);
                postParams.put("email", strings[2]);
                postParams.put("password", strings[3]);



                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostString(postParams));
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK){
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while((line = br.readLine()) != null){
                        response.append(line);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }

            return response.toString();
        }


        private String getPostString(HashMap<String, String> params)throws UnsupportedEncodingException {

            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for(Map.Entry<String, String> entry: params.entrySet()){
                if(first)
                    first = false;
                else
                    sb.append("&");
                sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
                sb.append("=");
                sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));

            }
            return sb.toString();

        }

        protected void onPostExecute(String response){
            try {
                JSONObject jsonresponse = new JSONObject(response);
                answer= jsonresponse.getString("info");
            } catch (JSONException e) {
                e.printStackTrace();
                answer = "fail";
            }

            CO.setText(answer);
        }

    }




    public void iniciarfb(View v){
        LoginManager.getInstance().logInWithReadPermissions(RegisterActivity.this, Arrays.asList("public_profile"));
    }

    public void iniciargg(View v){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, SIGN_IN_CODE);
    }







    public void registro(View v){
        boolean correctinput=true;
        String message;


       if (inputFormatAuthentication.isEmailValid(email.getText().toString())){
           em.setTextColor(Color.parseColor("#707070"));
       }else {
           email.setText("");
           em.setTextColor(Color.RED);
           correctinput = false;
       }

        if (inputFormatAuthentication.passwordsMatch(pswd.getText().toString(), pswdc.getText().toString())){
            psw1.setTextColor(Color.parseColor("#707070"));
            psw2.setTextColor(Color.parseColor("#707070"));
        }else {
            pswd.setText("");
            pswdc.setText("");
            psw1.setTextColor(Color.RED);
            psw2.setTextColor(Color.RED);
            correctinput = false;
        }

        if (inputFormatAuthentication.isNameValid(name.getText().toString())){
            nom.setTextColor(Color.parseColor("#707070"));
        }else {
            name.setText("");
            nom.setTextColor(Color.RED);
            correctinput = false;
        }

        if (inputFormatAuthentication.isNumberValid(phone.getText().toString())){
            tel.setTextColor(Color.parseColor("#707070"));
        }else {
            phone.setText("");
            tel.setTextColor(Color.RED);
            correctinput = false;
        }

        if(terminosycondiciones.isChecked())
            terminosycondiciones.setTextColor(Color.parseColor("#707070"));
       else {
            terminosycondiciones.setTextColor(Color.RED);
            correctinput = false;
        }

       if (correctinput){
           message = "Input Válido y Completo!";
           new RegisterUserManager().execute("http://ertourister.appspot.com/user/add", name.getText().toString(), email.getText().toString(),pswd.getText().toString(), phone.getText().toString());
       } else message = "Input Inválido o Incompleto!";

        Toast.makeText(RegisterActivity.this, message,
                Toast.LENGTH_SHORT).show();
    }


}

