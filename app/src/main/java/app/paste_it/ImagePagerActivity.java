package app.paste_it;

import android.content.Intent;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.List;

import app.paste_it.models.ConfirmDialogMessage;
import app.paste_it.models.ImageModel;
import app.paste_it.service.ImageImportService;
import app.paste_it.service.ImageUploadService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class ImagePagerActivity extends AppCompatActivity{

    private static final String TAG = ImagePagerActivity.class.getSimpleName();
    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


    private static final String FILE_AUTHORITY = "app.paste_it.fileprovider";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_pager);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this,R.color.colorPrimary));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final List<ImageModel> imageModels = intent.getParcelableArrayListExtra(getString(R.string.key_image_models));
        int position = intent.getIntExtra(getString(R.string.key_position),0);

        setTitle(imageModels.get(position).getFileName());

        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager(),imageModels);
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(position,false);
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setTitle(imageModels.get(position).getFileName());

            }
        });
        mPager.setPageTransformer(true, new ZoomOutPageTransformer());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.miDelete: showConfirmDeleteDialog(mPagerAdapter.getItemAtPosition(mPager.getCurrentItem()));
                return true;
            case R.id.miShare: startActionImageShare();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private void startActionImageShare() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        ImageModel imageModel = mPagerAdapter.getItemAtPosition(mPager.getCurrentItem());
        Uri uri = FileProvider.getUriForFile(this,FILE_AUTHORITY,new File(Utils.getFullPath(this,imageModel.getFileName())));
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        if(imageModel.getFileName().endsWith("png") || imageModel.getFileName().endsWith("PNG"))
        intent.setType("image/png");
        else
            intent.setType("image/jpeg");
        startActivity(Intent.createChooser(intent,getString(R.string.send_to)));
    }

    private void showConfirmDeleteDialog(ImageModel imageModel) {
        ConfirmDialogMessage message = new ConfirmDialogMessage(getString(R.string.confirm_delete),getString(R.string.confirm_dialog_picture));
        ConfirmDialogFragment.newInstance(imageModel, message).show(getSupportFragmentManager(), getString(R.string.tag_fragment_confirmdeletedialog));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_pager, menu);
        return true;
    }

    @BindView(R.id.pager)
    ViewPager mPager;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */
    private ScreenSlidePagerAdapter mPagerAdapter;


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        private List<ImageModel> imageModelList;

        public ImageModel getItemAtPosition(int position){
            return imageModelList.get(position);
        }

        public ScreenSlidePagerAdapter(FragmentManager fm, List<ImageModel> imageModels) {
            super(fm);
            this.imageModelList  = imageModels;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment = ScreenSlidePageFragment.newInstance(imageModelList.get(position));
            return fragment;
        }

        @Override
        public int getCount() {
            return imageModelList.size();
        }
    }
}
