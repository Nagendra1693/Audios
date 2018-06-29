package com.nagendra.audios;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.zip.Inflater;


public class AlbumActivity extends AppCompatActivity {
    String selectedAlbum,selectedAlbumLink;
    LinearLayout parent,subParent;
    ScrollView scrollView;
    TextView albumNameTV;
    ImageView albumImage;
    ProgressDialog progressDialog;
    DownloadManager downloadManager;
    ArrayList<String> allSongsHigh = new ArrayList<String>();
    ArrayList<String> allSongsLow = new ArrayList<String>();
    ArrayList<String> allSongsUndef = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        scrollView = (ScrollView)findViewById(R.id.albumScroll);
        parent = (LinearLayout)findViewById(R.id.albumParent);
//        This receives the intent
        selectedAlbum = getIntent().getStringExtra("Album Name");
        selectedAlbumLink = getIntent().getStringExtra("Album Link");

        albumNameTV = (TextView)findViewById(R.id.albumNameTV);
        albumImage=(ImageView)findViewById(R.id.albumImage);

        albumNameTV.setTextColor(Color.BLACK);

        subParent = new LinearLayout(this);
        subParent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        subParent.setOrientation(LinearLayout.VERTICAL);

        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        new PrefetchData().execute();


    }
    private class PrefetchData extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = createProgressDialog(AlbumActivity.this);
