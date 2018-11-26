package com.example.carlo.androidapp.actividades;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.carlo.androidapp.R;
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

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient googleApiClient;
    private static final int SIGN_IN_CODE = 777;


    TextView textview, registro;
    EditText em, psw;
    CallbackManager callbackManager;
    Intent mintent;
    SharedPreferences pref;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

        setContentView(R.layout.activity_login);
        mintent = new Intent(LoginActivity.this,MapsActivity.class);

        pref = getSharedPreferences("user_details",MODE_PRIVATE);
        if(pref.contains("userid")&&pref.contains("token"))
            startActivity(mintent);


        em = (EditText)findViewById(R.id.editTextEmail);
        psw = (EditText)findViewById(R.id.editTextPwd);
        registro = (TextView)findViewById(R.id.textViewANR);
        textview = (TextView)findViewById(R.id.textViewOC);

        registro.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                startActivity(mintent);
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
                            addUserSocialNetworks(name,email,password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Failed to get parameters from Facebook",
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


    public void basicLogin(String email, String password) throws JSONException {
        final RequestQueue loginqueue = Volley.newRequestQueue(this);
        String url = "https://er-citytourister.appspot.com/user/login";
        JSONObject postparams = new JSONObject();
        postparams.put("email", email);
        postparams.put("password", password);
        JsonObjectRequest loginrequest = new JsonObjectRequest(Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String info = response.getString("info");
                    if(info.equals("Login success")){
                        int userid = response.getInt("id");
                        String token = response.getString("token");
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putInt("userid",userid);
                        editor.putString("token", token);
                        editor.apply();
                        startActivity(mintent);
                    }else{
                        Toast.makeText(LoginActivity.this, "Datos de inicio de sesión incorrectos",
                                Toast.LENGTH_LONG).show();
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Login", "Entro al error login");
                error.printStackTrace();
            }
        });
        loginqueue.add(loginrequest);
    }


    public void addUserSocialNetworks(String name, final String email, final String password) throws JSONException {
        RequestQueue addqueue = Volley.newRequestQueue(this);
        String url = "https://er-citytourister.appspot.com/user/add";
        JSONObject postparams = new JSONObject();
        postparams.put("name",name);
        postparams.put("email", email);
        postparams.put("password", password);
        JsonObjectRequest addrequest = new JsonObjectRequest(Request.Method.POST, url, postparams, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String info = response.getString("info");
                    switch (info) {
                        case "Email already in use":
                            basicLogin(email, password);
                            break;
                        case "New user added":
                            int userid = response.getInt("id");
                            String token = response.getString("token");
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putInt("userid",userid);
                            editor.putString("token", token);
                            editor.apply();
                            startActivity(mintent);
                            break;
                        default:
                            Toast.makeText(LoginActivity.this, "Datos de inicio de sesión incorrectos",
                                    Toast.LENGTH_LONG).show();
                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        addqueue.add(addrequest);
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
            //GoogleSignInAccount acct = result.getSignInAccount();
            //String name = acct.getDisplayName();
            //String name = acct.getGivenName();
            //String name = acct.getFamilyName();
            //String email = acct.getEmail();
            //String personId = acct.getId();
            //new AddManager().execute("http://ertourister.appspot.com/user/add", name, email, personId);
            startActivity(mintent);


        }else Toast.makeText(LoginActivity.this, "Failed sign in Google",
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
    public void doBasicLogin(View v) throws JSONException {
        basicLogin(em.getText().toString(), psw.getText().toString());

    }

}