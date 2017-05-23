package app.paste_it;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckedTextView;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import app.paste_it.adapters.TagAdapter;
import app.paste_it.models.Paste;
import app.paste_it.models.Tag;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A Dialog fragment which shows  a list of tags the user has created
 * and provides user with a list of tags.
 */
public class TagFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = TagFragment.class.getSimpleName();

    @BindView(R.id.rvTags)
    RecyclerView rvTags;
    @BindView(R.id.ibAddTag)
    ImageButton ibAddTag;
    @BindView(R.id.etAddTag)
    EditText etAddTag;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tags")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Log.d(TAG,"DataSnapshot is: "+dataSnapshot);
            Map<String,Tag> map = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, Tag>>() {});
            if(map!=null && map.size()!=0){
                TagAdapter tagAdapter = (TagAdapter)rvTags.getAdapter();
                tagAdapter.setTags(new ArrayList<>(map.values()));
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home: getActivity().getSupportFragmentManager().popBackStack();
                return true;
            default: return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
    }

    public static TagFragment newInstance(Paste paste) {
        TagFragment tagFragment = new TagFragment();
        Bundle args = new Bundle();
        args.putParcelable("PASTE", paste);
        tagFragment.setArguments(args);
        return tagFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment__tag, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        rvTags.setLayoutManager(linearLayoutManager);
        TagAdapter tagAdapter = new TagAdapter(this);
        rvTags.setAdapter(tagAdapter);

        databaseReference.addListenerForSingleValueEvent(valueEventListener);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        appCompatActivity.setSupportActionBar(toolbar);
        appCompatActivity.setTitle(getString(R.string.choose_tags));

        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);


        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.acctTagName: ((AppCompatCheckedTextView)v).toggle();
                break;
            case R.id.ibDeleteTag:
                String tagId = v.getTag().toString();
                Tag tag = new Tag();
                tag.setId(tagId);
                ConfirmDialogFragment.newInstance(tag).show(getActivity().getSupportFragmentManager(),"ConfirmDeleteDialog");
                break;
        }
    }
}
