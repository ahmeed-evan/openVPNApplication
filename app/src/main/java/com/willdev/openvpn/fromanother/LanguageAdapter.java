package com.willdev.openvpn.fromanother;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.willdev.openvpn.R;
import com.willdev.openvpn.fromanother.interfaces.LanguageIF;
import com.willdev.openvpn.fromanother.item.LanguageList;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import cn.refactor.library.SmoothCheckBox;

public class LanguageAdapter extends RecyclerView.Adapter<LanguageAdapter.ViewHolder> {

    private Activity activity;
    private LanguageIF languageIF;
    private List<LanguageList> languageLists;

    public LanguageAdapter(Activity activity, List<LanguageList> languageLists, LanguageIF languageIF) {
        this.activity = activity;
        this.languageLists = languageLists;
        this.languageIF = languageIF;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.language_adapter, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.textView.setText(languageLists.get(position).getLanguage_name());

        Glide.with(activity).load(languageLists.get(position).getLanguage_image_thumb())
                .placeholder(R.drawable.logo)
                .into(holder.imageView);

        if (languageLists.get(position).getIs_selected().equals("true")) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnCheckedChangeListener((checkBox, isChecked) -> languageIF.selectLanguage(languageLists.get(position).getLanguage_id(), "", position, isChecked));

        holder.relativeLayout.setOnClickListener(v -> {
            if (holder.checkBox.isChecked()) {
                holder.checkBox.setChecked(false);
            } else {
                holder.checkBox.setChecked(true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return languageLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private RelativeLayout relativeLayout;
        private ImageView imageView;
        private MaterialTextView textView;
        private SmoothCheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView_language_adapter);
            checkBox = itemView.findViewById(R.id.checkBox_language_adapter);
            textView = itemView.findViewById(R.id.textView_language_adapter);
            relativeLayout = itemView.findViewById(R.id.relativeLayout_language_adapter);

        }
    }
}
