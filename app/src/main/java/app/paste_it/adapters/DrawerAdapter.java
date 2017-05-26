package app.paste_it.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import app.paste_it.R;
import app.paste_it.models.Tag;
import app.paste_it.models.holders.UserInfoHolder;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by madeyedexter on 5/25/2017.
 */

public class DrawerAdapter extends RecyclerView.Adapter {

    public static final int VIEW_TYPE_USER_INFO = 1;
    public static final int VIEW_TYPE_SECTION = 4;
    public static final int VIEW_TYPE_DIVIDER = 5;
    public static final int VIEW_TYPE_SECTION_HEADER = 6;
    public static final int VIEW_TYPE_NO_SECTION = 2;
    private static final String TAG = DrawerAdapter.class.getSimpleName();

    private View.OnClickListener onClickListener;
    private List<Tag> items = new ArrayList<>();
    private int selectionPosition=1;

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

    public List<Tag> getItems() {
        return items;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int viewType = getItemViewType(position);
        if(viewType==VIEW_TYPE_USER_INFO){
            UserInfoHolder userInfoHolder = (UserInfoHolder)holder;
            userInfoHolder.bindData();
            return;
        }
        if(viewType == VIEW_TYPE_DIVIDER || viewType == VIEW_TYPE_SECTION_HEADER){
            return;
        }
        if(viewType == VIEW_TYPE_NO_SECTION){
            SectionHolder sectionHolder = (SectionHolder) holder;
            if(position == getItemCount()-2)
                sectionHolder.bindData(R.drawable.ic_settings_black_24dp, "Settings");
            if(position == getItemCount()-1)
                sectionHolder.bindData(R.drawable.ic_info_black_24dp, "About");
            sectionHolder.itemView.setTag(R.string.selection_postion,position);
        }
        if(viewType == VIEW_TYPE_SECTION){
            SectionHolder sectionHolder = (SectionHolder) holder;
            if(position==1)
            sectionHolder.bindData(R.drawable.ic_note_black_24dp, "Pastes");
            else if(position==2)
                sectionHolder.bindData(R.drawable.ic_archive_black_24dp, "Archived");
            else{
                if(items.size()!=0){
                    sectionHolder.bindData(R.drawable.ic_label_black_24dp,items.get(position%5).getLabel());
                }
            }
            sectionHolder.itemView.setTag(R.string.selection_postion,position);
        }
        if(position==selectionPosition){
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorLightGrey));
        }
        else{
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.transparent));
        }
    }

    @Override
    public int getItemCount() {
        return items == null ? 8 : items.size() + 8;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0)
            return VIEW_TYPE_USER_INFO;
        if (position == 3 || position == items.size() + 5)
            return VIEW_TYPE_DIVIDER;
        if (position == 1 || position == 2 || (position >=5 && position <= 5+items.size()) || position >= items.size()+8)
            return VIEW_TYPE_SECTION;
        if(position==4)
            return VIEW_TYPE_SECTION_HEADER;
        else
            return VIEW_TYPE_NO_SECTION;
    }

    public int getSelectionPosition() {
        return selectionPosition;
    }

    public void setSelectionPosition(int selectionPosition) {
        this.selectionPosition = selectionPosition;
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
        @BindView(R.id.ivSectionIcon)
        ImageView ivSectionIcon;
        @BindView(R.id.tvSectionText)
        TextView tvSectionText;

        public SectionHolder(View rootView) {
            super(rootView);
            ButterKnife.bind(this,rootView);
        }

        public void bindData(int drawableRes, String text){
            ivSectionIcon.setImageResource(drawableRes);
            tvSectionText.setText(text);
        }
    }

}
