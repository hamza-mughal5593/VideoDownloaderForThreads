package com.video.downloader.thread.videodownloader.threadsvideodownloader.views;

import static android.content.ClipDescription.MIMETYPE_TEXT_PLAIN;
import static android.content.Context.CLIPBOARD_SERVICE;
import static android.os.Environment.DIRECTORY_DCIM;

import static com.video.downloader.thread.videodownloader.threadsvideodownloader.MyApp.currentActivity_ad;
import static com.video.downloader.thread.videodownloader.threadsvideodownloader.Utils.createFileFolder;
import static com.video.downloader.thread.videodownloader.threadsvideodownloader.web.Extra.disableSSLCertificateChecking;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.video.downloader.thread.videodownloader.threadsvideodownloader.R;
import com.video.downloader.thread.videodownloader.threadsvideodownloader.Utils;
import com.video.downloader.thread.videodownloader.threadsvideodownloader.web.BrowserWebChromeClient;
import com.video.downloader.thread.videodownloader.threadsvideodownloader.web.VideoContentSearch;
import com.video.downloader.thread.videodownloader.threadsvideodownloader.web.VideoDetectionInitiator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;


public class HomeFragment extends Fragment {

    private ClipboardManager clipBoard;
    LinearLayout tv_paste, login_btn1;

