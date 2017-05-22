package app.paste_it.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import app.paste_it.models.ImageModel;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = ImageAdapter.class.getSimpleName();

    public List<ImageModel> getItems() {
        return items;
    }

    private List<ImageModel> items;

    public ImageAdapter() {
        items = new ArrayList<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        return new RecyclerView.ViewHolder(imageView) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView imageView = (ImageView)holder.itemView;
        String path = holder.itemView.getContext().getFilesDir().getPath()+"/"+items.get(position).getFileName();
        File file = new File(path);
        if(file.exists()){
            Picasso.with(imageView.getContext()).load(file).centerCrop().into(imageView);
        }
        else{
            imageView.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        return items==null?0:items.size();
    }

    public void addItem(ImageModel imageModel){
        items.add(imageModel);
        notifyItemInserted(getItemCount()-1);
    }
}