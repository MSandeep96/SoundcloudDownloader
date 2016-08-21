package com.sande.soundown.Fragments;


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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sande.soundown.Adapter.TracksAdapter;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tracks extends Fragment implements ApiCons{


    private RequestQueue mReqQueue;
    private String mUrlLikes;
    private TracksAdapter mAdapter;
    private boolean loading;
    private boolean isScrollable=true;
    private DynamicBox box;
    private static final String KEY_URL = "URL";

    public Tracks() {
        // Required empty public constructor
    }

    public static Tracks getInstance(String mUrlLikes){
        Tracks mFrag=new Tracks();
        Bundle mBundle=new Bundle();
        mBundle.putString(KEY_URL,mUrlLikes);
        mFrag.setArguments(mBundle);
        return mFrag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VolleySingleton mVolley= VolleySingleton.getInstance(getContext());
        mReqQueue=mVolley.getRequestQueue();
        if(getArguments()!=null){
            mUrlLikes=getArguments().getString(KEY_URL);
        }
    }

    private void getLikes() {
        JsonObjectRequest mLikesReq=new JsonObjectRequest(Request.Method.GET, mUrlLikes, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading=false;
                box.hideAll();
                ArrayList<TrackObject> mLikes=null;
                try {
                    if(response.has(NEXT_HREF)) {
                        mUrlLikes = response.getString(NEXT_HREF);
                    }else{
                        mAdapter.setScrollable(false);
                        isScrollable=false;
                    }
                    Gson gson=new GsonBuilder().create();
                    mLikes=new ArrayList<>(Arrays.asList(gson.fromJson(response.getJSONArray(COLLECTION).toString(),TrackObject[].class)));
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
        box.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLikes();
            }
        });
        mAdapter=new TracksAdapter(getContext());
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
