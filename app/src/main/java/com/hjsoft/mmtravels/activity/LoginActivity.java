package com.hjsoft.mmtravels.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hjsoft.mmtravels.R;
import com.hjsoft.mmtravels.SessionManager;
import com.hjsoft.mmtravels.model.Pojo;
import com.hjsoft.mmtravels.webservices.API;
import com.hjsoft.mmtravels.webservices.RestClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hjsoft on 27/2/17.
 */
public class LoginActivity extends AppCompatActivity {

    Button btLogin;
    EditText etUname,etPwd,etCode;
    String stUname,stPwd,stCode;
    CoordinatorLayout cLayout;
    ProgressDialog progressDialog;
    API REST_CLIENT;
    SessionManager session;
    TextView tvFpwd;
    HashMap<String, String> user;
    String uname,pwd;
    String version="1";
    TextView tvTitle;
    ImageView ivMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session=new SessionManager(getApplicationContext());

        etUname=(EditText)findViewById(R.id.al_et_uname);
        etPwd=(EditText)findViewById(R.id.al_et_pwd);
        etCode=(EditText)findViewById(R.id.al_et_code);
        btLogin=(Button)findViewById(R.id.al_bt_login);
        cLayout=(CoordinatorLayout)findViewById(R.id.al_clayout);
        tvTitle=(TextView)findViewById(R.id.al_tv_title);
        ivMap=(ImageView)findViewById(R.id.al_iv);


      //  Picasso.with(this).load("https://maps.googleapis.com/maps/api/staticmap?path=17.73730317,83.23285889|17.73988994,83.26628954|17.7388205,83.3038019|17.74742162,83.33089053|17.77844261,83.35256573|17.80755298,83.35546484&size=640x400&key=AIzaSyCIx6j-T1Yd5UuQUgnuRY04ZdoDF4xCW0E").into(ivMap);
       // String text="<font color=#616161>Pushpak</font><font color=#b71c1c> Cabs</font>";
       // tvTitle.setText(Html.fromHtml(text));

        tvFpwd=(TextView)findViewById(R.id.al_tv_pwd);

        user = session.getUserDetails();

        uname=user.get(SessionManager.KEY_NAME);
        pwd=user.get(SessionManager.KEY_PWD);

        REST_CLIENT= RestClient.get();

