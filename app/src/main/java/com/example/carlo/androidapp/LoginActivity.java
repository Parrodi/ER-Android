package com.example.carlo.androidapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
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
import java.util.Objects;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private static final int SIGN_IN_CODE = 777;
    public String answer;

    TextView textview, registro;
    EditText em, psw;
    CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);

        em = (EditText)findViewById(R.id.editTextEmail);
        psw = (EditText)findViewById(R.id.editTextPwd);
        registro = (TextView)findViewById(R.id.textViewANR);
        textview = (TextView)findViewById(R.id.textViewOC);

        registro.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String password = object.getString("id");
                                    new AddManager().execute("http://ertourister.appspot.com/user/add", name, email, password);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(LoginActivity.this, "Failed to get parameters from FB",
                                            Toast.LENGTH_LONG).show();
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
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, "Facebook Error",
                                Toast.LENGTH_LONG).show();
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


    private class  BasicLoginManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
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
                postParams.put("email", strings[1]);
                postParams.put("password", strings[2]);

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

            textview.setText(answer);
        }

    }

    private class  AddManager extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
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
                answer = "Failed to get parameter info";
            }
            if(answer.equals("Email already in use"))
                answer = "User is registered in the database";
            textview.setText(answer);
        }

    }


    // Manages the results from FB and Google connections
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==SIGN_IN_CODE){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else callbackManager.onActivityResult(requestCode, resultCode, data);



    }

    //Manages the result of the Google connection
    private void handleSignInResult(GoogleSignInResult result) {
        if(result.isSuccess()){
           GoogleSignInAccount acct = result.getSignInAccount();
            String name = acct.getDisplayName();
            //String name = acct.getGivenName();
            //String name = acct.getFamilyName();
            String email = acct.getEmail();
            String personId = acct.getId();
            textview.setText(personId);
            //new AddManager().execute("http://ertourister.appspot.com/user/add", name, email, personId);


        }else Toast.makeText(LoginActivity.this, "Sign in failed Google",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    //Calls the method to begin a connection with FB
    public void fbLogin(View v){
        LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile"));
    }
    public void gLogin(View v){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent, SIGN_IN_CODE);

    }
    //Calls the method BasicLoginManager
    public void doBasicLogin(View v){
        new BasicLoginManager().execute("http://ertourister.appspot.com/user/login", em.getText().toString(), psw.getText().toString());

    }
}
