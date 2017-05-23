package app.paste_it;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
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
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.paste_it.adapters.PasteAdapter;
import app.paste_it.models.DaoSession;
import app.paste_it.models.ImageModel;
import app.paste_it.models.ImageModelDao;
import app.paste_it.models.Paste;
import app.paste_it.service.ImageUploadService;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment with a Google +1 button.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements View.OnClickListener,
        PasteAdapter.ThumbClickListener, SharedPreferences.OnSharedPreferenceChangeListener,
        SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<ImageModel>>

{

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final int ID_IMODEL_LOADER = 0;
    private static final int ID_PASTE_REFRESH_LOADER = 1;

    //views
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rvPaste)
    RecyclerView rvPaste;
    @BindView(R.id.fabNewPaste)
    FloatingActionButton fabNewPaste;
    @BindView(R.id.srLayout)
    SwipeRefreshLayout srLayout;

    private List<ImageModel> imageModels;

    private DatabaseReference pasteReference = FirebaseDatabase.getInstance().getReference("pastes/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());

    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<Paste> pastes = new ArrayList<>();
            for(DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()){
                pastes.add(dataSnapshotChild.getValue(Paste.class));
            }
            Collections.sort(pastes, new Comparator<Paste>() {
                @Override
                public int compare(Paste o1, Paste o2) {
                    return o2.getId().compareTo(o1.getId());
                }
            });
            ((PasteAdapter)rvPaste.getAdapter()).setPastes(pastes);
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

            //This callback will be fired for each paste,
            Log.d(TAG,"String: "+s+"\nAdded Snapshot: "+dataSnapshot);
            Paste paste = dataSnapshot.getValue(Paste.class);
            PasteUtils.resolvePaste(getContext(),paste,(PasteAdapter) rvPaste.getAdapter());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG,"String: "+s+"\nChanged Snapshot: "+dataSnapshot);
            Paste paste = dataSnapshot.getValue(Paste.class);
            PasteUtils.resolvePaste(getContext(),paste,(PasteAdapter) rvPaste.getAdapter());

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Log.d(TAG,"Removed Snapshot: "+dataSnapshot);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };



    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        pasteReference.addChildEventListener(childEventListener);
        getActivity().getSupportLoaderManager().restartLoader(ID_IMODEL_LOADER,null,this);
        Intent intent  = new Intent(getActivity(), ImageUploadService.class);
        intent.setAction(ImageUploadService.ACTION_IMAGE_UPLOAD);
        getActivity().startService(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        pasteReference.removeEventListener(childEventListener);
        pasteReference.removeEventListener(valueEventListener);
        getActivity().getSupportLoaderManager().destroyLoader(ID_IMODEL_LOADER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this,view);
        AppCompatActivity activity = ((AppCompatActivity)getActivity());
        activity.setSupportActionBar(mToolbar);
        activity.setTitle("Paste It!");
        setHasOptionsMenu(true);

        fabNewPaste.setOnClickListener(this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(Utils.calculateNoOfColumns(getContext()), StaggeredGridLayoutManager.VERTICAL);

        srLayout.setOnRefreshListener(this);
        srLayout.setColorSchemeResources(R.color.colorPrimary,R.color.colorAccent);

        if(savedInstanceState!= null){
            Parcelable rvos = savedInstanceState.getParcelable("rvos");
            staggeredGridLayoutManager.onRestoreInstanceState(rvos);
            rvPaste.setLayoutManager(staggeredGridLayoutManager);
            rvPaste.setAdapter(new PasteAdapter(this));
        }
        else{
            rvPaste.setLayoutManager(staggeredGridLayoutManager);
            rvPaste.setAdapter(new PasteAdapter(this));
        }

        pasteReference.orderByKey().addListenerForSingleValueEvent(valueEventListener);
        srLayout.setRefreshing(true);

        Utils.verifyStoragePermissions(getActivity());
        Utils.verifyManageDocumentsPermissions(getActivity());
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main_fragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG,"Option Selected");
        switch (item.getItemId()){
            case R.id.miSearch: addSearchFragment();
                return true;
            case R.id.miLogOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity(),LoginActivity.class));
                getActivity().finish();
                return true;
            default:return super.onOptionsItemSelected(item);
        }

    }

    private void addSearchFragment() {
        SearchFragment searchFragment = SearchFragment.newInstance();
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frame,searchFragment).commit();
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabNewPaste:
                Intent intent = new Intent(getContext(),PasteItActivity.class);
                intent.putExtra(getString(R.string.key_paste),new Paste());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onThumbClicked(Paste paste) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Parcelable rvos = rvPaste.getLayoutManager().onSaveInstanceState();
        outState.putParcelable("rvos",rvos);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.equals(getString(R.string.key_image_model_updated))){
            Log.d(TAG,"Preferences changed with key: "+key);
            //if we are still here, the user has imported the images and doing some other task
            //The images have been loaded locally, but not yet uploaded to the cloud
            Gson gson = new Gson();
            if(key.equals(getString(R.string.key_image_model_updated))){
                String jsonString = sharedPreferences.getString(key,"");
                ImageModel imageModel = gson.fromJson(jsonString,ImageModel.class);
                int index = PasteUtils.findIndex(imageModels,imageModel);
                if(index > -1){
                    imageModels.set(index,imageModel);
                    PasteAdapter pasteAdapter = (PasteAdapter)rvPaste.getAdapter();
                    int pasteIndex = PasteUtils.findIndexOfPaste(pasteAdapter.getPastes(),imageModel.getPasteId());
                    pasteAdapter.notifyItemChanged(pasteIndex);
                }
            }
            if(key.equals(getString(R.string.key_dload_uri_available))){
                String jsonString = sharedPreferences.getString(key,"");
                ImageModel imageModel = gson.fromJson(jsonString,ImageModel.class);
                int index = PasteUtils.findIndex(imageModels,imageModel);
                if(index > -1){
                    imageModels.set(index,imageModel);
                    PasteAdapter pasteAdapter = (PasteAdapter)rvPaste.getAdapter();
                    int pasteIndex = PasteUtils.findIndexOfPaste(pasteAdapter.getPastes(),imageModel.getPasteId());
                    pasteAdapter.notifyItemChanged(pasteIndex);
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        srLayout.setRefreshing(true);
        pasteReference.addListenerForSingleValueEvent(valueEventListener);

    }

    @Override
    public Loader<List<ImageModel>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<ImageModel>>(getActivity()) {
            private DaoSession daoSession;
            private ImageModelDao imageModelDao;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                daoSession = ((PasteItApplication)getActivity().getApplication()).getDaoSession();
                imageModelDao = daoSession.getImageModelDao();
            }

            @Override
            public List<ImageModel> loadInBackground() {
                return imageModelDao.loadAll();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<ImageModel>> loader, List<ImageModel> data) {
        imageModels = data;
    }

    @Override
    public void onLoaderReset(Loader<List<ImageModel>> loader) {

    }
}
