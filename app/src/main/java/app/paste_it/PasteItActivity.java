package app.paste_it;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
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

public class PasteItActivity extends AppCompatActivity implements ConfirmDialogFragment.YesNoListener{

    private static final String TAG = PasteItActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste_it);
        Paste paste = null;
        if(getIntent()!=null && getIntent().hasExtra(getString(R.string.key_paste))){
            paste = getIntent().getParcelableExtra(getString(R.string.key_paste));
        }
        if(savedInstanceState==null){
            PasteItFragment pasteItFragment = PasteItFragment.newInstance(paste);
            getSupportFragmentManager().beginTransaction().add(R.id.frame,pasteItFragment).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onYes(Tag tag) {

    }

    @Override
    public void onNo() {
        //do Nothing
    }
}
