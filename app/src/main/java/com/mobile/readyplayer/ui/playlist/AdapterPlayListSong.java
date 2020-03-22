package com.mobile.readyplayer.ui.playlist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.readyplayer.AdapterExplorerCallBack;
import com.mobile.readyplayer.ItemSongs;
import com.mobile.readyplayer.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterPlayListSong extends RecyclerView.Adapter<AdapterPlayListSong.MyViewHolder>{

    private List<ItemSongs> listOfSongs;
    private Context context;
    private AdapterExplorerCallBack mAdapterExplorerCallBack;

    public AdapterPlayListSong(List<ItemSongs> listOfSongs, Context context) {
        this.listOfSongs = listOfSongs;
//        mAdapterExplorerCallBack = (AdapterExplorerCallBack) context;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_songs, viewGroup, false);
        MyViewHolder myViewHolder = new AdapterPlayListSong.MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tvTitle.setText(listOfSongs.get(i).getTitle());
        myViewHolder.tvArtist.setText(listOfSongs.get(i).getArtist());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                mAdapterExplorerCallBack.onMethodCallBack(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listOfSongs.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle;
        private TextView tvArtist;
        private ImageView imPlayingState;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.title);
            tvArtist = itemView.findViewById(R.id.artist);
            imPlayingState = itemView.findViewById(R.id.playingState);
        }
    }
}
