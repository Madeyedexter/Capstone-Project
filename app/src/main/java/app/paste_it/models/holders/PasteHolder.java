package app.paste_it.models.holders;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import app.paste_it.R;
import app.paste_it.models.greendao.Paste;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Madeyedexter on 16-05-2017.
 */

public class PasteHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    private static final String TAG = PasteHolder.class.getSimpleName();

    @BindView(R.id.tvTitle)
    TextView tvTitle;
    @BindView(R.id.tvContent)
    TextView tvContent;
    @BindView(R.id.llTagsWrapper)
    LinearLayout llTagsWrapper;
    @BindView(R.id.llTagViewGroup)
    LinearLayout llTagViewGroup;
    CardView rootCard;


    public PasteHolder(View rootView){
        super(rootView);
        rootView.setOnClickListener(this);
        rootCard = (CardView)rootView;
        ButterKnife.bind(this,rootView);

    }

    public void bindData(Paste paste){
        if(paste.getTitle()==null || paste.getTitle().length()==0)
            tvTitle.setVisibility(View.GONE);
        else
            tvTitle.setText(paste.getTitle());
        if(paste.getText()==null || paste.getText().length()==0)
            tvContent.setVisibility(View.GONE);
        else
            tvContent.setText(paste.getText());

        if(paste.getUrls()!=null && paste.getUrls().size() > 0)
        Picasso.with(rootCard.getContext()).load(paste.getUrls().get(0).getUrl()).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Log.d(TAG, "Bitmap is: "+bitmap.toString());
                rootCard.setBackground(new BitmapDrawable(rootCard.getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                Log.d(TAG, "Bitmap Failed!");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

        if(paste.getTags()== null || paste.getTags().size()==0){
            llTagsWrapper.setVisibility(View.GONE);
        }
        else{
            llTagsWrapper.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View v) {

    }
}
