package app.paste_it.models.holders;

import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import app.paste_it.PasteUtils;
import app.paste_it.R;
import app.paste_it.adapters.ImageAdapter;
import app.paste_it.adapters.PreviewImageAdapter;
import app.paste_it.models.ImageModel;
import app.paste_it.models.Paste;
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
    @BindView(R.id.rvImagePreview)
    RecyclerView rvImagePreview;
    CardView rootCard;


    public PasteHolder(View rootView){
        super(rootView);
        rootView.setOnClickListener(this);
        rootCard = (CardView)rootView;
        ButterKnife.bind(this,rootView);
        rvImagePreview.setLayoutManager(new GridLayoutManager(rootView.getContext(),2));

    }

    public void bindData(Paste paste){
        if(paste.getTitle()==null || paste.getTitle().length()==0) {
            tvTitle.setVisibility(View.GONE);
        }
        else{
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(paste.getTitle());
        }
        if(paste.getText()==null || paste.getText().length()==0) {
            tvContent.setVisibility(View.GONE);
        }
        else {
            tvContent.setVisibility(View.VISIBLE);
            tvContent.setText(paste.getText());
        }

        if(paste.getUrls()!=null && paste.getUrls().size() > 0) {
            Log.d(TAG, "URLS are: " + paste.getUrls());
            rvImagePreview.setVisibility(View.VISIBLE);
            rvImagePreview.setAdapter(new PreviewImageAdapter(new ArrayList<>(paste.getUrls().values())));
        }
        else {
            rvImagePreview.setAdapter(null);
            rvImagePreview.setVisibility(View.GONE);
        }


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
