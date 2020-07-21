package com.mobile.readyplayer;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterPlaylist extends RecyclerView.Adapter<AdapterPlaylist.MyViewHolder> {

    public ArrayList<String> listOfPlaylists;
    private AdapterCallBack mAdapterCallBack;
    private Context context;

    private String removablePlaylistItem = "";

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_playlists, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    public AdapterPlaylist(ArrayList<String> listOfPlaylists, Context context) {
        this.listOfPlaylists = new ArrayList<>();
        this.listOfPlaylists = listOfPlaylists;
        mAdapterCallBack = (AdapterCallBack) context;
        this.context = context;
    }

    public void notifyDataHasChanged() {
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        myViewHolder.playlistName.setText(listOfPlaylists.get(i));
        myViewHolder.playlistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallBack.changePlaylist(myViewHolder.playlistName.getText().toString());
            }
        });

        myViewHolder.ivRemovePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removablePlaylistItem = myViewHolder.playlistName.getText().toString();

                if (!removablePlaylistItem.equals("All"))
                ((MainActivity)context).openRemovePlaylistDialog();
            }
        });
    }

    public void removePlayListItem() {
        listOfPlaylists.remove(removablePlaylistItem);
        ((MainActivity)context).listOfPlaylists = listOfPlaylists;
        notifyDataHasChanged();
    }

    public String removablePlaylistName() {
        return removablePlaylistItem;
    }

    @Override
    public int getItemCount() {
        return listOfPlaylists.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView playlistName;
        private ImageView ivRemovePlaylist;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            playlistName = itemView.findViewById(R.id.tvPlaylistName);
            ivRemovePlaylist = itemView.findViewById(R.id.ivRemovePlaylist);
        }
    }
}