//            progressDialog = new ProgressDialog(AlbumActivity.this);
//            progressDialog.setMessage("Updating Albums");
//            progressDialog.getWindow().setGravity(Gravity.BOTTOM);
//            progressDialog.setIndeterminate(true);
//            progressDialog.setCancelable(false);

            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            albumNameTV.setText(selectedAlbum);
            showLinks(selectedAlbumLink);

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            parent.addView(subParent);
            progressDialog.dismiss();
            AlbumActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        }
    }

    public static ProgressDialog createProgressDialog(Context mContext) {
        ProgressDialog dialog = new ProgressDialog(mContext);
        try {
            dialog.show();
        } catch (WindowManager.BadTokenException e) {

        }
        dialog.setCancelable(false);
        dialog.show();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.progressdialog);
        dialog.dismiss();
        // dialog.setMessage(Message);
        return dialog;
    }

    public void showLinks(String albumUrl){
        int i;
        int index=0;
        try{
            Document doc= Jsoup.connect(albumUrl).get();
            Elements paras = doc.select("div.entry-content").select("p");

            String imageUrl = doc.select("div.entry-content").select("img.aligncenter.size-full").attr("src");

            setImage(imageUrl);

            int total = paras.size();
            int high_index = -1;
            int low_index = -1;
            int undef_index = -1;
            int start = -1;
            for(i = 0; i < total; i++){
                if(paras.get(i).select("strong").text().contains("Click Below To Download All Songs [320KBPS]")){
                    high_index=i;
                }
                if(paras.get(i).select("strong").text().contains("Click Below To Download All Songs [128KBPS]")){
                    low_index=i;
                }
                if(paras.get(i).select("strong").text().contains("Click Below To Download All Songs Zip")){
                    undef_index=i;
                }
                if(paras.get(i).select("strong").text().equals("Click Below To Download Individual Songs")){
                    start = i+1;
                    System.out.println(i);
                    break;
                }
            }
//            This is triggered if there is an option to download different qualities as zip
            if(high_index != -1){
                String highPage = paras.get(high_index).select("strong").get(1).select("a").first().attr("abs:href");
                String lowPage = paras.get(low_index).select("strong").get(1).select("a").first().attr("abs:href");

                String highLink="",lowLink="";
                try {
                    Document document1 = Jsoup.connect(highPage).get();
                    highLink = document1.select("div.content").select("a").attr("abs:href");
                    Document document2 = Jsoup.connect(lowPage).get();
                    lowLink = document2.select("div.content").select("a").attr("abs:href");
                }catch (Exception e){
                    Log.e("Error getting dwd link",e.toString());
                }
                addLinks("Download all songs",highLink,lowLink);

            }
//            This is triggered if there is no slection of quality to download zip
            if(undef_index != -1){
                String undefPage = paras.get(undef_index).select("strong").get(1).select("a").first().attr("abs:href");

                String undefLink = "";
                try {
                    Document document = Jsoup.connect(undefPage).get();
                    undefLink = document.select("div.content").select("a").attr("abs:href");
                }catch (Exception e){
                    Log.e("Error getting dwd link",e.toString());
                }

                addLinks("Download all songs",undefLink,"undef");
            }
//            This triggered if individual songs are available to download
//            This is useful only when complete album released
            if(start != -1){
                index =1;
                int j=0;
                for(j = start; j< total; j++){
//                    This is to download songs with no selection of quality
                    if(undef_index != -1){
//                        dirty means String from webpage with html code which can't be read by java functions
//                        clean means it is free from non readable characters
                        String dirty = paras.get(j).select("strong").first().select("span").text();
                        String clean = "";
                        int k=0;
                        int l = dirty.length();
                        for(k=0; k < l; k++){
                            int temp =(int) dirty.charAt(k);
                            if(temp == 32 || temp == 40 || temp == 41 || temp == 44 || temp == 46 || (temp >= 48 && temp <= 57) || (temp >= 65 && temp <= 90) || (temp >= 97 && temp <= 122)){
                                clean += (char) temp;
                            }else{
                                clean +="-";
                            }
                        }

                        String undefPage = paras.get(j).select("strong").get(1).select("a").attr("abs:href");
                        String undefLink = "";
                        try {
                            Document document = Jsoup.connect(undefPage).get();
                            undefLink = document.select("div.content").select("a").attr("abs:href");
                        }catch (Exception e){
                            Log.e("Error getting dwd link",e.toString());
                        }
                        allSongsUndef.add(undefLink);
                        addLinks(clean,undefLink,"undef");
                    }
//                    This is used to download with selection of quality
                    else{
//                        dirty means String from webpage with html code which can't be read by java functions
//                        clean means it is free from non readable characters
                        String dirty = paras.get(j).select("strong").first().select("span").text();
                        String clean = "";
                        int k=0;
                        int l = dirty.length();
                        for(k=0; k < l; k++){
                            int temp =(int) dirty.charAt(k);
                            if(temp == 32 || temp == 40 || temp == 41 || temp == 44 || (temp >= 48 && temp <= 57) || (temp >= 65 && temp <= 90) || (temp >= 97 && temp <= 122)){
                                clean += (char) temp;
                            }else{
                                clean +="-";
                            }
                        }

                        String highPage = paras.get(j).select("strong").get(1).select("a").attr("abs:href");
                        String lowPage = paras.get(j).select("strong").get(2).select("a").attr("abs:href");

                        String highLink="",lowLink="";
                        try {
                            Document document1 = Jsoup.connect(highPage).get();
                            highLink = document1.select("div.content").select("a").attr("abs:href");
                            Document document2 = Jsoup.connect(lowPage).get();
                            lowLink = document2.select("div.content").select("a").attr("abs:href");
                        }catch (Exception e){
                            Log.e("Error getting dwd link",e.toString());
                        }
                        allSongsHigh.add(highLink);
                        allSongsLow.add(lowLink);
                        addLinks(clean,highLink,lowLink);
                    }

                }

            }
//            This is triggered if individual songs are released in batches
            if(high_index == -1 && low_index == -1 && undef_index == -1 && start == -1){
                int sub_start = -1;
                int j=0;
                for(i = 0; i < total; i++){
                    if(paras.get(i).select("span").select("strong").text().contains("Cast & Crew ::")){
                        sub_start = i+1;
                        break;
                    }
                }
                for(j = sub_start; j < total-1; j++){
                    String dirty = paras.get(j).select("strong").first().select("span").text();
                    String clean = "";
                    int k=0;
                    int l = dirty.length();
                    for(k=0; k < l; k++){
                        int temp =(int) dirty.charAt(k);
                        if(temp == 32 || temp == 40 || temp == 41 || temp == 44 || (temp >= 48 && temp <= 57) || (temp >= 65 && temp <= 90) || (temp >= 97 && temp <= 122)){
                            clean += (char) temp;
                        }else{
                            clean +="-";
                        }
                    }
                    String highPage = paras.get(j).select("strong").get(1).select("a").attr("abs:href");
                    String lowPage = paras.get(j).select("strong").get(2).select("a").attr("abs:href");

                    String highLink="",lowLink="";
                    try {
                        Document document1 = Jsoup.connect(highPage).get();
                        highLink = document1.select("div.content").select("a").attr("abs:href");
                        Document document2 = Jsoup.connect(lowPage).get();
                        lowLink = document2.select("div.content").select("a").attr("abs:href");
                    }catch (Exception e){
                        Log.e("Error getting dwd link",e.toString());
                    }
                    allSongsHigh.add(highLink);
                    allSongsLow.add(lowLink);
                    addLinks(clean,highLink,lowLink);
                }
            }
            if(allSongsUndef.size() != 0 || allSongsHigh.size() != 0){
                allSongsDownload(index);
            }

        } catch(Exception e){
            System.out.println("Exception -- \n"+e.toString());
        }
    }

    public void setImage(String inp_url){
        try{
            URL url= new URL(inp_url);
            // This downloads the thumbnail
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            albumImage.setImageBitmap(bmp);
        }catch (Exception e){
            Log.e("Image download except",e.toString());
        }

    }
