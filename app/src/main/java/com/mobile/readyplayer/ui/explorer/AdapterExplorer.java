package com.mobile.readyplayer.ui.explorer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.readyplayer.AdapterExplorerCallBack;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.ui.ItemExplorer;

import java.util.ArrayList;
import java.util.List;

public class AdapterExplorer extends RecyclerView.Adapter<AdapterExplorer.MyViewHolder> {

    private AdapterExplorerCallBack adapterExplorerCallBack;
    private Context context;
    private List<ItemExplorer> listOfFiles;
    public List<ItemExplorer> listOfCheckedFiles;
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

    public void checkAllCheckBoxes(boolean isCheck) {

        if (isCheck) {
            for (ItemExplorer itemExplorer : listOfFiles) {
                itemExplorer.setChecked(true);
                myViewHolder.checkBox.setChecked(true);
                listOfCheckedFiles.add(itemExplorer);
            }
        } else {
            for (ItemExplorer itemExplorer : listOfFiles) {
                itemExplorer.setChecked(false);
                listOfCheckedFiles.clear();
                myViewHolder.checkBox.setChecked(false);
                listOfCheckedFiles.remove(itemExplorer);
            }
        }
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
