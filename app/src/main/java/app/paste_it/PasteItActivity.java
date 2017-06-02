package app.paste_it;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import app.paste_it.models.Paste;
import app.paste_it.models.Tag;

/**
 * A Standalone activity which will be launched whenever a user wants to save some content.
 */

public class PasteItActivity extends AppCompatActivity implements ConfirmDialogFragment.YesNoListener {

    private static final String TAG = PasteItActivity.class.getSimpleName();

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paste_it);
        Paste paste = null;
        if (getIntent() != null && getIntent().hasExtra(getString(R.string.key_paste))) {
            paste = getIntent().getParcelableExtra(getString(R.string.key_paste));
        }
        if (savedInstanceState == null) {
            PasteItFragment pasteItFragment = PasteItFragment.newInstance(paste);
            //to be notified whenever an item is removed from the fragment
            getSupportFragmentManager().beginTransaction().add(R.id.frame, pasteItFragment, getString(R.string.tag_pasteit_fragment)).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onYes(Parcelable data) {
        if(data instanceof Tag){
            Tag tag = (Tag) data;
            FirebaseDatabase.getInstance().getReference("tags").child(uid).child(tag.getId()).removeValue();
        }
    }

    @Override
    public void onNo() {
        //do Nothing
    }

    @Override
    public void onBackPressed() {
        TagFragment tagFragment = (TagFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_tag_fragment));
        PasteItFragment pasteItFragment = (PasteItFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.tag_pasteit_fragment));
        if (tagFragment != null) {
            HashMap<String, Tag> tags = tagFragment.getTags();
            if (pasteItFragment != null) {
                Paste paste = pasteItFragment.getPaste();
                paste.setTags(tags);
                pasteItFragment.addTags();
                //also save the tags
                pasteItFragment.savePaste();
            }

        }
        super.onBackPressed();
    }
}
