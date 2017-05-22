package app.paste_it;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.paste_it.models.Paste;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A Dialog fragment which shows  a list of tags the user has created
 * and provides user with a list of tags.
 */
public class TagFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.rvTags)
    RecyclerView recyclerView;
    @BindView(R.id.ibAddTag)
    ImageButton ibAddTag;
    @BindView(R.id.etAddTag)
    EditText etAddTag;
    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("tags")
            .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    public static TagFragment newInstance(Paste paste) {
        TagFragment tagFragment = new TagFragment();
        Bundle args = new Bundle();
        tagFragment.setArguments(args);
        return tagFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_dialog_tag, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
}
