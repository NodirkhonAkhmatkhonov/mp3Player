package com.mobile.readyplayer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.readyplayer.ui.ItemPlaylist;

import org.w3c.dom.Text;

import java.util.List;

public class AdapterPlaylist extends RecyclerView.Adapter<AdapterPlaylist.MyViewHolder> {

    public List<ItemPlaylist> listOfPlaylists;
    private AdapterCallBack mAdapterCallBack;
    private Context context;

    private int removableItemIndex = -1;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_playlists, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    public AdapterPlaylist(List<ItemPlaylist> listOfPlaylists, Context context) {
        this.listOfPlaylists = listOfPlaylists;
        mAdapterCallBack = (AdapterCallBack) context;
        this.context = context;
    }

    public void notifyDataHasChanged() {
        notifyDataSetChanged();
    }
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final int index = i;
        myViewHolder.playlistName.setText(listOfPlaylists.get(i).getName());
        myViewHolder.playlistName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).openActivityPlaylistPage();
            }
        });

        myViewHolder.ivRemovePlaylist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removableItemIndex = index;
                ((MainActivity)context).openRemovePlaylistDialog();
            }
        });
    }

    public void removePlayListItem() {
        listOfPlaylists.remove(removableItemIndex);
        ((MainActivity)context).listOfPlaylists = listOfPlaylists;
        notifyDataHasChanged();
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
