package com.willdev.openvpn.fromanother;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.interfaces.OnClick;
import com.willdev.openvpn.fromanother.item.UserRMList;
import com.willdev.openvpn.fromanother.util.util.Method;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class UserRMAdapter extends RecyclerView.Adapter<UserRMAdapter.ViewHolder> {

    private Method method;
    private Activity activity;
    private String type;
    private Animation myAnim;
    private List<UserRMList> userRMLists;

    public UserRMAdapter(Activity activity, List<UserRMList> rewardPointLists, OnClick interstitialAdView, String type) {
        this.activity = activity;
        this.userRMLists = rewardPointLists;
        this.type = type;
        method = new Method(activity, interstitialAdView);
        myAnim = AnimationUtils.loadAnimation(activity, R.anim.bounce);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.user_rm_adapter, parent, false);

        return new ViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView_point.setText(activity.getResources().getString(R.string.user_point) + " " + userRMLists.get(position).getUser_points());
        holder.textView_date.setText(userRMLists.get(position).getRequest_date());
        holder.textView_price.setText(userRMLists.get(position).getRedeem_price());

        switch (userRMLists.get(position).getStatus()) {
            case "0":
                holder.textView_status.setText(activity.getResources().getString(R.string.pending));
                holder.relativeLayout_status.setBackground(activity.getResources().getDrawable(R.drawable.button_background));
                break;
            case "1":
                holder.textView_status.setText(activity.getResources().getString(R.string.approve));
                holder.relativeLayout_status.setBackground(activity.getResources().getDrawable(R.drawable.approve_bg));
                break;
            default:
                holder.textView_status.setText(activity.getResources().getString(R.string.reject));
                holder.relativeLayout_status.setBackground(activity.getResources().getDrawable(R.drawable.reject_bg));
                holder.relativeLayout_detail.setBackground(activity.getResources().getDrawable(R.drawable.reject_bg));
                break;
        }

        holder.relativeLayout_detail.setBackground(activity.getResources().getDrawable(R.drawable.approve_bg));
        Drawable buttonDrawable = holder.relativeLayout_detail.getBackground();
        buttonDrawable = DrawableCompat.wrap(buttonDrawable);
        DrawableCompat.setTint(buttonDrawable, Color.parseColor("#feb007"));
        holder.relativeLayout_detail.setBackground(buttonDrawable);


        holder.relativeLayout_status.setOnClickListener(view -> {
            if (!userRMLists.get(position).getStatus().equals("0")) {
                method.onClickData(position, activity.getResources().getString(R.string.point_status), type, "", userRMLists.get(position).getRedeem_id(), "td");
            } else {
                method.alertBox(activity.getResources().getString(R.string.payment_pending));
            }
        });

        holder.relativeLayout.setOnClickListener(v -> {
            method.onClickData(position, activity.getResources().getString(R.string.reward_point), type, "", userRMLists.get(position).getRedeem_id(), "uh");
        });

        holder.relativeLayout_detail.setOnClickListener(view -> {
            method.onClickData(position, activity.getResources().getString(R.string.reward_point), type, "", userRMLists.get(position).getRedeem_id(), "uh");
        });

    }

    @Override
    public int getItemCount() {
        return userRMLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout, relativeLayout_status, relativeLayout_detail;
        private MaterialTextView textView_point, textView_date, textView_price, textView_status;

        public ViewHolder(View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativeLayout_rm_adapter);
            relativeLayout_status = itemView.findViewById(R.id.relativeLayout_status_rm_adapter);
            relativeLayout_detail = itemView.findViewById(R.id.relativeLayout_detail_rm_adapter);
            textView_point = itemView.findViewById(R.id.textView_point_rm_adapter);
            textView_date = itemView.findViewById(R.id.textView_date_rm_adapter);
            textView_price = itemView.findViewById(R.id.textView_price_rm_adapter);
            textView_status = itemView.findViewById(R.id.textView_status_rm_adapter);

        }
    }
}
