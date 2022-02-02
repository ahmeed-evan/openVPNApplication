package com.willdev.openvpn.fromanother;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.item.RewardPointList;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RewardPointAdapter extends RecyclerView.Adapter<RewardPointAdapter.ViewHolder> {

    private Activity activity;
    private List<RewardPointList> rewardPointLists;

    public RewardPointAdapter(Activity activity, List<RewardPointList> rewardPointLists) {
        this.activity = activity;
        this.rewardPointLists = rewardPointLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.reward_point_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        if (rewardPointLists.get(position).getTitle().equals("")) {

            Glide.with(activity).load(rewardPointLists.get(position).getStatus_thumbnail())
                    .placeholder(R.drawable.logo).into(holder.imageView);
            holder.textView_title.setText(rewardPointLists.get(position).getActivity_type());
            holder.textView_type.setText(rewardPointLists.get(position).getActivity_type());

        } else {

            Glide.with(activity).load(rewardPointLists.get(position).getStatus_thumbnail())
                    .placeholder(R.drawable.logo).into(holder.imageView);
            holder.textView_title.setText(rewardPointLists.get(position).getTitle());
            holder.textView_type.setText(rewardPointLists.get(position).getActivity_type());
        }

        holder.textView_date.setText(rewardPointLists.get(position).getDate());
        holder.textView_time.setText(rewardPointLists.get(position).getTime());
        holder.textView_point.setText(rewardPointLists.get(position).getPoints());

    }

    @Override
    public int getItemCount() {
        return rewardPointLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView imageView;
        private MaterialTextView textView_title, textView_date, textView_time, textView_type, textView_point;

        public ViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_reward_point_adapter);
            textView_title = itemView.findViewById(R.id.textView_title_reward_point_adapter);
            textView_date = itemView.findViewById(R.id.textView_date_reward_point_adapter);
            textView_time = itemView.findViewById(R.id.textView_time_reward_point_adapter);
            textView_point = itemView.findViewById(R.id.textView_point_reward_point_adapter);
            textView_type = itemView.findViewById(R.id.textView_type_reward_point_adapter);

        }
    }
}
