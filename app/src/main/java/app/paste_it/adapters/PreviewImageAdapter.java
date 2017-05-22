package app.paste_it.adapters;

import android.database.DataSetObserver;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.paste_it.models.ImageModel;

/**
 * Created by Madeyedexter on 21-05-2017.
 */

public class PreviewImageAdapter extends RecyclerView.Adapter {

    private static final String TAG = PreviewImageAdapter.class.getSimpleName();

    private List<ImageModel> models;

    public PreviewImageAdapter(@NonNull  ArrayList<ImageModel> imageModels) {
        models = imageModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        return new RecyclerView.ViewHolder(imageView) {};
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView imageView = (ImageView)holder.itemView;
        String path = imageView.getContext().getFilesDir().getPath()+"/"+models.get(position).getFileName();
        File file = new File(path);
        Log.d(TAG,"File exists: "+file.exists());
        Picasso.with(imageView.getContext()).load(file).into(imageView);
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
