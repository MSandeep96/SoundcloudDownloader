package com.sande.soundown.Adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.sande.soundown.GsonFiles.OtherUsers;
import com.sande.soundown.Network.VolleySingleton;
import com.sande.soundown.R;
import com.sande.soundown.Utils.ProjectConstants;
import com.sande.soundown.activities.UserLikes;
import com.sande.soundown.activities.UserOpen;

import java.util.ArrayList;

/**
 * Created by Sandeep on 20-05-2016.
 */
public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserVH> {

    private final ImageLoader mImageLoader;
    private final Context mContext;
    LayoutInflater mInflater;
    ArrayList<OtherUsers> items=new ArrayList<>();
    public UsersAdapter(Context context) {
        mInflater=LayoutInflater.from(context);
        mContext=context;
        mImageLoader=VolleySingleton.getInstance(context).getImageLoader();
    }

    public void addItems(ArrayList<OtherUsers> mUser){
        items.addAll(mUser);
        notifyDataSetChanged();
    }

    @Override
    public UserVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View mView=mInflater.inflate(R.layout.user_items,parent,false);
        return new UserVH(mView);
    }

    @Override
    public void onBindViewHolder(UserVH holder, final int position) {
        holder.mUser.setText(items.get(position).getUsername());
        holder.mImage.setImageUrl(items.get(position).getAvatar_url(),mImageLoader);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openUser(position);
            }
        });
    }

    private void openUser(int position) {
        Intent mInte=new Intent(mContext, UserLikes.class);
        mInte.putExtra(ProjectConstants.USER_ID,items.get(position).getId());
        mContext.startActivity(mInte);
    }

    //// TODO: 21-05-2016 make prof pics hd

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class UserVH extends RecyclerView.ViewHolder{
        TextView mUser;
        NetworkImageView mImage;
        public UserVH(View itemView) {
            super(itemView);
            mUser=(TextView)itemView.findViewById(R.id.tv_ui);
            mImage=(NetworkImageView) itemView.findViewById(R.id.iv_ui);
        }
    }
}
