package com.video.downloader.thread.videodownloader.threadsvideodownloader.web;

import android.os.Handler;
import android.os.HandlerThread;

import com.video.downloader.thread.videodownloader.threadsvideodownloader.views.HomeFragment;

import java.util.ArrayDeque;
import java.util.Queue;

public class VideoDetectionInitiator {
    private Queue<VideoSearch> reservedSearches = new ArrayDeque<>();
    private Handler handler;
    private HomeFragment.ConcreteVideoContentSearch videoContentSearch;

    public VideoDetectionInitiator(HomeFragment.ConcreteVideoContentSearch videoContentSearch) {
        HandlerThread thread = new HandlerThread("Video Detect Thread");
        thread.start();
        handler = new Handler(thread.getLooper());

        this.videoContentSearch = videoContentSearch;
    }

    public void reserve(String url, String page, String title) {
        VideoSearch videoSearch = new VideoSearch();
        videoSearch.url = url;
        videoSearch.page = page;
        videoSearch.title = title;
        reservedSearches.add(videoSearch);
    }

    void initiate() {
        try {
            while (reservedSearches.size() != 0) {
                VideoSearch search = reservedSearches.remove();
                videoContentSearch.newSearch(search.url, search.page, search.title);
                handler.post(videoContentSearch);
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void clear() {
        handler.getLooper().quit();
        HandlerThread thread = new HandlerThread("Video Detect Thread");
        thread.start();
        handler = new Handler(thread.getLooper());
        reservedSearches.clear();
    }

    class VideoSearch {
        String url;
        String page;
        String title;
    }
}
