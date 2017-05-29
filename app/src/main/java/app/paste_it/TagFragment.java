package app.paste_it;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    private static final String ARG_PASTE = "ARG_PASTE";


    @BindView(R.id.rvTags)
    RecyclerView rvTags;
    @BindView(R.id.ibAddTag)
    ImageButton ibAddTag;
    @BindView(R.id.etAddTag)
    EditText etAddTag;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tags")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            Log.d(TAG, "DataSnapshot is: " + dataSnapshot);
            Map<String, Tag> map = dataSnapshot.getValue(new GenericTypeIndicator<Map<String, Tag>>() {
            });
            if (map != null && map.size() != 0) {
                TagAdapter tagAdapter = (TagAdapter) rvTags.getAdapter();
                List<Tag> tagList = new ArrayList<>(map.values());
                Paste paste = getArguments().getParcelable(ARG_PASTE);
                HashMap<String, Tag> pasteTags = paste.getTags();
                for (Tag tag : tagList) {
                    tag.setSelected(pasteTags.containsKey(tag.getId()));
                }
                tagAdapter.setTags(tagList);
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };


    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Tag tag = dataSnapshot.getValue(Tag.class);
            TagAdapter tagAdapter = (TagAdapter) rvTags.getAdapter();
            int index = PasteUtils.findIndexOfTag(tagAdapter.getTags(), tag.getId());
            tagAdapter.getTags().remove(index);
            tagAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public static TagFragment newInstance(Paste paste) {
        TagFragment tagFragment = new TagFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PASTE, paste);
        tagFragment.setArguments(args);
        return tagFragment;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        databaseReference.removeEventListener(childEventListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment__tag, container, false);
        ButterKnife.bind(this, rootView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvTags.setLayoutManager(linearLayoutManager);
        TagAdapter tagAdapter = new TagAdapter(this);

        Log.d(TAG, "After restore: " + tagAdapter.getTags());
        rvTags.setAdapter(tagAdapter);

        ibAddTag.setOnClickListener(this);

        databaseReference.orderByKey().addListenerForSingleValueEvent(valueEventListener);
        //only for removing tag
        databaseReference.addChildEventListener(childEventListener);


        return rootView;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.acctTagName:
                checkTag(v);
                break;
            case R.id.ibDeleteTag:
                showDeletTagConfirmDialog(v);
                break;
            case R.id.ibAddTag:
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addTag();
                    }
                }, 100);
                break;
        }
    }

    private void showDeletTagConfirmDialog(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        TagAdapter tagAdapter = (TagAdapter) rvTags.getAdapter();
        Tag tag = tagAdapter.getTags().get(position);
        ConfirmDialogFragment.newInstance(tag).show(getActivity().getSupportFragmentManager(), getString(R.string.tag_fragment_confirmdeletedialog));

    }

    private void checkTag(View v) {
        int position = Integer.parseInt(v.getTag().toString());
        TagAdapter tagAdapter = (TagAdapter) rvTags.getAdapter();
        Tag tag = tagAdapter.getTags().get(position);
        tag.setSelected(!tag.isSelected());
        Paste paste = getArguments().getParcelable(ARG_PASTE);
        paste.getTags().put(tag.getId(), tag);
        tagAdapter.getTags().set(position, tag);
        tagAdapter.notifyItemChanged(position);
    }

    private void addTag() {
        if (etAddTag.getText().toString().contains(" ")) {
            etAddTag.setError(getString(R.string.error_message_nowhitespace));
            return;
        }
        if (etAddTag.getText().length() < 3) {
            etAddTag.setError(getString(R.string.error_message_tag_length));
            return;
        }
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (uid != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tags").child(uid).push();
            String tagId = databaseReference.getKey();
            Tag newTag = new Tag();
            newTag.setId(tagId);
            newTag.setLabel(etAddTag.getText().toString());
            databaseReference.setValue(newTag);
            TagAdapter tagAdapter = (TagAdapter) rvTags.getAdapter();
            tagAdapter.getTags().add(newTag);
            tagAdapter.notifyItemInserted(tagAdapter.getItemCount() - 1);
            etAddTag.getText().clear();
        }
    }

    public HashMap<String, Tag> getTags() {
        TagAdapter tagAdapter = (TagAdapter) rvTags.getAdapter();
        HashMap<String, Tag> hashMap = new HashMap<>();
        for (Tag tag : tagAdapter.getTags()) {
            if (tag.isSelected()) hashMap.put(tag.getId(), tag);
        }
        return hashMap;
    }
}
