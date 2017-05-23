package app.paste_it.models.holders;

import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.RecyclerView;
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

    public void bindData(Tag tag){
        acctTagName.setText(tag.getLabel());
        ibDeleteTag.setTag(tag.getId());
    }
}
