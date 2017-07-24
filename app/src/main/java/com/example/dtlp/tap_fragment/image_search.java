package com.example.dtlp.tap_fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.LruCache;
import android.widget.TextView;

import com.example.dtlp.R;
import com.example.dtlp.tap_fragment.fragment_push_image.utility.DiskLruCache;
import com.example.dtlp.tap_fragment.fragment_push_image.utility.DiskLruCacheUtil;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 阳瑞 on 2017/5/30.
 */
public class image_search extends Activity {
    public static String Label;
    String ID;
    private String imageurl;
    PictureTagLayout pictureTagLayout;
    private TextView textView;


    ////////////////
    private DiskLruCacheUtil mDiskLruCacheUtil;
    private DiskLruCache mDiskLruCache;
    private LruCache<String, Bitmap> mLruCache;
    ////////////////////

    Handler handle = new Handler()
    {
        @Override
        public void handleMessage(Message msg) {

            Bitmap bitmap = (Bitmap) msg.obj;
            Drawable drawable =new BitmapDrawable(bitmap);
//            Drawable drawable =new BitmapDrawable();
            pictureTagLayout.setBackground(drawable);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_main_image_dothelable);

        pictureTagLayout = (PictureTagLayout) findViewById(R.id.tupian);
        textView = (TextView) findViewById(R.id.tvPictureTagLabel);


        Label = "";
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();//.getExtras()得到intent所附带的额外数据
        final String image=bundle.getString("image");
        ID=bundle.getString("ID");
        imageurl = image;
        Log.i("BBBBaaa", " ima = " + image);
        Log.i("BBBBaaa", " ID = " + ID);


//        Thread mThread = new Thread() {
//            @Override
//            public void run() {
//                Message msg = new Message();
//                Bitmap bmp = getURLimage(image);
//                msg.obj = bmp;
//                System.out.println("000");
////                        }
//                handle.sendMessage(msg); //新建线程加载图片信息，发送到消息队列中
//            }
//        };
//        mThread.start();
//        Log.i("DDDDDDD", "textImage = " + Label);
        ////////////////
        mDiskLruCacheUtil = new DiskLruCacheUtil(image_search.this);
        mDiskLruCache = mDiskLruCacheUtil.doOpen();
        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 8;
        mLruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        ///////////////

        ///////////////////////////

        if (pictureTagLayout == null || image == null) {
            return;
        }
        // 从内存中获取数据
        Bitmap bitmap = mLruCache.get(image);
        // 如果内存中有数据
        if (bitmap != null) {
            Drawable drawable =new BitmapDrawable(bitmap);
            pictureTagLayout.setBackground(drawable);
            return;
        }
        // 从缓存文件中获取数据DiskUrlCache
        bitmap = mDiskLruCacheUtil.doGet(image);
        if (bitmap != null) {
            Log.i("info", "bitmap = " + bitmap);
            Drawable drawable =new BitmapDrawable(bitmap);
            pictureTagLayout.setBackground(drawable);
            if (mLruCache.get(image) == null) {
                // 把缓存文件中的数据加入内存中
                mLruCache.put(image, bitmap);
            }
            return;
        }
        ///////////////////////////

    }

    public Bitmap getURLimage(String url) {
        Bitmap bmp = null;
        try {
            URL myurl = new URL(url);
            // 获得连接
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setConnectTimeout(6000);//设置超时
            conn.setDoInput(true);
            conn.setUseCaches(false);//不缓存
            conn.connect();
            InputStream is = conn.getInputStream();//获得图片的数据流
            bmp = BitmapFactory.decodeStream(is);
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    @Override
    public void onBackPressed() {
        Intent mIntent = new Intent();
        mIntent.putExtra("a",Label);
        mIntent.putExtra("b",ID);
        mIntent.putExtra("c",imageurl);
//        mIntent.putExtra("change02", "2000");
        // 设置结果，并进行传送
        this.setResult(0, mIntent);
        super.onBackPressed();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == KeyEvent.KEYCODE_BACK){
//            moveTaskToBack(true);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
