package app.paste_it;


import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import app.paste_it.adapters.PasteAdapter;
import app.paste_it.models.Paste;
import app.paste_it.models.Tag;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PastesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PastesFragment extends Fragment implements View.OnClickListener, PasteAdapter.ThumbClickListener, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = PastesFragment.class.getSimpleName();

    private static final String ARG1 = "ARG1";
    private static final java.lang.String TAG_ARG = "TAG_ARG";
    @BindView(R.id.rvPaste)
    RecyclerView rvPaste;
    @BindView(R.id.srLayout)
    SwipeRefreshLayout srLayout;
    @BindView(R.id.fabNewPaste)
    FloatingActionButton fabNewPaste;
    private DatabaseReference pasteReference = FirebaseDatabase.getInstance().getReference("pastes").child(
            FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                Paste paste = childSnapshot.getValue(Paste.class);
                resolvePaste(paste);
            }
            srLayout.setRefreshing(false);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            srLayout.setRefreshing(false);
        }
    };
    private ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Paste paste = dataSnapshot.getValue(Paste.class);
            resolvePaste(paste);
            srLayout.setRefreshing(false);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Paste paste = dataSnapshot.getValue(Paste.class);
            resolvePaste(paste);
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

    public PastesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @param tag An optional tag parameter, passed when this fragment is created for a tag section
     *            in the navigation drawer.
     * @return A new instance of fragment PastesFragment.
     */
    public static PastesFragment newInstance(String fragmentType, @Nullable Tag tag) {
        PastesFragment fragment = new PastesFragment();
        Bundle args = new Bundle();
        args.putString(ARG1, fragmentType);
        if (tag != null) {
            args.putParcelable(TAG_ARG, tag);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private void resolvePaste(Paste paste) {
        PasteAdapter pasteAdapter = (PasteAdapter) rvPaste.getAdapter();
        if (filterPaste(paste)) {
            //if a paste is filtered, it means it must be added/set to the adapter
            int index = PasteUtils.findIndexOfItemWithId(pasteAdapter.getPastes(), paste.getId());
            if (index > -1) {
                pasteAdapter.setPaste(index, paste);
            } else {
                pasteAdapter.addPaste(0, paste);
            }
        } else {
            //else it must be removed if it exists
            int index = PasteUtils.findIndexOfItemWithId(pasteAdapter.getPastes(), paste.getId());
            if (index > -1) {
                pasteAdapter.getPastes().remove(index);
                pasteAdapter.notifyItemRemoved(index);
            }
        }
    }

    private boolean filterPaste(Paste paste) {
        String arg = getArguments().getString(ARG1);
        if (arg.equals(getString(R.string.pastes))) {
            return !paste.isArchived();
        }
        if (arg.equals(getString(R.string.archived))) {
            return paste.isArchived();
        }

        for (Tag tag : paste.getTags().values()) {
            if (tag.getLabel().equals(arg) && !paste.isArchived())
                return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pasteReference.addChildEventListener(childEventListener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        pasteReference.removeEventListener(childEventListener);
        if (actionMode != null) {
            actionMode.finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(getString(R.string.key_section), getActivity().getTitle().toString());
        outState.putParcelable(getString(R.string.key_rvos), rvPaste.getLayoutManager().onSaveInstanceState());
        //noinspection unchecked
        outState.putIntegerArrayList(getString(R.string.key_selected_items), (ArrayList<Integer>) ((PasteAdapter) rvPaste.getAdapter()).getSelectedItems());
        outState.putBoolean(getString(R.string.key_action_mode_started), actionMode != null);
        outState.putBoolean(getString(R.string.is_swipe_refreshing), srLayout.isRefreshing());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pastes, container, false);
        ButterKnife.bind(this, rootView);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(Utils.calculateNoOfColumns(getContext()), StaggeredGridLayoutManager.VERTICAL);
        PasteAdapter pasteAdapter = new PasteAdapter(this);

        if (savedInstanceState != null) {
            staggeredGridLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(getString(R.string.key_rvos)));
            List<Integer> positionList = savedInstanceState.getIntegerArrayList(getString(R.string.key_selected_items));
            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
            for (int i = 0; i < positionList.size(); i++) {
                sparseBooleanArray.put(positionList.get(i), true);
            }
            pasteAdapter.setSelectedItems(sparseBooleanArray);
            if (savedInstanceState.getBoolean(getString(R.string.key_action_mode_started))) {
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
                actionMode.setTitle(String.valueOf(sparseBooleanArray.size()));
                actionMode.invalidate();
            }
            srLayout.setRefreshing(savedInstanceState.getBoolean(getString(R.string.is_swipe_refreshing), false));
        }
        rvPaste.setAdapter(pasteAdapter);
        rvPaste.setLayoutManager(staggeredGridLayoutManager);

        srLayout.setOnRefreshListener(this);
        srLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPink, R.color.colorTeal);

        setHasOptionsMenu(true);

        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        //change view based on section
        if (getArguments().getString(ARG1).equals(getString(R.string.pastes))) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorPrimary)));
            fabNewPaste.setOnClickListener(this);

        } else if (getArguments().getString(ARG1).equals(getString(R.string.archived))) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorBlueGrey)));
            fabNewPaste.hide();
        } else {

            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(getContext(), R.color.colorTeal)));
            fabNewPaste.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.colorPink)));
            fabNewPaste.setOnClickListener(this);
        }


        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fabNewPaste:
                startPasteItActivity(new Paste());
                break;
        }
    }

    @Override
    public void onRefresh() {
        srLayout.setRefreshing(true);
        Log.d(TAG, "Single Value listener added.");
        pasteReference.addListenerForSingleValueEvent(valueEventListener);
    }

    @Override
    public void onThumbClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        } else {
            PasteAdapter pasteAdapter = (PasteAdapter) rvPaste.getAdapter();
            startPasteItActivity(pasteAdapter.getPastes().get(position));
        }

    }

    @Override
    public boolean onThumbLongClicked(int position) {
        if (actionMode == null) {
            actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
        return true;
    }

    private void startPasteItActivity(Paste paste) {
        Intent intent = new Intent(getActivity(), PasteItActivity.class);
        Tag tag = getArguments().getParcelable(TAG_ARG);
        if (tag != null)
            paste.getTags().put(tag.getId(), tag);
        intent.putExtra(getString(R.string.key_paste), paste);
        startActivity(intent);
    }

    /**
     * Toggle the selection state of an item.
     * <p>
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        PasteAdapter adapter = (PasteAdapter) rvPaste.getAdapter();
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(String.valueOf(count));
            actionMode.invalidate();
        }
    }


    private class ActionModeCallback implements ActionMode.Callback {
        @SuppressWarnings("unused")
        private final String TAG = ActionModeCallback.class.getSimpleName();


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            if (!getArguments().getString(ARG1).equals(getString(R.string.archived)))
                mode.getMenuInflater().inflate(R.menu.selected_menu, menu);
            else
                mode.getMenuInflater().inflate(R.menu.selected_menu_archived, menu);
            srLayout.setEnabled(false);

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            PasteAdapter pasteAdapter = (PasteAdapter) rvPaste.getAdapter();
            @SuppressWarnings("unchecked") List<Integer> items = pasteAdapter.getSelectedItems();
            switch (item.getItemId()) {
                case R.id.miArchive:
                    for (int i : items) {
                        Paste paste = pasteAdapter.getPastes().get(i);
                        paste.setArchived(true);
                        FirebaseDatabase.getInstance().getReference("pastes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(paste.getId()).setValue(paste);
                    }

                    mode.finish();
                    return true;
                case R.id.miUnArchive:
                    for (int i : items) {
                        Paste paste = pasteAdapter.getPastes().get(i);
                        paste.setArchived(false);
                        FirebaseDatabase.getInstance().getReference("pastes").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .child(paste.getId()).setValue(paste);
                    }
                    mode.finish();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            PasteAdapter adapter = (PasteAdapter) rvPaste.getAdapter();
            adapter.clearSelection();
            srLayout.setEnabled(true);
            actionMode = null;
        }
    }
}
