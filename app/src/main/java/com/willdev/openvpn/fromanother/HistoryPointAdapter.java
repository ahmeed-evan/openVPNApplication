package com.willdev.openvpn.fromanother;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.item.RewardPointList;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class HistoryPointAdapter extends RecyclerView.Adapter<HistoryPointAdapter.ViewHolder> {

    private Activity activity;
    private List<RewardPointList> rewardPointLists;

    public HistoryPointAdapter(Activity activity, List<RewardPointList> rewardPointLists) {
        this.activity = activity;
        this.rewardPointLists = rewardPointLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.history_point_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.textView_type.setText(rewardPointLists.get(position).getActivity_type());
        holder.textView_date.setText(rewardPointLists.get(position).getDate());
        holder.textView_point.setText(rewardPointLists.get(position).getPoints());

    }

    @Override
    public int getItemCount() {
        return rewardPointLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private MaterialTextView textView_type, textView_date, textView_point;

        public ViewHolder(View itemView) {
            super(itemView);

            textView_type = itemView.findViewById(R.id.textView_type_history_point_adapter);
            textView_date = itemView.findViewById(R.id.textView_date_history_point_adapter);
            textView_point = itemView.findViewById(R.id.textView_point_history_point_adapter);

        }
    }
}
