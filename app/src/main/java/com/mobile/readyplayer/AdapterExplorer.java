package com.mobile.readyplayer;

import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.readyplayer.ui.ItemExplorer;

import java.util.ArrayList;
import java.util.List;

public class AdapterExplorer extends RecyclerView.Adapter<AdapterExplorer.MyViewHolder> {

    private AdapterExplorerCallBack adapterExplorerCallBack;
    private Context context;
    private List<ItemExplorer> listOfFiles;
    private List<ItemExplorer> listOfCheckedFiles;
    private MyViewHolder myViewHolder;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explorer, viewGroup, false);
        myViewHolder = new MyViewHolder(view);
        listOfCheckedFiles = new ArrayList<>();
        return myViewHolder;
    }

    public AdapterExplorer(List<ItemExplorer> listOfFiles, Context context) {
        this.listOfFiles = listOfFiles;
        adapterExplorerCallBack = (AdapterExplorerCallBack) context;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final ItemExplorer itemExplorer = listOfFiles.get(i);

        myViewHolder.checkBox.setChecked(itemExplorer.isChecked());
        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    listOfCheckedFiles.add(itemExplorer);
                } else {
                    listOfCheckedFiles.remove(itemExplorer);
                }
            }
        });

        if (itemExplorer.getFileType().equals("dir")) {
            myViewHolder.imageOfFile.setImageResource(R.drawable.ic_folder);
        } else
            myViewHolder.imageOfFile.setImageResource(R.drawable.ic_music);

        myViewHolder.nameOfFile.setText(itemExplorer.getNameOfFile());
        myViewHolder.nameOfFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapterExplorerCallBack.onMethodCallBack(itemExplorer.getNameOfFile());
            }
        });

    }

    public void checkAllCheckBoxes(boolean isCheck) {

        if (isCheck) {
            for (ItemExplorer itemExplorer : listOfFiles) {
                itemExplorer.setChecked(true);
                myViewHolder.checkBox.setChecked(true);
                listOfCheckedFiles.add(itemExplorer);
                Log.d("test", "" + itemExplorer);
            }
        } else {
            for (ItemExplorer itemExplorer : listOfFiles) {
                itemExplorer.setChecked(false);
                listOfCheckedFiles.clear();
                myViewHolder.checkBox.setChecked(false);
                listOfCheckedFiles.remove(itemExplorer);
            }
        }

        Toast.makeText(context, "" + listOfCheckedFiles.size(), Toast.LENGTH_SHORT).show();
        notifyDataSetChanged();
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
