package com.video.downloader.thread.videodownloader.threadsvideodownloader;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class Utils {
    public static File RootDirectoryInstaShow_img = new File(Environment.getExternalStorageDirectory() + "/DCIM/ThreadsDownloader");
    public static File RootDirectoryInstaShow_vid = new File(Environment.getExternalStorageDirectory() + "/DCIM/ThreadsDownloader");
    public static Dialog customDialog;

    public static void createFileFolder() {

        if (!RootDirectoryInstaShow_img.exists()) {
            RootDirectoryInstaShow_img.mkdirs();
        }
        if (!RootDirectoryInstaShow_vid.exists()) {
            RootDirectoryInstaShow_vid.mkdirs();
        }

    }


    public static void setToast(Context _mContext, String str) {
        Toast toast = Toast.makeText(_mContext, str, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showProgressDialog(Activity activity, String data) {
        Log.e("new_112", "showProgressDialog: "+ data);
        System.out.println("Show");
        if (customDialog != null) {
            customDialog.dismiss();
            customDialog = null;
        }
        customDialog = new Dialog(activity);
        LayoutInflater inflater = LayoutInflater.from(activity);
        View mView = inflater.inflate(R.layout.progress_dialog, null);



        customDialog.setCancelable(false);
        customDialog.setContentView(mView);
        final TextView cancel = (TextView) customDialog.findViewById(R.id.loading_data);
        cancel.setText(data);
        if (!customDialog.isShowing() && !activity.isFinishing()) {
            customDialog.show();
        }
    }

    public static void hideProgressDialog(Activity activity) {
        System.out.println("Hide");

        if (activity != null)
            if (!activity.isFinishing()) {
                if (customDialog != null) {
                    if (customDialog.isShowing())
                        customDialog.dismiss();
                }
            }

    }
}