    private EditText et_text;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        et_text = view.findViewById(R.id.editText);
        initViews();
        return view;    }
    private void initViews() {
        clipBoard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);


        tv_paste = view.findViewById(R.id.tv_paste);
        login_btn1 = view.findViewById(R.id.download_btn);


        login_btn1.setOnClickListener(v -> {

            String LL = et_text.getText().toString();
            if (LL.equals("")) {
                Utils.setToast(getActivity(), "Enter Url");
            } else if (!Patterns.WEB_URL.matcher(LL).matches()) {
                Utils.setToast(getActivity(), "Enter valid Url");
            } else {


                GetInstagramData();


            }


        });

        tv_paste.setOnClickListener(v -> {
            PasteText();
        });


    }

    private void PasteText() {
        try {
            et_text.setText("");
            String CopyIntent = getActivity().getIntent().getStringExtra("CopyIntent");
            if (CopyIntent == null)
                CopyIntent = "";
            if (CopyIntent.equals("")) {
                if (!(clipBoard.hasPrimaryClip())) {

                } else if (!(clipBoard.getPrimaryClipDescription().hasMimeType(MIMETYPE_TEXT_PLAIN))) {
                    if (clipBoard.getPrimaryClip().getItemAt(0).getText().toString().contains("threads.net")) {
                        et_text.setText(clipBoard.getPrimaryClip().getItemAt(0).getText().toString());
                    }

                } else {
                    ClipData.Item item = clipBoard.getPrimaryClip().getItemAt(0);
                    if (item.getText().toString().contains("threads.net")) {
                        et_text.setText(item.getText().toString());
                    }

                }
            } else {
                if (CopyIntent.contains("threads.net")) {
                    et_text.setText(CopyIntent);
                }
            }


        } catch (Exception e) {
//            Toast.makeText(getActivity(), "Exceptoin", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void GetInstagramData() {
        try {
            createFileFolder();
            URL url = new URL(et_text.getText().toString());
            String host = url.getHost();
            Log.e("initViews: ", host);
            if (host.equals("www.threads.net")) {
                formweb(et_text.getText().toString().trim());
            } else {
                Utils.setToast(getActivity(), "Enter valid URL");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private SSLSocketFactory defaultSSLSF;
    WebView page;
    String url_insta;
    private VideoDetectionInitiator videoDetectionInitiator;

    @SuppressLint("SetJavaScriptEnabled")
    private void formweb(String s) {


        Utils.showProgressDialog(getActivity(), "Fetching and downloading video");
        url_insta = s;
        defaultSSLSF = HttpsURLConnection.getDefaultSSLSocketFactory();
        videoDetectionInitiator = new VideoDetectionInitiator(new ConcreteVideoContentSearch());
        if (page == null) {
            page = view.findViewById(R.id.page);
        } else {
            View page1 = view.findViewById(R.id.page);
            ((ViewGroup) view).removeView(page1);
            ((ViewGroup) page.getParent()).removeView(page);
            ((ViewGroup) view).addView(page);
        }

        HandlerThread thread = new HandlerThread("Video Extraction Thread");
        thread.start();
        final Handler extractVideoHandler = new Handler(thread.getLooper());

        WebSettings webSettings = page.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAllowUniversalAccessFromFileURLs(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        page.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36");
        page.setWebViewClient(new WebViewClient() {//it seems not setting webclient, launches
            //default browser instead of opening the page in webview

            private VideoExtractionRunnable videoExtract = new VideoExtractionRunnable();
            private ConcreteVideoContentSearch videoSearch = new ConcreteVideoContentSearch();
            private String currentPage = page.getUrl();

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (!url.startsWith("intent")) {
                    return super.shouldOverrideUrlLoading(view, url);
                }
                return true;
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
//                    if (request.getUrl().toString().contains("youtube")) {
//                        new AlertDialog.Builder(getActivity())
//                                .setMessage("Youtube is not supported according to google policy")
//
//                                .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
////                                        getLMvdActivity().getBrowserManager().closeWindow(BrowserWindow.this);
//                                    }
//                                })
//                                .create()
//                                .show();
//                        return true;
//                    }
                if (!request.getUrl().toString().startsWith("intent")) {
                    return super.shouldOverrideUrlLoading(view, request);
                }
                return true;
            }

            @Override
            public void onPageStarted(final WebView view, final String url, Bitmap favicon) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {


//                            TextView urlBox = BrowserWindow.this.view.findViewById(R.id.urlBox);

//                            else {
//                                urlBox.setText(url);
//                                BrowserWindow.this.url = url;
//                            }

//                            TextView urlBox = BrowserWindow.this.view.findViewById(R.id.urlBox);
//                            urlBox.setText(url);
                        url_insta = url;
                    }
                });

//            loadingPageProgress.setVisibility(View.VISIBLE);
//            setupVideosFoundHUDText();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

//            loadingPageProgress.setVisibility(View.GONE);
            }

            @Override
            public void onLoadResource(final WebView view, final String url) {
                final String page = view.getUrl();
                final String title = view.getTitle();
                if (currentPage != null) {
                    if (!page.equals(currentPage)) {
                        currentPage = page;
                        videoDetectionInitiator.clear();
                    }
                }

                videoExtract.setUrl(url);
                videoExtract.setTitle(title);
                videoExtract.setPage(page);
                extractVideoHandler.post(videoExtract);
            }

            class VideoExtractionRunnable implements Runnable {
                private String url = "https://";
                private String title = "";
                private String page = "";

                public void setUrl(String url) {
                    this.url = url;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public void setPage(String page) {
                    this.page = page;
                }

                @Override
                public void run() {
                    try {
                        String urlLowerCase = url.toLowerCase();
                        Log.e("23323", "run: " + urlLowerCase);
                        String[] filters = getResources().getStringArray(R.array.videourl_filters);
                        boolean urlMightBeVideo = false;
                        for (String filter : filters) {
                            if (urlLowerCase.contains(filter)) {
                                urlMightBeVideo = true;
                                break;
                            }
                        }

                        if (urlMightBeVideo) {
                            videoSearch.newSearch(url, page, title);

//                        if (isDetecting) {
                            videoSearch.run();
//                        } else {
//                            videoDetectionInitiator.reserve(url, page, title);
//                        }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.hideProgressDialog(getActivity());
                    }
                }
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
                if (getActivity() != null) {
                    if ((url.contains("ad") || url.contains("banner") || url.contains("pop")) && isUrlAd(url)) {
                        Log.i("loremarTest", "Ads detected: " + url);
                        return new WebResourceResponse("text/javascript", "UTF-8", null);
                    }
                }
                return super.shouldInterceptRequest(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                if ((request.getUrl().toString().contains("ad") ||
                        request.getUrl().toString().contains("banner") ||
                        request.getUrl().toString().contains("pop")) && isUrlAd(request.getUrl()
                        .toString())) {
                    Log.i("loremarTest", "Ads detected: " + request.getUrl().toString());
                    return new WebResourceResponse(null, null, null);
                } else return null;
            }
        });
        page.setWebChromeClient(new BrowserWebChromeClient(currentActivity_ad, null, null));
//            page.setOnLongClickListener(this);
        page.loadUrl(url_insta);

    }

    public class ConcreteVideoContentSearch extends VideoContentSearch {

        @Override
        public void onStartInspectingURL() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
//                    if (findingVideoInProgress.getVisibility() == View.GONE) {
//                        findingVideoInProgress.setVisibility(View.VISIBLE);
//                    }
                }
            });

            disableSSLCertificateChecking();
        }

        @Override
        public void onFinishedInspectingURL(boolean finishedAll) {
            HttpsURLConnection.setDefaultSSLSocketFactory(defaultSSLSF);
            if (finishedAll) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                        findingVideoInProgress.setVisibility(View.GONE);
//                        Toast.makeText(currentActivity_ad, "onFinishedInspectingURL", Toast.LENGTH_SHORT).show();
                        Utils.hideProgressDialog(getActivity());
                    }
                });
            }
        }

        @Override
        public void onVideoFound(String size, String type, String link,
                                 String name, String page, boolean chunked,
                                 String website) {
//            videoList.addItem(size, type, link, name, page, chunked, website);
//            Toast.makeText(currentActivity_ad, "Video Found"+name, Toast.LENGTH_SHORT).show();
//            startDownload(link, RootDirectoryInsta_vid, getActivity(), page);
            if (link != null) {
                Log.e("onVideoFound", "onVideoFound: ");
                downloadfile(size, link, page, name, type);
//    Utils.showProgressDialog(getActivity(),"Downloading video");
            } else {
                Utils.hideProgressDialog(getActivity());
            }
//            updateFoundVideosBar();
        }
    }

    private static File downloadFile = null;
    private static long prevDownloaded = 0;
    private static long totalSize = 0;

    private static boolean stop = false;
    private static Thread downloadThread;
    private static String downloadFolder;

    public void downloadfile(String size, String link, String page2, String name, String type) {
        stop = false;
        downloadThread = Thread.currentThread();


        prevDownloaded = 0;
        URLConnection connection;
        try {
            totalSize = Long.parseLong(size);
            connection = (new URL(link)).openConnection();
            String filename = "name" + size + "." + type;

            File directory = prepareTargetDirectory();
            if (!directory.getAbsolutePath().endsWith("/")) {
                downloadFolder = directory.getAbsolutePath() + "/";
            } else {
                downloadFolder = directory.getAbsolutePath();
            }
            boolean directotryExists = directory.exists() || directory.mkdir() || directory
                    .createNewFile();
            if (directotryExists) {
                downloadFile = new File(directory, filename);
                if (connection != null) {
                    FileOutputStream out = null;
                    if (downloadFile.exists()) {
                        prevDownloaded = downloadFile.length();
                        connection.setRequestProperty("Range", "bytes=" + downloadFile.length
                                () + "-");
                        connection.connect();
                        Toast.makeText(currentActivity_ad, "File already downloaded", Toast.LENGTH_LONG).show();
                        downloadfinish();
                        Utils.hideProgressDialog(currentActivity_ad);
                        out = new FileOutputStream(downloadFile.getAbsolutePath(), true);
                        return;
                    } else {
                        connection.connect();
                        if (downloadFile.createNewFile()) {
                            out = new FileOutputStream(downloadFile.getAbsolutePath(), true);
                        }
                    }
                    if (out != null && downloadFile.exists()) {
                        InputStream in = connection.getInputStream();
                        ReadableByteChannel readableByteChannel = Channels.newChannel(in);
                        FileChannel fileChannel = out.getChannel();
                        while (downloadFile.length() < totalSize) {
                            if (stop) return;
                            fileChannel.transferFrom(readableByteChannel, 0, 1024);
                                /*ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
                                int read = readableByteChannel.read(buffer);
                                if (read!=-1) {
                                    buffer.flip();
                                    writableByteChannel.write(buffer);
                                }
                                else break;*/
                            if (downloadFile == null) return;
                        }
                        readableByteChannel.close();
                        in.close();
                        out.flush();
                        out.close();
                        fileChannel.close();
                        //writableByteChannel.close();
//                                downloadFinished(filename);
                        Log.e("File download", filename);
                        addImageToGallery(downloadFile.getAbsolutePath());
                        Utils.hideProgressDialog(currentActivity_ad);
                        Toast.makeText(currentActivity_ad, "Video Download successfully", Toast.LENGTH_SHORT).show();
                        downloadfinish();
                    }
                }
            } else {
                downloadfinish();
                Log.e("loremarTest", "Can't create Download directory.");
                Utils.hideProgressDialog(currentActivity_ad);

//                        onDownloadFailExceptionListener.onDownloadFailException("Can't create Download directory.");
            }
        } catch (FileNotFoundException e) {
            downloadfinish();
            Log.i("loremarTest", "link:" + link + " not found");
            Utils.hideProgressDialog(currentActivity_ad);
            Toast.makeText(currentActivity_ad, "Download failed", Toast.LENGTH_SHORT).show();
//                    linkNotFound(intent);
        }
//                catch (DownloadFailException e) {
//                    Log.e("loremarTest", e.getMessage());
//                    onDownloadFailExceptionListener.onDownloadFailException(e.getMessage());
//                }
        catch (Exception e) {
            downloadfinish();
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.e("loremarTest", sw.toString());
            Utils.hideProgressDialog(currentActivity_ad);
//                    onDownloadFailExceptionListener.onDownloadFailException(sw.toString());
        }//                    if (onDownloadFailExceptionListener != null)


    }

    public void addImageToGallery(final String filePath) {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "video/mp4");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        currentActivity_ad.getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values);
    }

    void downloadfinish() {


        if (page != null) {
            page.post(new Runnable() {
                @Override
                public void run() {
                    page.stopLoading();
                    page.removeAllViews();
//                page.destroy();

                    page = null;

                }
            });
        }
    }

    private static File prepareTargetDirectory() throws IOException {
        File downloadFolder =
                Environment.getExternalStoragePublicDirectory(DIRECTORY_DCIM + "/ThreadsDownloader");
        if (!downloadFolder.exists()) {
            downloadFolder.mkdirs();
        }
        if (
                downloadFolder
                        != null
                        && (downloadFolder.exists() || downloadFolder.mkdir() || downloadFolder.createNewFile())
                        && downloadFolder.canWrite()
        ) {
            return downloadFolder;
        }

        File externalStorage = Environment.getExternalStorageDirectory();
        String externalStorageState = Environment.getExternalStorageState();
        if (
                externalStorage
                        != null
                        && (externalStorage.exists() || externalStorage.mkdir() || externalStorage.createNewFile())
                        && externalStorage.canWrite()
                        && externalStorageState.equals(Environment.MEDIA_MOUNTED)
        ) {
            return new File(externalStorage, "Download");
        }

        File appExternal = currentActivity_ad.getExternalFilesDir(null);
        if (
                appExternal
                        != null
                        && (appExternal.exists() || appExternal.mkdir() || appExternal.createNewFile())
                        && appExternal.canWrite()
        ) {
            return new File(appExternal, "Download");
        }

        String message;
        switch (externalStorageState) {
            case Environment.MEDIA_UNMOUNTABLE:
                message = "External storage is un-mountable.";
                break;
            case Environment.MEDIA_SHARED:
                message = "USB mass storage is turned on. Can not mount external storage.";
                break;
            case Environment.MEDIA_UNMOUNTED:
                message = "External storage is not mounted.";
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                message = "External storage is mounted but has no write access.";
                break;
            case Environment.MEDIA_BAD_REMOVAL:
                message = "External storage was removed without being properly ejected.";
                break;
            case Environment.MEDIA_REMOVED:
                message = "External storage does not exist. Probably removed.";
                break;
            case Environment.MEDIA_NOFS:
                message = "External storage is blank or has unsupported filesystem.";
                break;
            case Environment.MEDIA_CHECKING:
                message = "Still checking for external storage.";
                break;
            case Environment.MEDIA_EJECTING:
                message = "External storage is currently being ejected.";
                break;
            case Environment.MEDIA_UNKNOWN:
                message = "External storage is not available for some unknown reason.";
                break;
            case Environment.MEDIA_MOUNTED:
                message = "External storage is mounted but for some unknown reason is not" +
                        " available.";
                break;
            default:
                message = "External storage is not available. No reason.";
        }
        throw new IOException(message);
    }

    public boolean isUrlAd(String url) {
        Log.i("loremarTest", "Finding ad in url: " + url);
//        boolean isAd = adblock.checkThroughFilters(url);
//        if (isAd) {
//            Log.i("loremarTest", "Detected ad: " + url);
//        }
        return false;
    }
}