package com.sande.soundown.Fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sande.soundown.Adapter.LikesAdapter;
import com.sande.soundown.GsonFiles.LikesObject;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class Likes extends Fragment implements ApiCons{


    private RequestQueue mReqQueue;
    private String mUrlLikes;
    private LikesAdapter mAdapter;
    private boolean loading;
    private boolean isScrollable=true;
    private DynamicBox box;

    public Likes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VolleySingleton mVolley= VolleySingleton.getInstance(getContext());
        mReqQueue=mVolley.getRequestQueue();
    }

    private void getLikes() {
        if(mUrlLikes==null){
            long userId=UtilsManager.getUserID(getContext());
            mUrlLikes = USERS_PAGE + userId + FAVORITES +
                    OAUTH_TOKEN_URI + UtilsManager.getAccessToken(getContext()) +
                    LINKED_PARTITION + SET_LIMIT;
        }
        JsonObjectRequest mLikesReq=new JsonObjectRequest(Request.Method.GET, mUrlLikes, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading=false;
                box.hideAll();
                ArrayList<LikesObject> mLikes=null;
                try {
                    if(response.has(NEXT_HREF)) {
                        mUrlLikes = response.getString(NEXT_HREF);
                    }else{
                        mAdapter.setScrollable(false);
                        isScrollable=false;
                    }
                    Gson gson=new GsonBuilder().create();
                    mLikes=new ArrayList<>(Arrays.asList(gson.fromJson(response.getJSONArray(COLLECTION).toString(),LikesObject[].class)));
                    if(mLikes.size()<=PAGINATION_LIMIT) {
                        mAdapter.setScrollable(false);
                        isScrollable=false;
                    }
                    mAdapter.addLikesObjects(mLikes);
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
        mReqQueue.add(mLikesReq);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView= inflater.inflate(R.layout.fragment_likes, container, false);
        RecyclerView mRecy=(RecyclerView)mView.findViewById(R.id.rv_fl);
        box=new DynamicBox(getContext(),mRecy);
        box.showLoadingLayout();
        mAdapter=new LikesAdapter(getContext());
        final LinearLayoutManager mLLM=new LinearLayoutManager(getContext());
        mRecy.setLayoutManager(mLLM);
        mRecy.setAdapter(mAdapter);
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
                                getLikes();
                            }
                        }
                    }
                }
            }
        });
        getLikes();
        return mView;
    }

}
