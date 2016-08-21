package com.sande.soundown.Fragments;


import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Interfaces.CallBackWelcome;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.vistrav.ask.Ask;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class Welcome2Fragment extends Fragment implements ApiCons{


    private Context context;
    private Account[] accounts;

    public Welcome2Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        context=getContext();
        View mView= inflater.inflate(R.layout.fragment_welcome2, container, false);
        (mView.findViewById(R.id.btn_authenticate)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //getPermissions();
                accounts= AccountManager.get(context).getAccountsByType("com.soundcloud.android.account");
                if(accounts.length==0){
                    getAuthFromBrowser();
                }else{
                    getAccount();
                }
            }
        });
        return mView;
    }

    private void getPermissions() {
        Ask.on(context).forPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.GET_ACCOUNTS
        ).withRationales("For saving songs to your device",
                "For accessing your Soundcloud account if exists").when(new Ask.Permission() {
            @Override
            public void granted(List<String> permissions) {
                if(permissions.size()==2) {
                    accounts= AccountManager.get(context).getAccountsByType("com.soundcloud.android.account");
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

    //these two methods fetch from accounts
    private void getAccount() {
        if(accounts.length==1){
            getAuthToken(accounts[0]);
        }else{
            String acc_names[]=new String[accounts.length];
            for(int i=0;i<accounts.length;i++){
                acc_names[i]=accounts[i].name;
            }
            AlertDialog mAlertDialog=new AlertDialog.Builder(context)
                    .setTitle("Pick Account")
                    .setAdapter(new ArrayAdapter<String>(context, android.R.layout.simple_list_item_1, acc_names), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getAuthToken(accounts[which]);
                        }
                    }).create();
            mAlertDialog.show();
        }
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
                        access = AccountManager.get(context).blockingGetAuthToken(params[0], "access_token", true);
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

    //get auth token from browser

    private void getAuthFromBrowser() {
        String makeReq=BASE_URL+CLIENT_ID_URI+CLIENT_ID+RESPONSE_TYPE+RESPONSE_TOKEN+DISPLAY_URI+DISPLAY+REDIRECT_URI+REDIRECT;
        Uri urlReq=Uri.parse(makeReq);
        Intent browser=new Intent(Intent.ACTION_VIEW,urlReq);
        startActivity(browser);
    }

    private void gotToken(String token) {
        PrefsWrapper.with(context).setAccessToken(token);
        ((CallBackWelcome)getContext()).gotAccessToken();
    }

}
