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
import com.sande.soundown.GsonFiles.FeedObject;
import com.sande.soundown.GsonFiles.TrackObject;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class Feeds extends Fragment implements ApiCons{


    private TracksAdapter mAdapter;
    private boolean isScrollable=true;
    private boolean loading;
    private DynamicBox box;
    private RequestQueue mReqQueue;
    private String mUrlFeed;

    public Feeds() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReqQueue = VolleySingleton.getInstance(getContext()).getRequestQueue();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView= inflater.inflate(R.layout.fragment_feeds, container, false);
        RecyclerView mRecy=(RecyclerView)mView.findViewById(R.id.rv_ff);
        final LinearLayoutManager mLLM=new LinearLayoutManager(getContext());
        mRecy.setLayoutManager(mLLM);
        box=new DynamicBox(getContext(),mRecy);
        box.showLoadingLayout();
        mAdapter=new TracksAdapter(getContext());
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
                                getFeed();
                            }
                        }
                    }
                }
            }
        });
        getFeed();
        return mView;
    }

    private void getFeed() {
        final PrefsWrapper wrapper=new PrefsWrapper(getContext());
        if(mUrlFeed==null){
            mUrlFeed = GET_FEED+"&"+OAUTH_TOKEN_URI+wrapper.getAccessToken();
        }
        JsonObjectRequest mFeedReq=new JsonObjectRequest(Request.Method.GET, mUrlFeed, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loading=false;
                box.hideAll();
                ArrayList<TrackObject> mFeed=new ArrayList<>();
                try {
                    if(response.has(NEXT_HREF)) {
                        mUrlFeed = response.getString(NEXT_HREF)+"&"+OAUTH_TOKEN_URI+wrapper.getAccessToken();
                    }else{
                        mAdapter.setScrollable(false);
                        isScrollable=false;
                    }
                    Gson gson=new GsonBuilder().create();
                    List<FeedObject> mFeedObjs=Arrays.asList(gson.fromJson(response.getJSONArray(COLLECTION).toString(),FeedObject[].class));
                    if(mFeedObjs.size()==0){
                        getFeed();
                        return;
                    }
                    for(FeedObject x:mFeedObjs){
                        mFeed.add(x.getOrigin());
                    }
                    mAdapter.addLikesObjects(mFeed);
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
        mReqQueue.add(mFeedReq);
    }

}
