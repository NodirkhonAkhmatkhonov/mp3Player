package com.mobile.readyplayer;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.readyplayer.ui.ItemExplorer;

import java.util.List;

public class AdapterExplorer extends RecyclerView.Adapter<AdapterExplorer.MyViewHolder> {

    private List<ItemExplorer> listOfFiles;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explorer, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    public AdapterExplorer(List<ItemExplorer> listOfFiles) {
        this.listOfFiles = listOfFiles;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        ItemExplorer itemExplorer = listOfFiles.get(i);

        myViewHolder.nameOfFile.setText(itemExplorer.getNameOfFile());
        myViewHolder.nameOfFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        myViewHolder.checkBox.setChecked(itemExplorer.isChecked());
        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myViewHolder.checkBox.setChecked(isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfFiles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
        private TextView nameOfFile;
        private ImageView imageOfFile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            nameOfFile = itemView.findViewById(R.id.nameOfFile);
            imageOfFile = itemView.findViewById(R.id.imageOfFile);
        }

    }
}
