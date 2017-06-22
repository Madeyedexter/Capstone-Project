package app.paste_it.adapters;

import android.content.Context;
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
        //here we bind the image to the image vioew. if the image that is being bound has the same size and name as
        //the one which is already bound, we don't set the image bitmap, saving us some time.
        Context context = holder.itemView.getContext();
        String boundFileName = holder.itemView.getTag()!=null?holder.itemView.getTag().toString():null;
        long boundFileSize = Utils.getFileSize(context, boundFileName);
        //the current file that should be bound
        ImageView imageView = (ImageView) holder.itemView;
        String path = Utils.getFullPath(context, models.get(position).getFileName());
        File file = new File(path);
        if (!models.get(position).getFileName().equals(boundFileName) || boundFileSize != file.length()) {
            if (file.exists()) {
                Picasso.with(imageView.getContext()).load(file).into(imageView);
            } else if (models.get(position).getDownloadURL() != null) {
                Picasso.with(imageView.getContext()).load(models.get(position).getDownloadURL()).into(imageView);
                //we do not have the file stored locally, download the file

            } else {
                imageView.setImageBitmap(null);
            }
        }
        holder.itemView.setTag(models.get(position).getFileName());

    }

    @Override
    public int getItemCount() {
        return models.size();
    }
}
