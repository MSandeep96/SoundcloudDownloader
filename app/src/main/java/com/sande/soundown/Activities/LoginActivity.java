package com.sande.soundown.Activities;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.UtilsManager;
import com.thefinestartist.finestwebview.FinestWebView;
import com.thefinestartist.finestwebview.FinestWebViewActivity;
import com.thefinestartist.finestwebview.listeners.WebViewListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class LoginActivity extends AppCompatActivity implements ApiCons{

    private Account[] accounts;
    private RequestQueue mReqQue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        VolleySingleton mSingleton=VolleySingleton.getInstance(this);
        mReqQue=mSingleton.getRequestQueue();
    }

    public void signIn(View view) {
        accounts=AccountManager.get(this).getAccountsByType("com.soundcloud.android.account");
        if(accounts.length==0){
            openWebView();
        }else{
            getAccount();
        }

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

    private void openWebView() {
        String makeReq=BASE_URL+CLIENT_ID_URI+CLIENT_ID+RESPONSE_TYPE+RESPONSE_TOKEN+DISPLAY_URI+DISPLAY+REDIRECT_URI+REDIRECT;
        new FinestWebView.Builder(this).webViewJavaScriptEnabled(true)
                .setWebViewListener(new WebViewListener() {
                    @Override
                    public void onPageStarted(String url) {
                        super.onPageStarted(url);
                        if(url.startsWith(REDIRECT)){
                            String token=
                                    url=url.replace(REDIRECTED_URL,"");
                            int pos=token.indexOf('&');
                            token=url.substring(0,pos);
                            gotToken(token);
                        }
                    }
                })

                .backPressToClose(true)
                .webViewJavaScriptCanOpenWindowsAutomatically(true)
                .show(makeReq);
    }

    private void getUserID(final String token) {
        String userIDurl=USER_DETAILS_ID+OAUTH_TOKEN_URI+token;
        JsonObjectRequest mReq=new JsonObjectRequest(Request.Method.GET, userIDurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    long userID=response.getLong("id");
                    UtilsManager.setUserID(getBaseContext(),userID);
                    UtilsManager.setAccessToken(getBaseContext(),token);
                    UtilsManager.setIsLoggedIn(getBaseContext(),true);
                    Intent inte=new Intent(getBaseContext(),MainActivity.class);
                    startActivity(inte);
                    finish();
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

    private void gotToken(String token) {
        getUserID(token);
    }


    public void getAuthToken(Account account){
        String access=null;
        try {
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
