package com.sande.soundown.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sande.soundown.Adapter.PlaylistsAdapter;
import com.sande.soundown.GsonFiles.PlaylistObject;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import mehdi.sakout.dynamicbox.DynamicBox;


public class Playlists extends Fragment implements ApiCons{

    private RequestQueue mReqQue;
    private PlaylistsAdapter mAdapter;
    private boolean loading;
    private String mPlaylistUrl;
    private boolean isScrollable;
    private DynamicBox box;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReqQue= VolleySingleton.getInstance(getContext()).getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.fragment_playlists, container, false);
        RecyclerView mRecy=(RecyclerView)mView.findViewById(R.id.rv_fp);
        box=new DynamicBox(getContext(),mRecy);
        box.showLoadingLayout();
        mAdapter=new PlaylistsAdapter(getContext());
        final LinearLayoutManager mLLM=new LinearLayoutManager(getContext());
        mRecy.setAdapter(mAdapter);
        mRecy.setLayoutManager(mLLM);
        mRecy.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(isScrollable) {
                    if (dy > 0) {
                        int visibleItemCount = mLLM.getChildCount();
                        int totalItemCount = mLLM.getItemCount();
                        int pastVisibleItems = mLLM.findFirstVisibleItemPosition();
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            if (!loading) {
                                loading = true;
                                getPlaylists();
                            }
                        }
                    }
                }
            }
        });
        getPlaylists();
        return mView;
    }

    private void getPlaylists() {
        if(mPlaylistUrl==null){
            mPlaylistUrl=USERS_PAGE+ UtilsManager.getUserID(getContext())
                    +PLAYLISTS+OAUTH_TOKEN_URI
                    +UtilsManager.getAccessToken(getContext())
                    +LINKED_PARTITION+SET_LIMIT;
        }
        JsonObjectRequest mLikesReq=new JsonObjectRequest(Request.Method.GET, mPlaylistUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                box.hideAll();
                loading=false;
                ArrayList<PlaylistObject> mPlays=null;
                try {
                    if(response.has(NEXT_HREF)) {
                        mPlaylistUrl = response.getString(NEXT_HREF);
                    }else{
                        isScrollable=false;
                        mAdapter.setScrollable(false);
                    }
                    Gson gson=new GsonBuilder().create();
                    mPlays=new ArrayList<>(Arrays.asList(gson.fromJson(response.getJSONArray(COLLECTION).toString(),PlaylistObject[].class)));
                    mAdapter.addPlaysObjects(mPlays);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                box.showInternetOffLayout();
            }
        });
        mReqQue.add(mLikesReq);
    }

}
