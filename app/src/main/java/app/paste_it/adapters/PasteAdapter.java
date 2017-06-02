package app.paste_it.adapters;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.paste_it.R;
import app.paste_it.models.Paste;
import app.paste_it.models.holders.LoadingHolder;
import app.paste_it.models.holders.PasteHolder;
import app.paste_it.models.holders.TextHolder;

/**
 * Created by Madeyedexter on 16-05-2017.
 */

public class PasteAdapter extends SelectableAdapter {
    public static final int ITEM_TYPE_DATA = 0;
    public static final int ITEM_TYPE_LOADING = 1;
    public static final int ITEM_TYPE_ENDED = 2;
    public static final int ITEM_TYPE_ERROR = 3;
    public static final int ITEM_TYPE_EMPTY = 4;
    public static final int ITEM_TYPE_IDLE = 5;
    private static final String TAG = PasteAdapter.class.getSimpleName();
    public ThumbClickListener clickListener;
    private boolean loading = false;
    private boolean ended = false;
    private boolean error = false;
    private List<Paste> pastes = new ArrayList<>();


    public PasteAdapter(ThumbClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setLoading(boolean loading) {
        this.loading = loading;
        notifyItemChanged(getItemCount() - 1);
    }

    public void setEnded(boolean ended) {
        this.ended = ended;
        notifyItemChanged(getItemCount() - 1);
    }

    public void setError(boolean error) {
        this.error = error;
        notifyItemChanged(getItemCount() - 1);
    }

    public void addPaste(int i, Paste paste) {
        pastes.add(i, paste);
        notifyItemInserted(i);
    }

    public void addPastes(int position, List<Paste> pastes) {
        this.pastes.addAll(position, pastes);
        notifyItemRangeInserted(position, pastes.size());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "Item Type is: " + viewType);
        switch (viewType) {
            case ITEM_TYPE_DATA:
                return new PasteHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_paste, parent, false));
            case ITEM_TYPE_EMPTY:
                return new RecyclerView.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empty_image, parent, false)) {
                };
            case ITEM_TYPE_LOADING: //Loading indicator
                Log.d(TAG, "Created LoadingHolder");
                return new LoadingHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default: //ITEM_TYPE_ENDED|ITEM_TYPE_ERROR|ITEM_TYPE_EMPTY
                Log.d(TAG, "Created TextHolder");
                return new TextHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_text_message, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case ITEM_TYPE_DATA:
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        clickListener.onThumbClicked(holder.getAdapterPosition());
                    }
                });
                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        return clickListener != null && clickListener.onThumbLongClicked(holder.getAdapterPosition());
                    }
                });
                ((PasteHolder) holder).bindData(pastes.get(position));
                ((PasteHolder) holder).selectedOverlay.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);
                break;
            case ITEM_TYPE_LOADING:
                break;
            //all others
            case ITEM_TYPE_EMPTY:
                //When using Staggered Grid Layout Manager, use this to span the item across whole device width
                //Thanks to Gabriele Mariotti's asnwer on this SO Question:
                // @link http://stackoverflow.com/questions/33696096/setting-span-size-of-single-row-in-staggeredgridlayoutmanager
                StaggeredGridLayoutManager.LayoutParams layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                layoutParams.setFullSpan(true);
                break;
            case ITEM_TYPE_ERROR:
                ((TextHolder) holder).setLightMessage("An error occurred while fetching data");
                break;
            case ITEM_TYPE_ENDED:
                ((TextHolder) holder).setLightMessage("End of Content.");
                break;
            case ITEM_TYPE_IDLE:
                ((TextHolder) holder).tvMessage.setVisibility(View.GONE);
                break;
        }

    }

    @Override
    public int getItemCount() {
        return pastes == null ? 1 : pastes.size() + 1;
    }

    //resets state variables
    public void resetSpecialStates() {
        loading = ended = error = false;
        notifyItemChanged(getItemCount() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        //The check for last position
        if (getItemCount() - 1 == position && loading)
            return ITEM_TYPE_LOADING;
        if (getItemCount() - 1 == position && ended)
            return ITEM_TYPE_ENDED;
        if (getItemCount() - 1 == position && error)
            return ITEM_TYPE_ERROR;
        if (getItemCount() - 1 == position && getItemCount() == 1)
            return ITEM_TYPE_EMPTY;
        if (getItemCount() - 1 == position)
            return ITEM_TYPE_IDLE;
        return ITEM_TYPE_DATA;
    }

    public void clear() {
        if (pastes != null) {
            pastes.clear();
        }
        resetSpecialStates();
        notifyDataSetChanged();
    }

    public void setPaste(int index, Paste pasteGreen) {
        pastes.set(index, pasteGreen);
        notifyItemChanged(index);
    }

    public List<Paste> getPastes() {
        return pastes;
    }

    public void setPastes(List<Paste> pastes) {
        this.pastes = pastes;
        notifyDataSetChanged();
    }


    public interface ThumbClickListener {
        void onThumbClicked(int position);

        boolean onThumbLongClicked(int position);
    }
}
