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
import com.mobile.readyplayer.Constants;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.ui.ItemExplorer;

import java.util.ArrayList;
import java.util.List;

public class AdapterExplorer extends RecyclerView.Adapter<AdapterExplorer.MyViewHolder>{

    private AdapterExplorerCallBack adapterExplorerCallBack;
    private List<ItemExplorer> listOfFiles;
    public ArrayList<ItemExplorer> listOfSelectedSongs;
    private ArrayList<String> listOfSelected;
    private Context context;

    public boolean isCheckAll;

    public AdapterExplorer(final List<ItemExplorer> listOfFiles, Context context) {
        adapterExplorerCallBack = (AdapterExplorerCallBack) context;
        listOfSelectedSongs = new ArrayList<>();
        listOfSelected = new ArrayList<>();
        this.listOfFiles = listOfFiles;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_explorer, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final ItemExplorer itemExplorer = listOfFiles.get(i);

        myViewHolder.nameOfFile.setText(itemExplorer.nameOfFile);
        myViewHolder.checkBox.setChecked(itemExplorer.isChecked);

        if (i == listOfFiles.size() - 1 && isCheckAll) {
            isCheckAll = false;
        }

    }

    public void checkAllCheckboxes(boolean isCheckAll) {
        if (isCheckAll){
            for (ItemExplorer itemExplorer: listOfFiles){
                itemExplorer.setChecked(true);
            }

            String mutablePath = Constants.MUTABLE_PATH.substring(0, Constants.MUTABLE_PATH.lastIndexOf('/'));
            listOfSelected.add(mutablePath);
//            listOfSelectedSongs.add(new ItemExplorer("dir", mutablePath.substring(mutablePath.lastIndexOf('/')), mutablePath));
        } else {
            for (ItemExplorer itemExplorer: listOfFiles){
                itemExplorer.setChecked(false);
            }

//            listOfSelectedSongs.clear();
            listOfSelected.clear();
        }

        Log.d("test", "-----------------");

        for (String path: listOfSelected){
            Log.d("test", path);
        }

        adapterExplorerCallBack.showFloatingActionButton(listOfSelected.isEmpty());
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
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isCheckAll) {

//                        if (isChecked)
//                            listOfSelectedSongs.add(listOfFiles.get(getAdapterPosition()));
//                        else {
//                            listOfSelectedSongs.remove(listOfFiles.get(getAdapterPosition()));
//                        }

                        if (isChecked) {
                            listOfSelected.add(listOfFiles.get(getAdapterPosition()).absolutePath);
                        }
                        else {
                            listOfSelected.remove(listOfFiles.get(getAdapterPosition()).absolutePath);
                        }

                        listOfFiles.get(getAdapterPosition()).setChecked(isChecked);

                        adapterExplorerCallBack.showFloatingActionButton(listOfSelected.isEmpty());
                    }
                }
            });

            nameOfFile = itemView.findViewById(R.id.nameOfFile);
            nameOfFile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapterExplorerCallBack.insideDirectory(listOfFiles.get(getAdapterPosition()).getNameOfFile());
                }
            });

            imageOfFile = itemView.findViewById(R.id.imageOfFile);
        }
    }
}
