package com.video.downloader.thread.videodownloader.threadsvideodownloader;

public class DownloadMedia {
    private String downloadImage;
    private String downloadimage_date;
    private String downloadVideo;
    private String downloadVideo_date;

    public DownloadMedia() {
    }

    public DownloadMedia(String downloadImage, String downloadVideo) {
        this.downloadImage = downloadImage;
        this.downloadVideo = downloadVideo;
    }

    public String getDownloadVideo_date() {
        return downloadVideo_date;
    }

    public void setDownloadVideo_date(String downloadVideo_date) {
        this.downloadVideo_date = downloadVideo_date;
    }

    public String getDownloadimage_date() {
        return downloadimage_date;
    }

    public void setDownloadimage_date(String downloadimage_date) {
        this.downloadimage_date = downloadimage_date;
    }

    public String getDownloadImage() {
        return downloadImage;
    }

    public void setDownloadImage(String downloadImage) {
        this.downloadImage = downloadImage;
    }

    public String getDownloadVideo() {
        return downloadVideo;
    }

    public void setDownloadVideo(String downloadVideo) {
        this.downloadVideo = downloadVideo;
    }
}