//   This add a view in the Album activity layout for handling downloads
    public void addLinks(String title, final String high, final String low){
//        This splits the title with "-".
//        This is because I added "-" to avoid unreadable characters when reading actual title
        String combine_name[] =title.split("-");

//        This layout holds the individual song details like song name and singers
        LinearLayout songInfoLayout = new LinearLayout(this);
        songInfoLayout.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT,5));
        songInfoLayout.setOrientation(LinearLayout.VERTICAL);

//        This checks whether the song name contails singer name or not
//        If singer(s) availabe then it is a individual song.
//        This condition invokes if there a singer . i.e. Individual song
        if(title.contains("-")){
//            This concates index and Song name
            String songName = combine_name[0] + ". " + combine_name[1];
//          Singers are initialised as unavailable as i don't there existence yet
            String singers = "unavailable";
//            If the combine_name[] containes 3rd element then it contain singer(s)
            if(combine_name.length > 2){
                singers = combine_name[2];
            }
//            Create two textviews to hold song name and singer(s)

//            This holds song name
            TextView songNameTV = new TextView(this);
//            This holds singer(s)
            TextView singersTV = new TextView(this);

            songNameTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80,1));
            singersTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 50,1));

            songNameTV.setText(songName);
            singersTV.setText("   Singer(s):"+singers);

            songNameTV.setTextColor(Color.BLACK);
            singersTV.setTextColor(Color.BLACK);

            songNameTV.setTextSize(20);

//            Add the created textviews to a linear layout (songInfoLayout)
            songInfoLayout.addView(songNameTV);
            songInfoLayout.addView(singersTV);

        }
//        This is used to download album zip files
        else{
//            Create a textview to download all songs zip file
            TextView allSongsTV = new TextView(this);

            allSongsTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 80,1));

            allSongsTV.setText(title+" (Zip)");
            allSongsTV.setTextSize(20);
            allSongsTV.setTextColor(Color.BLACK);

            songInfoLayout.addView(allSongsTV);
        }


//        This layout is created to add the download links for the repective song
        LinearLayout layout2 = new LinearLayout(this);
        layout2.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout2.setOrientation(LinearLayout.HORIZONTAL);

//        Two different dimension are required to defrentiate zips an individual songs
//        This is used for Zip links
        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, 130,1);
//        This is used for individual songs
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, 80,1);

//        Two textviews are created
//        One for 320kbps and another for 128kbps
        TextView tv2 = new TextView(this);
        TextView tv3 = new TextView(this);

//        This is used to download songs which is having only one quality to download
        if(low.equals("undef")){
//            This is triggered if it is zip file
            if(title.contains("-")){
                tv2.setLayoutParams(layoutParams1);
            }
//            This is triggered if it is individual song
            else{
                tv2.setLayoutParams(layoutParams2);
            }

            tv2.setText("download");
            tv2.setTextColor(Color.BLACK);
            tv2.setGravity(Gravity.CENTER_VERTICAL);
            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(high,"");
                }
            });
//            this adds info and download links to the linear layout
            layout2.addView(songInfoLayout);
            layout2.addView(tv2);
        }
//        If the current one is a individual song
        else{
            if(title.contains("-")){
                tv2.setLayoutParams(layoutParams1);
                tv3.setLayoutParams(layoutParams1);
            }else {
                tv2.setLayoutParams(layoutParams2);
                tv3.setLayoutParams(layoutParams2);
            }

            tv2.setText("High");
            tv3.setText("Low");

            tv2.setTextSize(16);
            tv3.setTextSize(16);

            tv2.setTextColor(Color.BLACK);
            tv3.setTextColor(Color.BLACK);

            tv2.setGravity(Gravity.CENTER_VERTICAL);
            tv3.setGravity(Gravity.CENTER_VERTICAL);

            tv2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(high,"");
                }
            });
            tv3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    downloadFile(low,"");
                }
            });


            layout2.addView(songInfoLayout);
            layout2.addView(tv2);
            layout2.addView(tv3);

        }
