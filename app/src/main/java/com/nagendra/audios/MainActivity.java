package com.nagendra.audios;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    TextView updateStatusTv;
    ProgressBar progressBar;
    ArrayList<String> Albums;
    ArrayList<String> AlbumLinks;

    ContextWrapper cw;
    // path to /data/data/yourapp/app_data/imageDir
    File directory;
    int count[] = {0};
    int progressStatus =0;
    String text = "";
    DatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar)findViewById(R.id.updatingProgressBar);
        updateStatusTv = (TextView)findViewById(R.id.updateStatusTv);
        cw = new ContextWrapper(getApplicationContext());
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        Albums = new ArrayList<String>();
        AlbumLinks = new ArrayList<String>();

        count[0] =0;

        try {
            databaseHelper = new DatabaseHelper(this);
        } catch (Exception e) {
            Log.d("DB create: ", e.toString());
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{
                        Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10);
            }
            return;
        }

    }

    // This is called after the this activity UI is loaded
    @Override
    protected void onStart() {
        super.onStart();
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    int i = 0;
                    while (!this.isInterrupted()) {
                        Thread.sleep(400);
                        if(i%3 == 0){
                            text = " .";
                        }else if(i%3 == 1){
                            text = " . .";
                        }else{
                            text = " . . .";
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // update TextView here!
                                updateStatusTv.setText(text);
                            }
                        });
                        i++;
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        thread.start();
        // This is handles any message sent to it
        final Handler handler=new Handler() {
            @Override
            // This is called after every theard
            public void handleMessage(Message msg) {
                if(count[0] != 0 ){
                    progressStatus += 4;
                    progressBar.setProgress(progressStatus);
                }
                count[0]++;
                // If every thread is completed then this is true.
                if (count[0] == 26) {
//                        progressBar.setProgress(100);
                    Intent i=new Intent(MainActivity.this,ResultsActivity.class);
                    i.putStringArrayListExtra("AlbumLinks",AlbumLinks);
                    startActivity(i);
                    finish();
                }
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(65);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(66);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(67);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(68);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(69);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(70);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(71);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(72);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(73);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(74);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(75);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(76);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(77);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(78);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(79);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(80);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(81);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(82);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(83);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(84);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(85);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(86);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(87);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(88);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(89);
                handler.sendEmptyMessage(0);
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                getPages(90);
                handler.sendEmptyMessage(0);
            }
        }).start();


    }

//  This gets all the page numbers available for selected character
    public void getPages(int inp){
        char selectedChar = (char) (inp);
        try{
            // Get the main page of the selected character
            Document doc= Jsoup.connect("https://www.atozmp3.co/tag/"+selectedChar).get();
            String pages;
            try{
                // This is used to get the total pages available for the selected character
                pages=doc.select("a.page-numbers").get(1).text();
                int total=Integer.parseInt(pages);
                getData(inp,total);
            } catch(Exception e){
                getData(inp,-1);
                Log.d("Internal Exception",e.toString());
            }
        } catch(Exception e){}
            Log.d("Got pages for --->",Character.toString(selectedChar));
    }

    //  This gets all the albums name starting with certain character
    public void getData(int alphabet,int pages){
        char selectedAphabet = (char) (alphabet);
        int j;
        if(pages == -1){
            pages = 1;
        }
        for(j = 1; j <= pages; j++){
            String page = Integer.toString(j);
            try{
                // This gets the document of every page
                Document pre = Jsoup.connect("https://www.atozmp3.co/tag/" + selectedAphabet + "/page/" + page).get();
                // Albums are selected with this query
                Elements albums = pre.select("article[itemprop='blogPost']");
                // This loop runs for every album available in that page
                for(Element album : albums){
                    String albumName = album.select("a").get(1).text();
                    String albumLink = album.select("a").first().attr("abs:href");
                    String albumThumbnail = album.select("a").first().select("img").attr("src");
                    albumName = albumName.replace('/','-');
                    Albums.add(albumName);
                    AlbumLinks.add(albumLink);
                    // This opens a file in the internal directory to check existence
                    File file = new File(directory,albumName+".jpg");
                    // If the file is already exist in the directory then this character albums are skipped
                    if(file.exists()){
                      j=pages+1;
                      break;
                    }
                    // Else they are stored for future purpose
                    else{
                        // This adds the album name and album link
                        databaseHelper.addAlbum(albumName, albumLink);
                        // This downloads the thumnail of the album
                        storeThumb(albumName,albumThumbnail);
                    }
//                    Log.d("Album --> ",albumName);
                }
            }catch (Exception e){
                Log.e("Pagging Exception For" + Character.toString(selectedAphabet) ,e.toString());
            }
        }

        Log.d("Quiting thread",Character.toString(selectedAphabet));

    }

// This downloads Thumbnail of the album for the use of Autocomplete Textview
    public void storeThumb(String Album,String thumb){

        // Create imageDir
        File mypath=new File(directory,Album+".jpg");
        // Creats a FileOutputStream to write the downloaded file into the storage
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            URL url= new URL(thumb);
            // This downloads the thumbnail
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            // Use the compress method on the BitMap object to write image to the OutputStream
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
