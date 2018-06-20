package com.nagendra.audios;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

public class ResultsActivity extends AppCompatActivity {

    AutoCompleteTextView autoCompleteTextView;
    AlbumAdapter adapter = null;
    ArrayList<Album> albums = null;
    ArrayList<String> available_albums;
    ArrayList<String> albumLinks;
    ContextWrapper cw;
    // path to /data/data/yourapp/app_data/imageDir
    File directory;
    String str;

    DatabaseHelper dbuser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        // This gets the directory of internal storage
        cw = new ContextWrapper(getApplicationContext());
        directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // This is used to create custom array for Autocomplete TextView adapter
        albums = new ArrayList<>();
        // This is used to store the albums available in directory(Names are extracted from file names)
        available_albums = new ArrayList<String>();
        // This lists all the files in selected directory
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        // Gets the album name from every thumbnail available in the internal storage
        for (int i = 0; i < files.length; i++)
        {
            str = files[i].getName().substring(0, files[i].getName().length() - 4);
            available_albums.add(str);
        }

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator + "Nagendra Audios");

        if (!folder.exists()) {
            folder.mkdirs();
        }
//        albums=(ArrayList<String>)getIntent().getSerializableExtra("Albums");
        albumLinks=(ArrayList<String>)getIntent().getSerializableExtra("AlbumLinks");
        // Creates a custom arrayList and add it to the album arraylist for passing as adapter
        albums = populateAlbumData(albums);
        // This gets the handle of the autocomplete textview declared in the design
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        // This creates an adapter using the available albums
        adapter = new AlbumAdapter(this, albums);
        // Custom adapter is set to the autocomplete textview
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Album album = (Album) parent.getItemAtPosition(position);
                String selectedAlbum = (String) album.getName();
                Cursor cursor = dbuser.fetchAlbum(selectedAlbum);
                String selectedAlbumLink = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.KEY_LINK));
//                Toast.makeText(ResultsActivity.this, selectedAlbumLink,Toast.LENGTH_LONG).show();
                Intent i=new Intent(ResultsActivity.this,AlbumActivity.class);
                i.putExtra("Album Name",selectedAlbum);
                i.putExtra("Album Link",selectedAlbumLink);
                startActivity(i);
            }
        });


        try {
            dbuser = new DatabaseHelper(this);
        } catch (Exception e) {
            Log.d("DB create: ", e.toString());
        }
        getLatestAlbums();

    }
    private ArrayList<Album> populateAlbumData(ArrayList<Album> albums) {
        int i=0;
        for(i=0; i<available_albums.size()-1;i++){
            // Get the albums names and thumbnails paths and add to the arraylist
            albums.add(new Album(available_albums.get(i), available_albums.get(i)+".jpg"));
        }

        return albums;
    }
    public void getLatestAlbums(){
        int i=0;
        for(i = 0; i < 8; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    setImage(finalI);
                }
            }).start();
        }
    }
    public void setImage(int i){
        try {
            ImageButton imageView = null;
            switch (i){
                case 0:
                    imageView = (ImageButton)findViewById(R.id.latestImage1);
                    break;
                case 1:
                    imageView = (ImageButton)findViewById(R.id.latestImage2);
                    break;
                case 2:
                    imageView = (ImageButton)findViewById(R.id.latestImage3);
                    break;
                case 3:
                    imageView = (ImageButton)findViewById(R.id.latestImage4);
                    break;
                case 4:
                    imageView = (ImageButton)findViewById(R.id.latestImage5);
                    break;
                case 5:
                    imageView = (ImageButton)findViewById(R.id.latestImage6);
                    break;
                case 6:
                    imageView = (ImageButton)findViewById(R.id.latestImage7);
                    break;
                case 7:
                    imageView = (ImageButton)findViewById(R.id.latestImage8);
                    break;

            }
            Document document = Jsoup.connect("https://www.atozmp3.co").get();

            Element element = document.select("div#primary").select("li").get(i);

            final String albumLink = element.select("a").first().attr("abs:href");
            final String albumName = element.select("h2").text();
//            String imageLink = element.select("img").attr("src");

            Document doc= Jsoup.connect(albumLink).get();
            Elements paras = doc.select("div.entry-content");

            final String imageLink = paras.select("img.aligncenter.size-full").attr("src");



            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(ResultsActivity.this,AlbumActivity.class);
                    i.putExtra("Album Name",albumName);
                    i.putExtra("Album Link",albumLink);
                    startActivity(i);
                }
            });
            URL url = new URL(imageLink);
//                // This downloads the thumbnail
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
        }catch (Exception e){
            Log.e("Exception with images",e.toString());
        }

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder change=new AlertDialog.Builder(ResultsActivity.this);
        change.setTitle("You want to leave?");

        change.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        change.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog show=change.create();
        show.show();
    }
}

