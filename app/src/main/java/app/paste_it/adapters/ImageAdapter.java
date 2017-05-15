package app.paste_it.adapters;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private static final String TAG = ImageAdapter.class.getSimpleName();

    private Context mContext;

    private List<Uri> items;

    public ImageAdapter(Context c) {
        mContext = c;
        items = new ArrayList<>();
    }

    public int getCount() {
        return items==null?0:items.size();
    }

    public Uri getItem(int position) {
        return items.get(position);
    }

    public long getItemId(int position) {
        return items.get(position).hashCode();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = (convertView == null?new ImageView(mContext):(ImageView) convertView);
        Picasso.with(mContext).load(items.get(position)).into(imageView);
        return imageView;
    }

    public void addUri(Uri uri){
        items.add(uri);
        notifyDataSetChanged();
    }
}