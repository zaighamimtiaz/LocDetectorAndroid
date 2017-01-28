package com.example.hassan.servicetest;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private Button btn1,btn2;
    private EditText userId,pass;
    private BroadcastReceiver broadcastReceiver;
    private int uId;

    @Override
    protected void onResume() {
        super.onResume();

        if (broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    String latitude = intent.getExtras().getString("latitude");
                    String longitude = intent.getExtras().getString("longitude");

                    // String url = "https://fyp-loc-detect.herokuapp.com/locations";
                    String url = "https://fyp-loc-detect.herokuapp.com/users/" + uId + "/locations";

                    JSONObject obj = new JSONObject();

                    try {
                        obj.put("latitude", latitude);
                        obj.put("longitude",longitude);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.POST, url,obj, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(),
                                    "Something went Wrong ...!!!",Toast.LENGTH_SHORT).show();
                        }
                    }
                    );
                    AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

                    // Toast.makeText(getApplicationContext(),str,Toast.LENGTH_SHORT).show();
                }
            };
        }

        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        userId = (EditText)findViewById(R.id.userId);
        pass = (EditText)findViewById(R.id.password);


        if(!runtime_permissions())
            enable_buttons();
    }

    private void enable_buttons() {

        btn1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        String emailid , password;
                        String url = "https://fyp-loc-detect.herokuapp.com/users/login";

                        emailid = userId.getText().toString();
                        password = pass.getText().toString();

                        if ( emailid.equals("") || password.equals("") ){
                            Toast.makeText(getApplicationContext(),"Fill Credentials...!!!",Toast.LENGTH_SHORT).show();
                        }

                        else {

                            JSONObject obj = new JSONObject();

                            try {
                                obj.put("emailid", emailid);
                                obj.put("password",password);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                                    Request.Method.POST, url,obj, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {

                                    if ( response.has("msg") )
                                        Toast.makeText(getApplicationContext(),"Incorrect User Id or Password...!!!",
                                                Toast.LENGTH_SHORT).show();
                                    else
                                    {
                                        try {
                                            uId = response.getInt("id");

                                            Intent i = new Intent(MainActivity.this,MyService.class);
                                            startService(i);

                                            Toast.makeText(getApplicationContext(),"service started",Toast.LENGTH_LONG).show();
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(getApplicationContext(),
                                            "Something went Wrong ...!!!", Toast.LENGTH_SHORT).show();
                                }
                            }
                            );
                            AppSingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                        }
                    }
                }
        );

        btn2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getApplicationContext(),MyService.class);
                        stopService(i);

                        Toast.makeText(getApplicationContext(),"Service stopped",Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private boolean runtime_permissions() {

        if (Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this,Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION ,
            Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return  true;
        }

        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED)
            enable_buttons();

        else
            runtime_permissions();
    }
}
