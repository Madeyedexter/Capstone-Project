package app.paste_it;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import app.paste_it.adapters.DrawerAdapter;
import app.paste_it.models.Tag;
import app.paste_it.service.ImageUploadService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        DrawerLayout.DrawerListener, View.OnClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    //views
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rvLeftDrawer)
    RecyclerView rvLeftDrawer;

    private DatabaseReference userTagsDbReference = FirebaseDatabase.getInstance().getReference("tags").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    private ChildEventListener userTagsChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Tag tag = dataSnapshot.getValue(Tag.class);
            DrawerAdapter drawerAdapter = (DrawerAdapter) rvLeftDrawer.getAdapter();
            drawerAdapter.getItems().add(tag);
            drawerAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Tag tag = dataSnapshot.getValue(Tag.class);
            DrawerAdapter drawerAdapter = (DrawerAdapter) rvLeftDrawer.getAdapter();
            int index = PasteUtils.findIndexOfItemWithId(drawerAdapter.getItems(), tag.getId());
            if (index > -1) {
                drawerAdapter.getItems().set(index, tag);
                drawerAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Tag tag = dataSnapshot.getValue(Tag.class);
            DrawerAdapter drawerAdapter = (DrawerAdapter) rvLeftDrawer.getAdapter();
            int index = PasteUtils.findIndexOfItemWithId(drawerAdapter.getItems(), tag.getId());
            drawerAdapter.getItems().remove(index);
            drawerAdapter.notifyDataSetChanged();
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

        setSupportActionBar(toolbar);

        drawerLayout.addDrawerListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed);
        actionBarDrawerToggle.setDrawerSlideAnimationEnabled(true);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        String sectionText = getString(R.string.pastes);
        int selectedPosition = 1;

        if (savedInstanceState != null) {
            linearLayoutManager.onRestoreInstanceState(savedInstanceState.getParcelable(getString(R.string.rvOsDrawer)));
            sectionText = savedInstanceState.getString(getString(R.string.key_section));
            selectedPosition = savedInstanceState.getInt(getString(R.string.selection_postion));
        } else {
            PastesFragment pastesFragment = PastesFragment.newInstance(sectionText, null);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, pastesFragment).commit();
        }
        setTitle(sectionText);
        rvLeftDrawer.setLayoutManager(linearLayoutManager);
        DrawerAdapter drawerAdapter = new DrawerAdapter(this);
        rvLeftDrawer.setAdapter(drawerAdapter);
        drawerAdapter.setSelectionPosition(selectedPosition);
        drawerAdapter.notifyDataSetChanged();

        userTagsDbReference.addChildEventListener(userTagsChildEventListener);


        Utils.verifyStoragePermissions(this);
        Utils.verifyManageDocumentsPermissions(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        userTagsDbReference.removeEventListener(userTagsChildEventListener);
    }

    @Override
    public void onClick(View v) {
        final String sectionText = ((TextView) v.findViewById(R.id.tvSectionText)).getText().toString();
        if(sectionText.equals(getString(R.string.settings))){
            launchSettingsActivity();
            return;
        }
        if(sectionText.equals(getString(R.string.about))){
            launchAboutActivity();
            return;
        }
        switch (v.getId()) {
            default:

                final int position = Integer.parseInt(v.getTag(R.string.selection_postion).toString());
                DrawerAdapter drawerAdapter = ((DrawerAdapter) rvLeftDrawer.getAdapter());
                if (position != drawerAdapter.getSelectionPosition() && drawerAdapter.getItemViewType(position) == DrawerAdapter.VIEW_TYPE_SECTION) {
                    drawerAdapter.setSelectionPosition(position);
                    drawerAdapter.notifyDataSetChanged();
                    final String text = ((TextView) v.findViewById(R.id.tvSectionText)).getText().toString();
                    setTitle(text);

                    Tag tag = (Tag) v.getTag(R.string.tag);
                    handleDrawerSectionSelection(tag);
                }
        }
        drawerLayout.closeDrawer(Gravity.START, true);
    }

    private void handleDrawerSectionSelection(@Nullable Tag tag) {
        String sectionText = getTitle().toString();
        if (sectionText.equals(getString(R.string.pastes))) {
            PastesFragment pastesFragment = PastesFragment.newInstance(getString(R.string.pastes), null);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, pastesFragment).commit();
        } else if (sectionText.equals(getString(R.string.archived))) {
            PastesFragment pastesFragment = PastesFragment.newInstance(getString(R.string.archived), null);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, pastesFragment).commit();
        } else {
            PastesFragment pastesFragment = PastesFragment.newInstance(sectionText, tag);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, pastesFragment).commit();
        }

    }

    private void launchAboutActivity() {
        //TODO: Launch About Activity
    }

    private void launchSettingsActivity() {
        Intent intent  = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.rvOsDrawer), rvLeftDrawer.getLayoutManager().onSaveInstanceState());
        outState.putInt(getString(R.string.selection_postion), ((DrawerAdapter) rvLeftDrawer.getAdapter()).getSelectionPosition());
        outState.putString(getString(R.string.key_section), getTitle().toString());
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
                    Log.d(TAG, "Permission Granted");

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
    public void onStart() {
        super.onStart();
        //upload all pending images
        Intent intent = new Intent(this, ImageUploadService.class);
        intent.setAction(ImageUploadService.ACTION_IMAGE_UPLOAD);
        startService(intent);
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        //handleDrawerSectionSelection();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
