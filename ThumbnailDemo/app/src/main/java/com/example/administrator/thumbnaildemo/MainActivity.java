package com.example.administrator.thumbnaildemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.tv)
    TextView tv;
    @BindView(R.id.img)
    ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        requestAllPower();
        img.setDrawingCacheEnabled(true);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<Video> list = getVideos();
                if(list.size()>0){
                    thumbnail(list.get(0));//获取缩略图
                }else{
                    Toast.makeText(MainActivity.this,"暂无视频",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
            //这里我们拿到回掉回来的bitmap，可以加载到我们想使用到的地方
                img.setImageBitmap(bitmap);
                savePhoto(bitmap,"videoScreen");
        }
    };
    private void thumbnail(Video video) {
        tv.setVisibility(View.GONE);
        img.setVisibility(View.VISIBLE);
        Glide.with(this).load(video.getUrl1()).asBitmap().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).into(target);
    }
    public  String savePhoto(Bitmap photoBitmap,String photoName) {
        String path = "";
        String localPath = null;
        path  = Environment.getExternalStorageDirectory()+"/"+photoName+".png";
        File photoFile = new File(path);
        if (photoFile.exists()){
            photoFile.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(photoFile);
            if (photoBitmap != null) {
                if (photoBitmap.compress(Bitmap.CompressFormat.PNG, 100,
                        fileOutputStream)) {
                    localPath = photoFile.getPath();
                    fileOutputStream.flush();
                    Toast.makeText(MainActivity.this,"图片成功保存至:"+localPath,Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(MainActivity.this,"Bitmap为空",Toast.LENGTH_SHORT).show();
            }
        } catch (FileNotFoundException e) {
            photoFile.delete();
            localPath = null;
            e.printStackTrace();
            Log.e("saveerror","FileNotFound");
        } catch (IOException e) {
            photoFile.delete();
            localPath = null;
            Log.e("saveerror",e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                    fileOutputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return localPath;
    }

    public void requestAllPower() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS}, 1);
            }
        }
    }
    public List<Video> getVideos() {
        List<Video> list = null;
        if (this != null) {
            Cursor cursor = getContentResolver().query(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
                    null, null);
            if (cursor != null) {
                list = new ArrayList<Video>();
                while (cursor.moveToNext()) {
                    int id = cursor.getInt(cursor
                            .getColumnIndexOrThrow(MediaStore.Video.Media._ID));
                    String title = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                    String album = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ALBUM));
                    String artist = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.ARTIST));
                    String displayName = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME));
                    String mimeType = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                    String path = cursor
                            .getString(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                    long duration = cursor
                            .getInt(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
                    long size = cursor
                            .getLong(cursor
                                    .getColumnIndexOrThrow(MediaStore.Video.Media.SIZE));
                    Video video = new Video();
                    video.setName1(title);
                    video.setSize1(size);
                    video.setUrl1(path);
                    video.setDuration1(duration);
                    list.add(video);
                }
                cursor.close();
            }
        }
        return list;
    }
}
