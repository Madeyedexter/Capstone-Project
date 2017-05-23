package app.paste_it.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.paste_it.R;
import app.paste_it.models.Tag;
import app.paste_it.models.holders.TagHolder;

/**
 * Created by Madeyedexter on 23-05-2017.
 */

public class TagAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String TAG = TagAdapter.class.getSimpleName();

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
        notifyDataSetChanged();
    }
    private View.OnClickListener onClickListener;
    public TagAdapter(View.OnClickListener onClickListener){
        this.onClickListener=onClickListener;
    }

    private List<Tag> tags;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag,parent,false);
        return new TagHolder(view,onClickListener);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TagHolder tagHolder = (TagHolder) holder;
        tagHolder.bindData(tags.get(position));
    }

    @Override
    public int getItemCount() {
        return tags==null?0:tags.size();
    }
}
