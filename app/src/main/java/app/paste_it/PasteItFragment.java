package app.paste_it;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import app.paste_it.adapters.ImageAdapter;
import app.paste_it.models.ImageModel;
import app.paste_it.models.Paste;
import app.paste_it.models.Tag;
import app.paste_it.service.ImageImportService;
import app.paste_it.service.ImageUploadService;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PasteItFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PasteItFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener, View.OnClickListener{

    private static final String TAG = PasteItFragment.class.getSimpleName();

    private static final int RC_SELECT_PICTURE = 1;
    private static final int RC_CAPTURE_IMAGE = 2;
    private static final String UID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private static final String ARG_PARAM1 = "param1";
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.rvImages)
    RecyclerView rvImages;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.tvLastUpdated)
    TextView tvLastUpdated;
    @BindView(R.id.llTagHolder)
    LinearLayout llTagHolder;
    private ImageAdapter imageAdapter;
    private Paste paste;

    public PasteItFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paste The Paste being shown in this fragment
     * @return A new instance of fragment PasteItFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PasteItFragment newInstance(Paste paste) {
        PasteItFragment fragment = new PasteItFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM1, paste);
        fragment.setArguments(args);
        return fragment;
    }

    public Paste getPaste() {
        return paste;
    }

    public void setPaste(Paste paste) {
        this.paste = paste;
        savePaste();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //when fragment is first launched
        if(getArguments()!=null){
            paste = getArguments().getParcelable(ARG_PARAM1);
        }
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    //todo: parcel paste object

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.key_paste),paste);
        outState.putParcelable(getString(R.string.key_ll_os),rvImages.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_paste_it, menu);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                savePaste();
                NavUtils.navigateUpFromSameTask(getActivity());
                break;
            case R.id.miAttachImageFromCamera: //TODO: launch camera activity
                break;
            case R.id.miAttachImageFromFile:
                pickImage();
                break;
            case R.id.miTag: showTagFragment();

            default:
                super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showTagFragment() {
        TagFragment tagFragment = TagFragment.newInstance(paste);
        getActivity().getSupportFragmentManager().beginTransaction().addToBackStack(getString(R.string.tag_tag_fragment)).add(R.id.frame, tagFragment, getString(R.string.tag_tag_fragment)).commit();
    }

    private void pickImage() {
        Utils.verifyStoragePermissions(getActivity());
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            switch (requestCode) {
                case RC_SELECT_PICTURE:
                    //paste id
                    String pasteId = savePaste();
                    //image model id
                    String id = String.valueOf(System.currentTimeMillis());
                    ImageModel imageModel = new ImageModel();
                    imageModel.setId(id);
                    imageModel.setPasteId(pasteId);
                    paste.getUrls().put(imageModel.getId(),imageModel);
                    ImageImportService.startActionImport(getContext(),data.getData(),imageModel);
                    //TODO: retrieve image meta data and display image preview, after the image is imported
                    imageAdapter.addItem(imageModel);

                    break;
                case RC_CAPTURE_IMAGE:
                    break;
            }
        }
    }

    public String savePaste() {
        if(paste==null)
            paste =new Paste();
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        if(paste.getCreated()==0)
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
        newPasteRef.setValue(paste);

        if(paste.getModified()!=0){
            tvLastUpdated.setVisibility(View.VISIBLE);
        tvLastUpdated.setText("Changes Saved: "+paste.getModified());
        }
        return id;
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
            Intent intent  = new Intent(getContext(), ImageUploadService.class);
            intent.setAction(ImageUploadService.ACTION_IMAGE_UPLOAD);
            getContext().startService(intent);
        }
        if(key.equals(getString(R.string.key_dload_uri_available))){
            String jsonString = sharedPreferences.getString(key,"");
            ImageModel imageModel = gson.fromJson(jsonString,ImageModel.class);
            if(imageModel.getPasteId().equals(paste.getId())) {
                paste.getUrls().put(imageModel.getId(), imageModel);
                int index = PasteUtils.findIndex(imageAdapter.getItems(), imageModel);
                Log.d(TAG, "Index is: " + index);
                if (index > -1) {
                    imageAdapter.getItems().set(index, imageModel);
                    imageAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paste_it, container, false);
        ButterKnife.bind(this,view);

        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.setTitle(getString(R.string.paste_it));
        setHasOptionsMenu(true);

        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(FlexDirection.ROW, JustifyContent.FLEX_END);
        rvImages.setLayoutManager(flexboxLayoutManager);

        //when fragment is recreated
        if(savedInstanceState!=null){
            paste = savedInstanceState.getParcelable(getString(R.string.key_paste));
            Parcelable lloS = savedInstanceState.getParcelable(getString(R.string.key_ll_os));
            flexboxLayoutManager.onRestoreInstanceState(lloS);
        }
        imageAdapter = new ImageAdapter(paste.getUrls().values());
        rvImages.setAdapter(imageAdapter);

        etTitle.setText(paste.getTitle());
        etContent.setText(paste.getText());
        addTags();

        llTagHolder.setOnClickListener(this);
        if(paste.getModified()!=0){
            tvLastUpdated.setVisibility(View.VISIBLE);
            tvLastUpdated.setText("Last Modified: "+ paste.getModified());
        }
        return view;
    }

    public void addTags() {
        llTagHolder.removeAllViews();
        for(Tag tag : paste.getTags().values()){
            TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.item_textview_tag, null);
            textView.setText(tag.getLabel());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(16,16,16,16);
            textView.setLayoutParams(layoutParams);
            llTagHolder.addView(textView);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.llTagHolder: showTagFragment();
        }
    }
}
