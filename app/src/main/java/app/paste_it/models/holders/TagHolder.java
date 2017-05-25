package app.paste_it.models.holders;

import android.icu.text.Normalizer2;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import app.paste_it.R;
import app.paste_it.models.Tag;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Madeyedexter on 23-05-2017.
 */

public class TagHolder extends RecyclerView.ViewHolder {

    private static final String TAG = TagHolder.class.getSimpleName();

    @BindView(R.id.acctTagName)
    AppCompatCheckedTextView acctTagName;
    @BindView(R.id.ibDeleteTag)
    ImageButton ibDeleteTag;

    public TagHolder(View itemView, View.OnClickListener onClickListener) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        acctTagName.setOnClickListener(onClickListener);
        ibDeleteTag.setOnClickListener(onClickListener);
    }

    public void bindData(int position,Tag tag){
        acctTagName.setText(tag.getLabel());
        acctTagName.setChecked(tag.isSelected());
        ibDeleteTag.setTag(position);
        acctTagName.setTag(position);
    }
}
