package com.mobile.readyplayer.ui.explorer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.readyplayer.ActivityPlaylistPage;
import com.mobile.readyplayer.AdapterExplorer;
import com.mobile.readyplayer.AdapterPlaylist;
import com.mobile.readyplayer.Constants;
import com.mobile.readyplayer.MainActivity;
import com.mobile.readyplayer.R;
import com.mobile.readyplayer.base.BaseFragment;
import com.mobile.readyplayer.ui.ItemExplorer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FragmentExplorer extends BaseFragment {

    private RecyclerView recyclerViewFiles;
    private AdapterExplorer adapterExplorer;
    private List<ItemExplorer> listOfFiles;

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        recycleExplorerFiles(view);
    }

    private void recycleExplorerFiles(View view) {
        recyclerViewFiles = view.findViewById(R.id.recyclerViewFiles);
        recyclerViewFiles.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFiles.setHasFixedSize(true);

        listOfFiles = new ArrayList<>();

        makeList();

        adapterExplorer = new AdapterExplorer(listOfFiles, getContext());
        recyclerViewFiles.setAdapter(adapterExplorer);

        ///////////////////////////
        adapterExplorer.notifyDataSetChanged();
    }

    private void makeList() {
        final File dir = new File(Constants.MUTABLE_PATH);
        final File[] files = dir.listFiles();

        for (File file: files) {
            if (!file.isHidden()) {
                listOfFiles.add(new ItemExplorer(file.isDirectory(), file.getName(), file.getAbsolutePath()));
            }
        }
    }

    private static String getMimeType(String fileUrl) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(fileUrl);
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public void openSubDirectory(String nameOfSubDirectory) {

    }

    public void backDirectory(String nameOfFile) {
        listOfFiles.clear();

//        String mutablePath = Constants.MUTABLE_PATH;
//        int lastIndexOfSlash = mutablePath.lastIndexOf('/');
//        Constants.MUTABLE_PATH = mutablePath.substring(0, lastIndexOfSlash);

        Constants.MUTABLE_PATH += "/" + nameOfFile;
        makeList();
        adapterExplorer.notifyDataSetChanged();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_explorer;
    }
}