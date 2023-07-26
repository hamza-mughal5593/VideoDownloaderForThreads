package com.video.downloader.thread.videodownloader.threadsvideodownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DownVideoAdapter extends RecyclerView.Adapter<DownVideoAdapter.MyHolderVideo>
{
    ArrayList<DownloadMedia> list;
    private Context context;

    public DownVideoAdapter(ArrayList<DownloadMedia> list, Context context) {
        this.list = list;
        this.context = context;
    }

    String pathFolder= android.os.Environment.getExternalStorageDirectory().getAbsolutePath() +"/DCIM/ThreadsDownloader";
    @NonNull
    @Override
    public MyHolderVideo onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.download_design, parent, false);
        MyHolderVideo imageViewHolder = new MyHolderVideo(view);
        return imageViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolderVideo holder, @SuppressLint("RecyclerView") int position)
    {
        holder.main_image.setImageBitmap(createVideoThumbNail(pathFolder+"/"+list.get(position).getDownloadVideo()));

//        try {
//            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//            retriever.setDataSource(context, Uri.fromFile(new File(pathFolder+"/"+list.get(position).getDownloadVideo())));
//
//            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//            long timeInMillisec = Long.parseLong(time);
//            holder.tvVideoTime.setText(String.format("%d:%d ",
//                    TimeUnit.MILLISECONDS.toMinutes(timeInMillisec),
//                    TimeUnit.MILLISECONDS.toSeconds(timeInMillisec)-TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(timeInMillisec))));
//        }
//        catch (Exception ex)
//        {
//            ex.printStackTrace();
//        }


        String[] separated = list.get(position).getDownloadVideo().split("#");
        if (separated.length!=0){
            if (separated[0]!=null){
                holder.account_name.setText(separated[0]);
            }else
                holder.account_name.setText(list.get(position).getDownloadVideo());
        }else {
            holder.account_name.setText(list.get(position).getDownloadVideo());
        }

        holder.main_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i  = new Intent(context, VideoPlayer.class);
                i.putExtra("Link", pathFolder+"/"+list.get(position).getDownloadVideo());
                i.putExtra("Activity","falseVideo");
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyHolderVideo extends RecyclerView.ViewHolder {

        private ImageView main_image;
        private TextView account_name;

        public MyHolderVideo(@NonNull View itemView) {
            super(itemView);


            main_image = itemView.findViewById(R.id.image_view);
            account_name = itemView.findViewById(R.id.textView);



        }
    }

    public Bitmap createVideoThumbNail(String path){
        return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }
}


