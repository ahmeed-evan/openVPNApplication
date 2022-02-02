package com.willdev.openvpn.fromanother;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.interfaces.OnClick;
import com.willdev.openvpn.fromanother.item.UserFollowList;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFollowAdapter extends RecyclerView.Adapter {

    private Method method;
    private Activity activity;
    private String type;
    private List<UserFollowList> userFollowLists;

    private final int VIEW_TYPE_LOADING = 0;
    private final int VIEW_TYPE_ITEM = 1;

    public UserFollowAdapter(Activity activity, List<UserFollowList> userFollowLists, String type, OnClick interstitialAdView) {
        this.activity = activity;
        this.type = type;
        method = new Method(activity, interstitialAdView);
        this.userFollowLists = userFollowLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.user_follow_adapter, parent, false);
            return new ViewHolder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = LayoutInflater.from(activity).inflate(R.layout.layout_loading_item, parent, false);
            return new ProgressViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        if (holder.getItemViewType() == VIEW_TYPE_ITEM) {

            final ViewHolder viewHolder = (ViewHolder) holder;

            if (!userFollowLists.get(position).getUser_image().equals("")) {
                Glide.with(activity).load(userFollowLists.get(position).getUser_image())
                        .placeholder(R.drawable.user_profile).into(viewHolder.circleImageView);
            } else {
                Glide.with(activity).clear(viewHolder.circleImageView);
            }

            if (userFollowLists.get(position).getIs_verified().equals("true")) {
                viewHolder.textView_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_verification, 0);
            } else {
                viewHolder.textView_userName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }

            viewHolder.textView_userName.setText(userFollowLists.get(position).getFollow_user_name());

            viewHolder.relativeLayout.setOnClickListener(v -> method.onClickData(position, "", type, "", userFollowLists.get(position).getFollow_user_id(), ""));

        }

    }

    @Override
    public int getItemCount() {
        return userFollowLists.size() + 1;
    }

    public void hideHeader() {
        ProgressViewHolder.progressBar.setVisibility(View.GONE);
    }

    private boolean isHeader(int position) {
        return position == userFollowLists.size();
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private CircleImageView circleImageView;
        private MaterialTextView textView_userName;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.rel_user_follow_adapter);
            circleImageView = itemView.findViewById(R.id.imageView_user_follow_adapter);
            textView_userName = itemView.findViewById(R.id.textView_user_follow_adapter);

        }
    }

    public static class ProgressViewHolder extends RecyclerView.ViewHolder {
        public static ProgressBar progressBar;

        public ProgressViewHolder(View v) {
            super(v);
            progressBar = v.findViewById(R.id.progressBar);
        }
    }
}