//        This is the final layout for each download tag
        CardView cardView = new CardView(this);

        // Set the CardView layoutParams
        CardView.LayoutParams params = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT);
        if(title.contains("-")){
            params.setMargins(10,0,10,10);
        }else {
            params.setMargins(10,0,10,40);
        }

        cardView.setLayoutParams(params);

        // Set CardView corner radius
        cardView.setRadius(15);

        // Set cardView content padding
        cardView.setContentPadding(15, 15, 15, 15);


        // Set a background color for CardView default : #FFC6D6C3
        cardView.setCardBackgroundColor(Color.parseColor("#EFE3FF"));

        cardView.addView(layout2);


        subParent.addView(cardView);
    }
//    This adds link to download all songs as original into a folder name with albumname
    public void allSongsDownload(int index){

        LinearLayout layout = new LinearLayout(this);
        layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, 80,5);
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(0, 80,1);

        TextView allSongsTV = new TextView(this);
        allSongsTV.setLayoutParams(layoutParams1);
        allSongsTV.setText("Download all songs");
        allSongsTV.setTextSize(20);
        allSongsTV.setTextColor(Color.BLACK);

        layout.addView(allSongsTV);

        if(allSongsUndef.size() != 0){
            TextView allSongsUndefTV = new TextView(this);
            allSongsUndefTV.setLayoutParams(layoutParams2);
            allSongsUndefTV.setText("download");
            allSongsUndefTV.setGravity(Gravity.CENTER_VERTICAL);
            allSongsUndefTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = 0;
                    for(i = 0; i < allSongsUndef.size(); i++){
                        downloadFile(allSongsUndef.get(i),selectedAlbum);
                    }
                }
            });

            layout.addView(allSongsUndefTV);
        }else{
            TextView allSongsHighTV = new TextView(this);
            TextView allSongsLowTV = new TextView(this);

            allSongsHighTV.setLayoutParams(layoutParams2);
            allSongsLowTV.setLayoutParams(layoutParams2);

            allSongsHighTV.setText("High");
            allSongsLowTV.setText("Low");

            allSongsHighTV.setTextSize(16);
            allSongsLowTV.setTextSize(16);

            allSongsHighTV.setTextColor(Color.BLACK);
            allSongsLowTV.setTextColor(Color.BLACK);


            allSongsHighTV.setGravity(Gravity.CENTER_VERTICAL);
            allSongsLowTV.setGravity(Gravity.CENTER_VERTICAL);

            allSongsHighTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = 0;
                    for(i = 0; i < allSongsHigh.size(); i++){
                        downloadFile(allSongsHigh.get(i),selectedAlbum);
                    }
                }
            });
            allSongsLowTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int i = 0;
                    for(i = 0; i < allSongsLow.size(); i++){
                        downloadFile(allSongsLow.get(i),selectedAlbum);
                    }
                }
            });

            layout.addView(allSongsHighTV);
            layout.addView(allSongsLowTV);
        }


        CardView cardView = new CardView(this);
        // Set the CardView layoutParams
        CardView.LayoutParams params = new CardView.LayoutParams(CardView.LayoutParams.MATCH_PARENT,CardView.LayoutParams.WRAP_CONTENT);
        params.setMargins(10,0,10,40);

        cardView.setLayoutParams(params);
        cardView.setRadius(15);
        cardView.setContentPadding(15, 15, 15, 15);
        cardView.setCardBackgroundColor(Color.parseColor("#EFE3FF"));
        cardView.addView(layout);

        subParent.addView(cardView,index);

    }
//    This handles the downloading part
    public void downloadFile(String downloadLink,String albumFolder){
        Uri inp_uri = Uri.parse(downloadLink);

        DownloadManager.Request request = new DownloadManager.Request(inp_uri);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setAllowedOverRoaming(false);
        String fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(downloadLink);
        String fileName = URLUtil.guessFileName(downloadLink, null, fileExtenstion);
        request.setTitle("Downloading " + fileName);
        request.setDescription("Downloading " + fileName);
        request.setVisibleInDownloadsUi(true);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setDestinationInExternalPublicDir("Nagendra Audios"+File.separator+albumFolder, fileName);

        downloadManager.enqueue(request);
        if(albumFolder.contentEquals("")){
            Toast.makeText(this,"Downloading " + fileName,Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Downloading " + albumFolder,Toast.LENGTH_LONG).show();
        }
    }
}
