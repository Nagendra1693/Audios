package com.nagendra.audios;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

public class AlbumAdapter extends ArrayAdapter<Album> {
    ArrayList<Album> albums, tempAlbum, suggestions;
    ContextWrapper cw;
    public AlbumAdapter(Context context, ArrayList<Album> objects) {
        super(context, android.R.layout.simple_list_item_1, objects);
        this.albums = objects;
        this.tempAlbum = new ArrayList<Album>(objects);
        this.suggestions = new ArrayList<Album>(objects);
        this.cw=new ContextWrapper(context);



    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Album album = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.album_row, parent, false);
        }
        TextView txtAlbum = (TextView) convertView.findViewById(R.id.tvAlbum);
        ImageView albumThumbnail = (ImageView) convertView.findViewById(R.id.albumThumbnail);
        if (txtAlbum != null)
            txtAlbum.setText(album.getName());
        if (albumThumbnail != null && album.getThumbnail() != ""){
            try {


                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File f=new File(directory, album.getThumbnail());
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                albumThumbnail.setImageBitmap(b);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return convertView;
    }


    @Override
    public Filter getFilter() {
        return myFilter;
    }

    Filter myFilter = new Filter() {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            Album album = (Album) resultValue;
            return album.getName();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                suggestions.clear();
                for (Album album : tempAlbum) {
                    if (album.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(album);
                    }
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            ArrayList<Album> c = (ArrayList<Album>) results.values;

            if (results != null && results.count > 0) {
                clear();
                addAll(c);
//                for (Album album : c) {
//                    add(album);
//
//                }
                notifyDataSetChanged();
            } else {
                clear();
                notifyDataSetInvalidated();
            }
        }
    };
}