package app.paste_it;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;

import app.paste_it.adapters.ImageAdapter;
import app.paste_it.models.ImageModel;
import app.paste_it.models.Paste;
import app.paste_it.models.Tag;
import app.paste_it.service.ImageImportService;
import app.paste_it.service.ImageUploadService;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A Standalone activity which will be launched whenever a user wants to save some content.
 */

public class PasteItActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = PasteItActivity.class.getSimpleName();

    private static final int RC_SELECT_PICTURE = 1;
    private static final int RC_CAPTURE_IMAGE = 2;
    private static final String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvImages)
    RecyclerView rvImages;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.etTag)
    EditText etTag;
    @BindView(R.id.tvLastUpdated)
    TextView tvLastUpdated;
    private ImageAdapter imageAdapter;
    private Paste paste;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste_it);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.paste_it));

        final int noOfColumns = Utils.calculateNoOfColumns(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this,noOfColumns);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if(noOfColumns==2){

                }
                return 2;
            }
        });

        imageAdapter = new ImageAdapter();
        rvImages.setAdapter(imageAdapter);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_paste_it, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    //todo: parcel paste object

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                savePaste();
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.miAttachImageFromCamera: //TODO: launch camera activity
                break;
            case R.id.miAttachImageFromFile:
                pickImage();
                break;
            case R.id.miTag:

            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickImage() {
        Utils.verifyStoragePermissions(this);
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            switch (requestCode) {
                case RC_SELECT_PICTURE:
                    //paste id
                    String pasteId = savePaste();
                    //image model id
                    String id = String.valueOf(System.currentTimeMillis());
                    ImageModel imageModel = new ImageModel();
                    imageModel.setId(id);
                    imageModel.setPasteId(pasteId);
                    ImageImportService.startActionImport(this,data.getData(),imageModel);
                    //TODO: retrieve image meta data and display image preview, after the image is imported
                    imageAdapter.addItem(imageModel);

                    break;
                case RC_CAPTURE_IMAGE:
                    break;
            }
        }
    }

    private String savePaste() {
        if(paste==null)
            paste =new Paste();
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if(paste.getCreated()==null)
        paste.setCreated(System.currentTimeMillis());
        paste.setModified(System.currentTimeMillis());
        paste.setTitle(title);
        paste.setText(content);

        String id = null;
        DatabaseReference newPasteRef = null;
        if(paste.getId()==null) {
             newPasteRef = FirebaseDatabase.getInstance().getReference("pastes/" + UID).push();
             id = newPasteRef.getKey();
            paste.setId(id);
        }
        else {
            id = paste.getId();
            newPasteRef = FirebaseDatabase.getInstance().getReference("pastes/" + UID).child(id);
        }
        //do not save the pastes, they will be saved asynchronously later when the images are uploaded
        ArrayList<Tag> tags = new ArrayList<>();
        paste.setTags(tags);
        newPasteRef.setValue(paste);
        tvLastUpdated.setText("Changes Saved: "+paste.getModified());
        return id;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d(TAG,"Preferences changed with key: "+key);
        //if we are still here, the user has imported the images and doing some other task
        //The images have been loaded locally, but not yet uploaded to the cloud
        Gson gson = new Gson();
        if(key.equals(getString(R.string.key_image_model_updated))){
            String jsonString = sharedPreferences.getString(key,"");
            ImageModel imageModel = gson.fromJson(jsonString,ImageModel.class);
            paste.getUrls().put(imageModel.getId(),imageModel);
            int index = PasteUtils.findIndex(imageAdapter.getItems(),imageModel);
            Log.d(TAG,"Index is: "+index);
            Log.d(TAG,"Items in Adapter are: "+imageAdapter.getItems());
            if(index > -1){
                imageAdapter.getItems().set(index,imageModel);
                imageAdapter.notifyDataSetChanged();
            }
            //start the image upload service
            Intent intent  = new Intent(this, ImageUploadService.class);
            intent.setAction(ImageUploadService.ACTION_IMAGE_UPLOAD);
            startService(intent);
        }
        if(key.equals(getString(R.string.key_dload_uri_available))){
            String jsonString = sharedPreferences.getString(key,"");
            ImageModel imageModel = gson.fromJson(jsonString,ImageModel.class);
            paste.getUrls().put(imageModel.getId(),imageModel);
            int index = PasteUtils.findIndex(imageAdapter.getItems(),imageModel);
            Log.d(TAG,"Index is: "+index);
            if(index > -1){
                imageAdapter.getItems().set(index,imageModel);
                imageAdapter.notifyDataSetChanged();
            }
        }
    }



}
