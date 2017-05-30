package app.paste_it;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

import app.paste_it.models.ImageModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ScreenSlidePageFragment extends Fragment {


    private static final String ARG1 = "ARG1";

    @BindView(R.id.ivImage)
    ImageView ivImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide, container, false);

        ButterKnife.bind(this,rootView);

        Bundle args = getArguments();
        ImageModel imageModel = args.getParcelable(ARG1);
        String path = getContext().getFilesDir().getPath() + "/" + imageModel.getFileName();
        File file = new File(path);
        if(file.exists())
        Picasso.with(getContext()).load(file).into(ivImage);
        else if (imageModel.getDownloadURL()!=null){
            Picasso.with(getContext()).load(imageModel.getDownloadURL()).into(ivImage);
        }
        else ivImage.setImageBitmap(null);

        return rootView;
    }

    public static Fragment newInstance(ImageModel imageModel) {
        ScreenSlidePageFragment screenSlidePageFragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG1, imageModel);
        screenSlidePageFragment.setArguments(args);
        return screenSlidePageFragment;
    }
}
