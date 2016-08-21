package com.sande.soundown.activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.UtilsManager;
import com.vistrav.ask.Ask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements ApiCons{

    private Account[] accounts;
    private RequestQueue mReqQue;
    private long userID;
    private String accessToken;
    private TextView statusTV;
    private PrefsWrapper prefWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_login);
        Glide.with(this).load(R.drawable.background_login).into(((ImageView)findViewById(R.id.al_iv_background)));
        mReqQue=VolleySingleton.getInstance(this).getRequestQueue();
        statusTV=(TextView)findViewById(R.id.tv_actlogin);
        prefWrapper=new PrefsWrapper(this);

        //check if called by intent filter
        getAccessTokenFromUrl();
    }

    private void getAccessTokenFromUrl() {
        final Uri data = this.getIntent().getData();
        if(data != null && data.getScheme().equals("sociallogin") && data.getFragment() != null) {
            String accessToken = data.getFragment().replaceFirst("access_token=", "");
            int amperPos=accessToken.indexOf("&");
            accessToken=accessToken.substring(0,amperPos);
            gotToken(accessToken);
        }
    }

    public void signIn(View view) {
        //get permissions if api > 23
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if(currentapiVersion>=23) {
            getPermissions();
        }
    }

    private void getAuthFromBrowser() {
        String makeReq=BASE_URL+CLIENT_ID_URI+CLIENT_ID+RESPONSE_TYPE+RESPONSE_TOKEN+DISPLAY_URI+DISPLAY+REDIRECT_URI+REDIRECT;
        Uri urlReq=Uri.parse(makeReq);
        Intent browser=new Intent(Intent.ACTION_VIEW,urlReq);
        startActivity(browser);
    }

    private void getAccount() {
        if(accounts.length==1){
            getAuthToken(accounts[0]);
        }else{
            String acc_names[]=new String[accounts.length];
            for(int i=0;i<accounts.length;i++){
                acc_names[i]=accounts[i].name;
            }
            AlertDialog mAlertDialog=new AlertDialog.Builder(this)
                    .setTitle("Pick Account")
                    .setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, acc_names), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAuthToken(accounts[which]);
                        }
                    }).create();
            mAlertDialog.show();
        }
    }

    private void getUserID() {
        statusTV.setText("PLEASE WAIT");
        String userIDurl=USER_DETAILS_ID+OAUTH_TOKEN_URI+accessToken;
        JsonObjectRequest mReq=new JsonObjectRequest(Request.Method.GET, userIDurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    userID=response.getLong("id");
                    gotoMain();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: 28-Apr-16 set error handle auth
            }
        });
        mReqQue.add(mReq);
    }

    private void getPermissions() {
        Ask.on(this).forPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.GET_ACCOUNTS
        ).withRationales("For saving songs to your device",
                "For accessing your Soundcloud account if exists").when(new Ask.Permission() {
            @Override
            public void granted(List<String> permissions) {
                if(permissions.size()==2) {
                    accounts=AccountManager.get(LoginActivity.this).getAccountsByType("com.soundcloud.android.account");
                    if(accounts.length==0){
                        getAuthFromBrowser();
                    }else{
                        getAccount();
                    }
                }
            }

            @Override
            public void denied(List<String> permissions) {
                if(permissions.size()!=0) {
                    getPermissions();
                    //didnt get some permission
                }
            }
        }).go();
    }

    // TODO: 20-05-2016 check if this works 

    private void gotoMain() {
        //setting preferences
        prefWrapper.setUserID(userID).setAccessToken(accessToken).setIsLoggedIn(true);
        Intent inte=new Intent(getBaseContext(),MainActivity.class);
        startActivity(inte);
        finish();
    }

    private void gotToken(String token) {
        accessToken=token;
        getUserID();
    }


    public void getAuthToken(Account account){
        String access=null;
        try {
            //this method though..:-O
             access=new AsyncTask<Account, Void, String>() {
                 @Override
                protected String doInBackground(Account... params) {
                    String access = null;
                    try {
                        access = AccountManager.get(getBaseContext()).blockingGetAuthToken(params[0], "access_token", true);
                    } catch (OperationCanceledException | AuthenticatorException | IOException e) {
                        e.printStackTrace();
                    }
                    return access;
                }
            }.execute(account).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        gotToken(access);
    }

}
