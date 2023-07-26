package com.video.downloader.thread.videodownloader.threadsvideodownloader;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;
import com.video.downloader.thread.videodownloader.threadsvideodownloader.views.DownloadFragment;
import com.video.downloader.thread.videodownloader.threadsvideodownloader.views.HomeFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView homeFrag = findViewById(R.id.home_frag);
        ImageView menuIc = findViewById(R.id.menu_ic);
        NavigationView navView = findViewById(R.id.navView);
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);
        ImageView downloadFrag = findViewById(R.id.download_frag);

        HomeFragment homeFragment = new HomeFragment();
        DownloadFragment downloadFragment = new DownloadFragment();

        // Fragment ko Activity mein set karein
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, homeFragment);
        fragmentTransaction.commit();

        homeFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, homeFragment);
                fragmentTransaction.commit();
                homeFrag.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black), PorterDuff.Mode.SRC_IN);
                downloadFrag.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.light_gray), PorterDuff.Mode.SRC_IN);
            }
        });

        downloadFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, downloadFragment);
                fragmentTransaction.commit();
                homeFrag.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.light_gray), PorterDuff.Mode.SRC_IN);
                downloadFrag.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.black), PorterDuff.Mode.SRC_IN);
            }
        });

        menuIc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerOpen(navView)) {
                    drawerLayout.closeDrawer(navView);
                } else {
                    drawerLayout.openDrawer(navView);
                }
            }
        });



        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.nav_rate) {
                    openAppRating(MainActivity.this);


                } else if (id == R.id.premium) {

                } else if (id == R.id.nav_share) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT,"I am recommending this app\nhttps://play.google.com/store/apps/details?id="+MainActivity.this.getPackageName());
                    intent.setType("text/plain");
                    startActivity(Intent.createChooser(intent, "Share To:"));


                } else if (id == R.id.privacy_policy) {
                    String url2 = "https://threadzvideodownload.blogspot.com/2023/07/privacy-policy.html";
                    Intent i2 = new Intent(Intent.ACTION_VIEW);
                    i2.setData(Uri.parse(url2));
                    if (getPackageManager().queryIntentActivities(i2, 0).size() > 0) {
                        startActivity(i2);
                    }
                } else if (id == R.id.contact) {
                    String url2 = "https://threadzvideodownload.blogspot.com/2023/07/privacy-policy.html";
                    Intent i2 = new Intent(Intent.ACTION_VIEW);
                    i2.setData(Uri.parse(url2));
                    if (getPackageManager().queryIntentActivities(i2, 0).size() > 0) {
                        startActivity(i2);
                    }
                }
                if (drawerLayout.isDrawerOpen(navView)) {
                    drawerLayout.closeDrawer(navView);
                }
                return true;
            }
        });

    }
    public void openAppRating(Context context) {
        String appId = context.getPackageName();
        Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;

        List<ResolveInfo> otherApps = context.getPackageManager().queryIntentActivities(rateIntent, 0);
        for (ResolveInfo otherApp : otherApps) {
            if (otherApp.activityInfo.applicationInfo.packageName.equals("com.android.vending")) {
                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(otherAppActivity.applicationInfo.packageName, otherAppActivity.name);
                rateIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                rateIntent.setComponent(componentName);
                context.startActivity(rateIntent);
                marketFound = true;
                break;
            }
        }

        if (!marketFound) {
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appId));
            context.startActivity(webIntent);
        }
    }
}