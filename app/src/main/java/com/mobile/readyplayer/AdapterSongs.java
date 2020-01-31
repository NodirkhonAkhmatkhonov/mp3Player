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

import java.util.List;

public class AdapterSongs extends RecyclerView.Adapter<AdapterSongs.MyViewHolder> {

    private List<ItemSongs> listOfFiles;
    private AdapterCallBack mAdapterCallBack;
    public int currentSongPos = 0;
    private Context context;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_songs, viewGroup, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    public AdapterSongs(List<ItemSongs> listOfFiles, Context context) {
        this.listOfFiles = listOfFiles;
        mAdapterCallBack = (AdapterCallBack) context;
        this.context = context;
    }

    public AdapterSongs(List<ItemSongs> listOfFiles, Context context, AdapterCallBack callBack) {
        this.listOfFiles = listOfFiles;
        mAdapterCallBack = callBack;
        this.context = context;
        currentSongPos = -1;
    }

    public void changeCurrentSongPos(int currentSongPos) {
        this.currentSongPos = currentSongPos;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, int i) {
        final int position = i;
        myViewHolder.tvTitle.setText(listOfFiles.get(i).getTitle());
        myViewHolder.tvArtist.setText(listOfFiles.get(i).getArtist());

        myViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAdapterCallBack.onMethodCallBack(position);
            }
        });

        if (i == currentSongPos) {
            myViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorSelectedSong));
            myViewHolder.imPlayingState.setImageResource(R.drawable.ic_equalizer_white_24dp);
        } else {
            myViewHolder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorTransparent));
            myViewHolder.imPlayingState.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }
    }

    @Override
    public int getItemCount() {
        return listOfFiles.size();
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