        if (!isTaskRoot()) {
            final Intent intent = getIntent();
            if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && Intent.ACTION_MAIN.equals(intent.getAction())) {
                Log.w("data", "Main Activity is not the root.  Finishing Main Activity instead of launching.");
                finish();
                return;
            }
        }

        if(session.isLoggedIn())
        {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Loading Duties ...");
            progressDialog.show();

            JsonObject v=new JsonObject();
            v.addProperty("login",uname);
            v.addProperty("pwd",pwd);
            v.addProperty("version",version);

            Call<Pojo>login=REST_CLIENT.validate(v);
            login.enqueue(new Callback<Pojo>() {

                Pojo loginStatus;

                @Override
                public void onResponse(Call<Pojo> call, Response<Pojo> response) {

                    if(response.isSuccessful())
                    {
                        loginStatus = response.body();
                        progressDialog.dismiss();
                        //session.createLoginSession(stUname,stPwd);
                        Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        progressDialog.dismiss();
                        String msg = response.message();

                        if(msg.equals("Old Version"))
                        {
                            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                            LayoutInflater inflater = getLayoutInflater();
                            final View dialogView = inflater.inflate(R.layout.alert_version, null);
                            dialogBuilder.setView(dialogView);

                            Button ok = (Button) dialogView.findViewById(R.id.av_bt_ok);

                            final AlertDialog alertDialog = dialogBuilder.create();
                            alertDialog.show();
                            alertDialog.setCanceledOnTouchOutside(false);
                            alertDialog.setCancelable(false);

                            ok.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    alertDialog.dismiss();
                                    finish();
                                }
                            });
                        }
                    }
                }

                @Override
                public void onFailure(Call<Pojo> call, Throwable t) {

                    progressDialog.dismiss();

                    Toast.makeText(LoginActivity.this,"No Internet Connection!",Toast.LENGTH_LONG).show();
                    finish();
                }
            });
        }

        tvFpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(LoginActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        });



        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                stUname=etUname.getText().toString().trim();
                stPwd=etPwd.getText().toString().trim();
                stCode=etCode.getText().toString().trim();

                if(stUname.length()==0)
                {
                    Snackbar snackbar = Snackbar.make(cLayout,"Enter valid Username", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(LoginActivity.this,R.color.colorPrimaryDark));
                    snackbar.show();
                }
                else if(stPwd.length()==0)
                {
                    Snackbar snackbar = Snackbar.make(cLayout,"Enter valid Password", Snackbar.LENGTH_LONG);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(LoginActivity.this,R.color.colorPrimaryDark));
                    snackbar.show();
                }
                else
                {
                    progressDialog = new ProgressDialog(LoginActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Authenticating...");
                    progressDialog.show();


                    JsonObject v=new JsonObject();
                    v.addProperty("login",stUname);
                    v.addProperty("pwd",stPwd);
                    v.addProperty("version",version);

                   System.out.println("details are"+stUname+stPwd+stCode);

                    Call<Pojo>login=REST_CLIENT.validate(v);
                    login.enqueue(new Callback<Pojo>() {

                        Pojo loginStatus;

                        @Override
                        public void onResponse(Call<Pojo> call, Response<Pojo> response) {


                            loginStatus = response.body();
                            //System.out.println("response is "+response.message()+response.code());

                            if(response.isSuccessful())
                            {

                                String msg = loginStatus.getMessage();
                                // System.out.println("msg in onREsponse in LoginActivity");
                                if (msg.equals("Exist"))
                                {
                                    progressDialog.dismiss();
                                    session.createLoginSession(stUname,stPwd);

                                    user = session.getUserDetails();
                                    uname=user.get(SessionManager.KEY_NAME);
                                    pwd=user.get(SessionManager.KEY_PWD);

                                    Intent i=new Intent(LoginActivity.this,HomeActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                            else
                            {
                                String msg = response.message();

                                if(msg.equals("Old Version"))
                                {
                                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);

                                    LayoutInflater inflater = getLayoutInflater();
                                    final View dialogView = inflater.inflate(R.layout.alert_version, null);
                                    dialogBuilder.setView(dialogView);

                                    Button ok = (Button) dialogView.findViewById(R.id.av_bt_ok);

                                    final AlertDialog alertDialog = dialogBuilder.create();
                                    alertDialog.show();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.setCancelable(false);

                                    ok.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            alertDialog.dismiss();
                                            finish();
                                        }
                                    });
                                }
                                else {
                                    progressDialog.dismiss();
                                    Snackbar snackbar = Snackbar.make(cLayout, "Invalid details entered", Snackbar.LENGTH_LONG);
                                    View sbView = snackbar.getView();
                                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                                    textView.setTextColor(Color.parseColor("#ffffff"));
                                    snackbar.getView().setBackgroundColor(ContextCompat.getColor(LoginActivity.this, R.color.colorPrimaryDark));
                                    snackbar.show();
                                    etUname.setText("");
                                    etPwd.setText("");
                                    etCode.setText("");
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Pojo> call, Throwable t) {

                            progressDialog.dismiss();
                            Snackbar snackbar = Snackbar.make(cLayout,"Error in Network Connection", Snackbar.LENGTH_LONG);
                            View sbView = snackbar.getView();
                            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                            textView.setTextColor(Color.parseColor("#ffffff"));
                            snackbar.getView().setBackgroundColor(ContextCompat.getColor(LoginActivity.this,R.color.colorPrimaryDark));
                            snackbar.show();
                        }
                    });
                }
            }
        });
    }
}

