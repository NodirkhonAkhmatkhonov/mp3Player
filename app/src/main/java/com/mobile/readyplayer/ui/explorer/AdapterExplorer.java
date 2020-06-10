package com.mobile.readyplayer.ui.explorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.readyplayer.AdapterExplorerCallBack;
import com.mobile.readyplayer.ItemSongs;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.ui.ItemExplorer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AdapterExplorer extends RecyclerView.Adapter<AdapterExplorer.MyViewHolder> {

    private AdapterExplorerCallBack adapterExplorerCallBack;
    private Context context;
    private List<ItemExplorer> listOfFiles;
    public Set<ItemExplorer> listOfCheckedFiles = new HashSet<>();
    private MyViewHolder myViewHolder;
    private boolean isAllCheck;


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explorer, viewGroup, false);
        myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    public AdapterExplorer(List<ItemExplorer> listOfFiles, Context context) {
        this.listOfFiles = listOfFiles;
        adapterExplorerCallBack = (AdapterExplorerCallBack) context;
        this.context = context;
    }

    public void checkAllCheckBoxes(boolean isCheck) {

        listOfCheckedFiles.clear();
        Log.d("test", "list of checked files size after clearing = " + listOfCheckedFiles.size());

        if (isCheck) {
            for (ItemExplorer itemExplorer : listOfFiles) {
                itemExplorer.setChecked(true);
                myViewHolder.checkBox.setChecked(true);
                listOfCheckedFiles.add(itemExplorer);
            }

            Log.d("test", "all button true");
        } else {
            for (ItemExplorer itemExplorer : listOfFiles) {
                itemExplorer.setChecked(false);
                myViewHolder.checkBox.setChecked(false);
//                listOfCheckedFiles.remove(itemExplorer);
            }
            Log.d("test", "all button false");
        }

        for (ItemExplorer itemExplorer: listOfCheckedFiles)
            Log.d("test", itemExplorer.getAbsolutePath());

        isAllCheck = isCheck;

        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final ItemExplorer itemExplorer = listOfFiles.get(i);
        final int position = i;

        myViewHolder.checkBox.setChecked(itemExplorer.isChecked());
        myViewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isAllCheck) {
                    if (isChecked) {
                        listOfCheckedFiles.add(itemExplorer);
                    } else {
                        listOfCheckedFiles.remove(itemExplorer);
                    }
                }

                adapterExplorerCallBack.showFloatingActionButton(listOfCheckedFiles.isEmpty());
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
                adapterExplorerCallBack.showFloatingActionButton(true);
                adapterExplorerCallBack.uncheckCheckAllButton();
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
