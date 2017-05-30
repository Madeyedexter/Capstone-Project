package app.paste_it.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.paste_it.R;
import app.paste_it.Utils;
import app.paste_it.models.ImageModel;

/**
 * Created by Madeyedexter on 21-05-2017.
 */

public class PreviewImageAdapter extends RecyclerView.Adapter {

    private static final String TAG = PreviewImageAdapter.class.getSimpleName();

    private List<ImageModel> models;

    public PreviewImageAdapter(@NonNull ArrayList<ImageModel> imageModels) {
        models = imageModels;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_preview, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ImageView imageView = (ImageView) holder.itemView;
        String path = Utils.getFullPath(imageView.getContext(), models.get(position).getFileName());
        File file = new File(path);
        if (file.exists()) {
            Picasso.with(imageView.getContext()).load(file).into(imageView);
        } else if(models.get(position).getDownloadURL()!=null){
            Picasso.with(imageView.getContext()).load(models.get(position).getDownloadURL()).into(imageView);
        } else{
            imageView.setImageBitmap(null);
        }
    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
