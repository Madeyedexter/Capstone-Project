package app.paste_it.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import app.paste_it.R;
import app.paste_it.models.holders.UserInfoHolder;

/**
 * Created by madeyedexter on 5/25/2017.
 */

public class DrawerAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_USER_INFO = 1;
    public static final int VIEW_TYPE_SECTION = 4;
    public static final int VIEW_TYPE_DIVIDER = 5;
    public static final int VIEW_TYPE_SECTION_HEADER = 6;
    public static final int VIEW_TYPE_NO_SECTION = 2;

    private View.OnClickListener onClickListener;
    private List<Object> items = new ArrayList<>();

    public DrawerAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_USER_INFO:
                return new UserInfoHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_user_summary, parent, false));
            case VIEW_TYPE_DIVIDER:
                return new DummyHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_divider, parent, false));
            case VIEW_TYPE_SECTION_HEADER:
                return new DummyHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_section_header, parent, false));
            case VIEW_TYPE_SECTION:
                RecyclerView.ViewHolder viewHolder = new SectionHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_section, parent, false
                ));
                viewHolder.itemView.setOnClickListener(onClickListener);
                viewHolder.itemView.setTag(VIEW_TYPE_SECTION);
                return viewHolder;
            case VIEW_TYPE_NO_SECTION:
                RecyclerView.ViewHolder noSectionView = new SectionHolder(LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_section, parent, false
                ));
                noSectionView.itemView.setOnClickListener(onClickListener);
                noSectionView.itemView.setTag(onClickListener);
                return noSectionView;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return items == null ? 8 : items.size() + 8;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_USER_INFO;
        if (position == 1 || position == 2 || position == 4)
            return VIEW_TYPE_SECTION;
        if (position == 3 || position == items.size() + 1)
            return VIEW_TYPE_DIVIDER;
        if (position == 4 || position == 4 + items.size())
            return VIEW_TYPE_SECTION;
        else
            return VIEW_TYPE_NO_SECTION;

    }

    interface DrawerClickCallback {
        void onDrawerItemClicked(Object item);
    }

    public class DummyHolder extends RecyclerView.ViewHolder {
        DummyHolder(View rootView) {
            super(rootView);
        }
    }

    public class SectionHolder extends RecyclerView.ViewHolder {
        public SectionHolder(View rootView) {
            super(rootView);
        }
    }

}
