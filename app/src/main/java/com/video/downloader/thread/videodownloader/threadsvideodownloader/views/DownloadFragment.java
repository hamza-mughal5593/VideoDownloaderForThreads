package com.video.downloader.thread.videodownloader.threadsvideodownloader.views;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.video.downloader.thread.videodownloader.threadsvideodownloader.DownVideoAdapter;
import com.video.downloader.thread.videodownloader.threadsvideodownloader.DownloadMedia;
import com.video.downloader.thread.videodownloader.threadsvideodownloader.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class DownloadFragment extends Fragment {

    GridLayoutManager gridLayoutManager;
    private RecyclerView recycler;
    ArrayList<DownloadMedia> listOfVideos;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_download, container, false);

        recycler = view.findViewById(R.id.recycler_download);     getViewOfVideos();

        setListenerOfVideos();


        return view;
    }
    private void getViewOfVideos() {
        recycler.setNestedScrollingEnabled(true);
        recycler.setHasFixedSize(false);
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1));
    }

    private void setListenerOfVideos() {
        listOfVideos = FetchVideos();
        DownVideoAdapter adapter = new DownVideoAdapter(listOfVideos, getActivity());
        recycler.setAdapter(adapter);
    }


    private ArrayList<DownloadMedia> FetchVideos() {

        ArrayList<DownloadMedia> filenames = new ArrayList<DownloadMedia>();

        String path = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/ThreadsDownloader";
        File directory = new File(path);
        File[] files = directory.listFiles();

        if (files != null) {
            for (int i = 0; i < files.length; i++) {

                String file_name = files[i].getName();
                // you can store name to arraylist and use it later
                Date lastModDate = new Date(files[i].lastModified());

                DownloadMedia obj = new DownloadMedia();
                obj.setDownloadVideo(file_name);
                obj.setDownloadVideo_date(lastModDate.toString());

                filenames.add(obj);
            }

        }


        Collections.sort(filenames, new Comparator<DownloadMedia>() {
            public int compare(DownloadMedia o1, DownloadMedia o2) {
                if (o1.getDownloadVideo_date() == null || o2.getDownloadVideo_date() == null)
                    return 0;
                return o2.getDownloadVideo_date().compareTo(o1.getDownloadVideo_date());
            }
        });


        return filenames;
    }

    public PopupWindow displayMoreMenu() {
        PopupWindow popupWindow = new PopupWindow(requireContext());

        // Inflate your layout or dynamically add view
        LayoutInflater inflater = (LayoutInflater) requireContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.main_activity_more_layout, null);
        RelativeLayout delete = view.findViewById(R.id.delete);
        RelativeLayout share = view.findViewById(R.id.share);



        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                popupWindow.dismiss();
            }
        });
        popupWindow.setFocusable(true);
        popupWindow.setWidth(WRAP_CONTENT);
        popupWindow.setHeight(WRAP_CONTENT);
        popupWindow.setContentView(view);

        return popupWindow;
    }

}