package com.sande.soundown.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sande.soundown.Interfaces.ApiCons;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.PrefsWrapper;
import com.sande.soundown.Utils.UtilsManager;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;
import mehdi.sakout.dynamicbox.DynamicBox;

/**
 * A simple {@link Fragment} subclass.
 */
public class Welcome3Fragment extends Fragment implements ApiCons {


    private TextView mTextName;
    private CircleImageView mProfPic;
    private DynamicBox mBox;

    public Welcome3Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View mView= inflater.inflate(R.layout.fragment_welcome3, container, false);
        mTextName=(TextView)mView.findViewById(R.id.fw_tv_welcome_name);
        mProfPic=(CircleImageView)mView.findViewById(R.id.fw_civ_profile);
        //mBox=new DynamicBox(getContext(),mView);
        //mBox.showLoadingLayout();
        return mView;
    }

    public void getUserID(){
        String accessToken=PrefsWrapper.with(getContext()).getAccessToken();
        String userIDurl=USER_DETAILS_ID+OAUTH_TOKEN_URI+accessToken;
        JsonObjectRequest mReq=new JsonObjectRequest(Request.Method.GET, userIDurl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //mBox.hideAll();
                try {
                    String avatar=response.getString(AVATAR);
                    avatar=avatar.replace("large","t300x300");
                    VolleySingleton.getInstance(getContext()).getImageLoader().get(avatar, new ImageLoader.ImageListener() {
                        @Override
                        public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                            mProfPic.setImageBitmap(response.getBitmap());
                        }

                        @Override
                        public void onErrorResponse(VolleyError error) {

                        }
                    });
                    String welUser="Welcome, "+response.getString(USERNAME);
                    mTextName.setText(welUser);
                    long userID=response.getLong("id");
                    PrefsWrapper.with(getContext()).setUserID(userID);

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
        VolleySingleton.getInstance(getContext()).addToRequestQueue(mReq);
    }

}
