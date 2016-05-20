package com.sande.soundown.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sande.soundown.Decorations.SpaceItemDecoration;
import com.sande.soundown.Adapter.UsersAdapter;
import com.sande.soundown.GsonFiles.OtherUsers;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class UserOpen extends AppCompatActivity {

    public static final String IS_FOLLOWING="IS_FOLLOWING";
    private UsersAdapter mAdapter;
    String next_href;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_open);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent caller=getIntent();
        boolean isFollowing=caller.getBooleanExtra(IS_FOLLOWING,false);
        String url;
        if(isFollowing){
            getSupportActionBar().setTitle("Following");
            url= ApiCons.FOLLOWING+new PrefsWrapper(this).getAccessToken();
        }else{
            getSupportActionBar().setTitle("Followers");
            url= ApiCons.FOLLOWER+new PrefsWrapper(this).getAccessToken();
        }
        RecyclerView recyclerView=(RecyclerView)findViewById(R.id.rv_cuo);
        GridLayoutManager mGLM=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(mGLM);
        recyclerView.addItemDecoration(new SpaceItemDecoration(20));
        mAdapter=new UsersAdapter(this);
        recyclerView.setAdapter(mAdapter);
        getUsers(url);
    }


    public void getUsers(String url) {
        JsonObjectRequest jsonReq=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson= new GsonBuilder().create();
                try {
                    JSONArray objects=response.getJSONArray("collection");
                    String next_href=response.getString("next_href");
                    OtherUsers[] users=gson.fromJson(objects.toString(),OtherUsers[].class);
                    mAdapter.addItems(new ArrayList<>(Arrays.asList(users)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(jsonReq);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }else{
            return super.onOptionsItemSelected(item);
        }
    }
}
