package app.paste_it;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;

import app.paste_it.adapters.ImageAdapter;
import app.paste_it.models.firebase.Paste;
import app.paste_it.models.greendao.ImageURL;
import app.paste_it.models.greendao.Tag;
import app.paste_it.service.SavePasteService;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A Standalone activity which will be launched whenever a user wants to save some content.
 *
 */

public class PasteItActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{

    private static final String TAG = PasteItActivity.class.getSimpleName();

    private static final int RC_SELECT_PICTURE = 1;
    private static final int RC_CAPTURE_IMAGE = 2;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.gvImages)
    GridView gvImages;
    @BindView(R.id.etTitle)
    EditText etTitle;
    @BindView(R.id.etContent)
    EditText etContent;
    @BindView(R.id.etTag)
    EditText etTag;

    private ImageAdapter imageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste_it);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.paste_it));

        imageAdapter = new ImageAdapter(this);
        gvImages.setAdapter(imageAdapter);
        gvImages.setOnItemClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.paste_it_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            case R.id.miDelete: finish();
                break;
            case R.id.miAttachImageFromCamera: //TODO: launch camera activity
                break;
            case R.id.miAttachImageFromFile:
                pickImage();

                break;
            case R.id.miSave: savePaste();

            default:super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickImage() {
        Utils.verifyStoragePermissions(this);
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), RC_SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK && data!=null && data.getData()!=null){
            switch (requestCode){
                case RC_SELECT_PICTURE: imageAdapter.addUri(data.getData());
                    break;
                case RC_CAPTURE_IMAGE: break;
            }
        }
    }

    private void savePaste() {
        String title = etTitle.getText().toString();
        String content = etContent.getText().toString();
        Paste paste = new Paste();
        paste.setCreated(System.currentTimeMillis());
        paste.setModified(System.currentTimeMillis());
        paste.setTitle(title);
        paste.setText(content);
        ArrayList<String> uris = new ArrayList<>();
        for(int i=0; i< imageAdapter.getCount(); i++){
            uris.add(imageAdapter.getItem(i).toString());
        }
        paste.setUrls(uris);
        ArrayList<String> tags = new ArrayList<>();
        paste.setTags(tags);
        /*
        for(int i=0; i< imageAdapter.getCount(); i++){
            ImageURL imageURL = new ImageURL();
            imageURL.setUri(imageAdapter.getItem(i).toString());
            uris.add(imageURL);
        }*/
        Intent intent = new Intent(this, SavePasteService.class);
        intent.putExtra("paste",paste);
        startService(intent);
        Toast.makeText(this,"Your paste will be saved.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    }
}
