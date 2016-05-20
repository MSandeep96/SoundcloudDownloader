package com.sande.soundown.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Interfaces.CallBackMain;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.ProjectConstants;
import com.sande.soundown.Utils.UtilsManager;
import com.sande.soundown.activities.UserOpen;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class Profile extends Fragment implements ApiCons, View.OnClickListener {


    public Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View mView = inflater.inflate(R.layout.fragment_profile, container, false);
        final CircleImageView mImageView = (CircleImageView) mView.findViewById(R.id.civ_fprof);
        final TextView mUserName = (TextView) mView.findViewById(R.id.userName_tv_fprof);
        final TextView mFolCount = (TextView) mView.findViewById(R.id.folwer_tv_fprof);
        final TextView mFolinCount = (TextView) mView.findViewById(R.id.foling_tv_fprof);
        final TextView mLikesCount = (TextView) mView.findViewById(R.id.likescnt_tv_fprof);
        final TextView mPlaysCount = (TextView) mView.findViewById(R.id.playlistcnt_tv_fprof);
        TextView mPlaceLikes = (TextView) mView.findViewById(R.id.likesplac_tv_fprof);
        TextView mPlacePlaylist = (TextView) mView.findViewById(R.id.playlistplac_tv_fprof);
        mPlaceLikes.setOnClickListener(this);
        mPlacePlaylist.setOnClickListener(this);
        mFolCount.setOnClickListener(this);
        mFolinCount.setOnClickListener(this);
        PrefsWrapper wrapper=new PrefsWrapper(getContext());
        final VolleySingleton mVolley = VolleySingleton.getInstance(getContext());
        String url = USERS_PAGE +wrapper.getUserID() + "?" + OAUTH_TOKEN_URI + wrapper.getAccessToken();
        JsonObjectRequest mReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    mUserName.setText(response.getString(USERNAME));
                    mFolCount.setText(String.valueOf("Followers\n"+response.getInt(FOLLOWERCNT)));
                    mFolinCount.setText(String.valueOf("Following\n"+response.getInt(FOLLOWINGCNT)));
                    mLikesCount.setText(String.valueOf(response.getInt(FAV_COUNT)));
                    mPlaysCount.setText(String.valueOf(response.getInt(PLAYLISTCNT)));
                    String avatar=response.getString(AVATAR);
                    avatar=avatar.replace("large","t300x300");
                    mVolley.getImageLoader().get(avatar, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            mImageView.setImageBitmap(response.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //// TODO:
            }
        });
        mVolley.addToRequestQueue(mReq);
        return mView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.likescnt_tv_fprof:
                ((CallBackMain)getContext()).setViewPager(0);
                break;
            case R.id.playlistcnt_tv_fprof:
                ((CallBackMain)getContext()).setViewPager(1);
                break;
            case R.id.foling_tv_fprof:
                callIntent(true);
                break;
            case R.id.folwer_tv_fprof:
                callIntent(false);
                break;
        }
    }

    private void callIntent(boolean i) {
        //notice
        //true for following
        //false for followers
        Intent mInte=new Intent(getContext(), UserOpen.class);
        mInte.putExtra(UserOpen.IS_FOLLOWING,i);
        startActivity(mInte);
    }
}
