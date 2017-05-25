package app.paste_it;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

public class MainActivity extends AppCompatActivity implements
        DrawerLayout.DrawerListener, View.OnClickListener,
        PasteAdapter.ThumbClickListener, SharedPreferences.OnSharedPreferenceChangeListener,
        SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<List<ImageModel>> {
    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int ID_IMODEL_LOADER = 0;
    private static final int ID_PASTE_REFRESH_LOADER = 1;

    //views
    @BindView(R.id.rvPaste)
    RecyclerView rvPaste;
    @BindView(R.id.srLayout)
    SwipeRefreshLayout srLayout;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.sv)
    SearchView searchView;
    private List<ImageModel> imageModels;
    private DatabaseReference pasteReference = FirebaseDatabase.getInstance().getReference("pastes/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<Paste> pastes = new ArrayList<>();
            for (DataSnapshot dataSnapshotChild : dataSnapshot.getChildren()) {
                pastes.add(dataSnapshotChild.getValue(Paste.class));
            }
            Collections.sort(pastes, new Comparator<Paste>() {
                @Override
                public int compare(Paste o1, Paste o2) {
                    return o2.getId().compareTo(o1.getId());
                }
            });
            ((PasteAdapter) rvPaste.getAdapter()).setPastes(pastes);
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
            Log.d(TAG, "String: " + s + "\nAdded Snapshot: " + dataSnapshot);
            Paste paste = dataSnapshot.getValue(Paste.class);
            PasteUtils.resolvePaste(MainActivity.this, paste, (PasteAdapter) rvPaste.getAdapter());
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Log.d(TAG, "String: " + s + "\nChanged Snapshot: " + dataSnapshot);
            Paste paste = dataSnapshot.getValue(Paste.class);
            PasteUtils.resolvePaste(MainActivity.this, paste, (PasteAdapter) rvPaste.getAdapter());
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
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        drawerLayout.addDrawerListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_closed);
        actionBarDrawerToggle.setDrawerSlideAnimationEnabled(true);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        setTitle("Paste It!");
//        fabNewPaste.setOnClickListener(this);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(Utils.calculateNoOfColumns(this), StaggeredGridLayoutManager.VERTICAL);

        srLayout.setOnRefreshListener(this);
        srLayout.setColorSchemeResources(R.color.colorPrimary, R.color.colorAccent);
        if (savedInstanceState != null) {
            Parcelable rvos = savedInstanceState.getParcelable(getString(R.string.key_rvos));
            staggeredGridLayoutManager.onRestoreInstanceState(rvos);
            rvPaste.setLayoutManager(staggeredGridLayoutManager);
            rvPaste.setAdapter(new PasteAdapter(this));
        } else {
            rvPaste.setLayoutManager(staggeredGridLayoutManager);
            rvPaste.setAdapter(new PasteAdapter(this));
        }

        pasteReference.orderByKey().addListenerForSingleValueEvent(valueEventListener);
        srLayout.setRefreshing(true);

        Utils.verifyStoragePermissions(this);
        Utils.verifyManageDocumentsPermissions(this);

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.miSearch:
                return true;
            case R.id.miLogOut:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addSearchFragment() {
        SearchFragment searchFragment = SearchFragment.newInstance();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frame, searchFragment).commit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onThumbClicked(Paste paste) {
        startPasteItActivity(paste);
    }

    private void startPasteItActivity(Paste paste) {
        Intent intent = new Intent(this, PasteItActivity.class);
        intent.putExtra(getString(R.string.key_paste), paste);
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Parcelable rvos = rvPaste.getLayoutManager().onSaveInstanceState();
        outState.putParcelable(getString(R.string.key_rvos), rvos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        actionBarDrawerToggle.onConfigurationChanged(newConfig);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utils.REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.d(TAG,"Permission Granted");

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_image_model_updated))) {
            Log.d(TAG, "Preferences changed with key: " + key);
            //if we are still here, the user has imported the images and doing some other task
            //The images have been loaded locally, but not yet uploaded to the cloud
            Gson gson = new Gson();
            if (key.equals(getString(R.string.key_image_model_updated))) {
                String jsonString = sharedPreferences.getString(key, "");
                ImageModel imageModel = gson.fromJson(jsonString, ImageModel.class);
                int index = PasteUtils.findIndex(imageModels, imageModel);
                if (index > -1) {
                    imageModels.set(index, imageModel);
                    PasteAdapter pasteAdapter = (PasteAdapter) rvPaste.getAdapter();
                    int pasteIndex = PasteUtils.findIndexOfPaste(pasteAdapter.getPastes(), imageModel.getPasteId());
                    pasteAdapter.notifyItemChanged(pasteIndex);
                }
            }
            if (key.equals(getString(R.string.key_dload_uri_available))) {
                String jsonString = sharedPreferences.getString(key, "");
                ImageModel imageModel = gson.fromJson(jsonString, ImageModel.class);
                int index = PasteUtils.findIndex(imageModels, imageModel);
                if (index > -1) {
                    imageModels.set(index, imageModel);
                    PasteAdapter pasteAdapter = (PasteAdapter) rvPaste.getAdapter();
                    int pasteIndex = PasteUtils.findIndexOfPaste(pasteAdapter.getPastes(), imageModel.getPasteId());
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
        return new AsyncTaskLoader<List<ImageModel>>(this) {
            private DaoSession daoSession;
            private ImageModelDao imageModelDao;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                daoSession = ((PasteItApplication) getApplication()).getDaoSession();
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

    @Override
    public void onStart() {
        super.onStart();
        pasteReference.addChildEventListener(childEventListener);
        getSupportLoaderManager().restartLoader(ID_IMODEL_LOADER, null, this);

        //upload all pending images
        Intent intent = new Intent(this, ImageUploadService.class);
        intent.setAction(ImageUploadService.ACTION_IMAGE_UPLOAD);
        startService(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
        pasteReference.removeEventListener(childEventListener);
        pasteReference.removeEventListener(valueEventListener);
        getSupportLoaderManager().destroyLoader(ID_IMODEL_LOADER);
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
