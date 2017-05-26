package app.paste_it;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import app.paste_it.adapters.PasteAdapter;
import app.paste_it.models.Paste;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = PastesFragment.class.getSimpleName();

    public PastesFragment() {
        // Required empty public constructor
    }

    @BindView(R.id.rvPaste)
    RecyclerView rvPaste;
    @BindView(R.id.srLayout)
    SwipeRefreshLayout srLayout;
    @BindView(R.id.fabNewPaste)
    FloatingActionButton fabNewPaste;

    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            //This callback will be fired for each paste,
            Log.d(TAG, "String: " + s + "\nAdded Snapshot: " + dataSnapshot);
            Paste paste = dataSnapshot.getValue(Paste.class);
            PasteUtils.resolvePaste(MainActivity.this, paste, (PasteAdapter) rvPaste.getAdapter());
            srLayout.setRefreshing(false);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "String: " + s + "\nChanged Snapshot: " + dataSnapshot);
            Paste paste = dataSnapshot.getValue(Paste.class);
            PasteUtils.resolvePaste(MainActivity.this, paste, (PasteAdapter) rvPaste.getAdapter());
            srLayout.setRefreshing(false);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG, "Removed Snapshot: " + dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PastesFragment.
     */
    public static PastesFragment newInstance() {
        PastesFragment fragment = new PastesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        if(savedInstanceState!=null){

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.key_section),getActivity().getTitle().toString());
        outState.putParcelable(getString(R.string.key_rvos), rvPaste.getLayoutManager().onSaveInstanceState());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pastes, container, false);
        ButterKnife.bind(this,rootView);

        fabNewPaste.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabNewPaste: startPasteItActivity(new Paste());
                break;
        }
    }

    @Override
    public void onRefresh() {
        srLayout.setRefreshing(true);
        pasteReference.addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public void onThumbClicked(Paste paste) {
        startPasteItActivity(paste);
    }

    private void startPasteItActivity(Paste paste) {
        Intent intent = new Intent(getActivity(), PasteItActivity.class);
        intent.putExtra(getString(R.string.key_paste), paste);
        startActivity(intent);
    }
}
