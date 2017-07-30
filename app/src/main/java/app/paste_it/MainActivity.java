package app.paste_it;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import app.paste_it.models.Tag;
import app.paste_it.models.UIProps;
import app.paste_it.service.ImageUploadService;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DrawerLayout.DrawerListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    //views

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_navigation_view)
    NavigationView navigationView;

    //unable to bind these views with ButterKnife
    CircleImageView ivUserPic;
    TextView tvUsername;
    TextView tvPasteCount;

    //a map to maintain tags information
    private Map<Integer,Tag> tagsMap = new HashMap<>();
    private UIProps uiProps;


    MenuItem tagMenus;

    private DatabaseReference userTagsDbReference = FirebaseDatabase.getInstance().getReference("tags").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private ChildEventListener userTagsChildEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            Tag tag = dataSnapshot.getValue(Tag.class);
            SubMenu subMenu = tagMenus.getSubMenu();
            MenuItem menuItem = subMenu.add(R.id.drawer_item_tag_group,tag.getId().hashCode(),Menu.NONE,tag.getLabel());
            menuItem.setCheckable(true);
            menuItem.setIcon(R.drawable.ic_label_black_24dp);
            tagsMap.put(tag.getId().hashCode(),tag);
            menuItem.setChecked(menuItem.getItemId()==uiProps.getKeySelectedDrawerTag());

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            Tag tag = dataSnapshot.getValue(Tag.class);
            SubMenu subMenu = tagMenus.getSubMenu();
            MenuItem menuItem = subMenu.findItem(tag.getId().hashCode());
            menuItem.setTitle(tag.getLabel());
            tagsMap.put(tag.getId().hashCode(),tag);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            Tag tag = dataSnapshot.getValue(Tag.class);
            SubMenu subMenu = tagMenus.getSubMenu();
            subMenu.removeItem(tag.getId().hashCode());
            tagsMap.remove(tag.getId().hashCode());
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    };
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private NavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            PastesFragment pastesFragment;
            switch (item.getItemId()){
                case R.id.drawer_item_pastes:
                    unCheckTagMenuItems();
                    item.setChecked(true);
                    setTitle(getString(R.string.pastes));
                    uiProps.setKeySelectedDrawerTag(-1);
                    pastesFragment = PastesFragment.newInstance(getString(R.string.pastes), null);
                    drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.colorPrimaryDark));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, pastesFragment).commit();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.drawer_item_archived: item.setChecked(true);
                    unCheckTagMenuItems();
                    setTitle(getString(R.string.archived));
                    uiProps.setKeySelectedDrawerTag(-1);
                    pastesFragment = PastesFragment.newInstance(getString(R.string.archived), null);
                    drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.md_blue_grey_700_color_code));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, pastesFragment).commit();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.drawer_item_settings:
                    launchSettingsActivity();
                    drawerLayout.closeDrawers();
                    return true;
                case R.id.drawer_item_about:
                    launchAboutActivity();
                    drawerLayout.closeDrawers();
                    return true;
                default:
                    unCheckTagMenuItems();
                    item.setChecked(true);
                    Tag tag = tagsMap.get(item.getItemId());
                    setTitle(tag.getLabel());
                    pastesFragment = PastesFragment.newInstance(tag.getLabel(), tag);
                    drawerLayout.setStatusBarBackgroundColor(ContextCompat.getColor(MainActivity.this,R.color.md_teal_700_color_code));
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame, pastesFragment).commit();
                    drawerLayout.closeDrawers();
                    uiProps.setKeySelectedDrawerTag(item.getItemId());
                    return true;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        tagMenus = navigationView.getMenu().getItem(2);

        setSupportActionBar(toolbar);

        initUIProps(savedInstanceState);
        initDrawer();


        if (savedInstanceState == null) {
            PastesFragment pastesFragment = PastesFragment.newInstance(uiProps.getSectionText(), null);
            getSupportFragmentManager().beginTransaction().replace(R.id.frame, pastesFragment).commit();
            //check the first item of the navigation view
            navigationView.getMenu().getItem(0).setChecked(true);
        }
        userTagsDbReference.addChildEventListener(userTagsChildEventListener);



        Utils.verifyStoragePermissions(this);
    }

    private void initDrawer() {
        View headerView = navigationView.getHeaderView(0);
        ivUserPic = (CircleImageView) headerView.findViewById(R.id.ivUserpic);
        tvUsername = (TextView) headerView.findViewById(R.id.tvUsername);
        tvPasteCount = (TextView) headerView.findViewById(R.id.tvPasteCount);
        if(firebaseUser!=null){
            Picasso.with(this).load(firebaseUser.getPhotoUrl()).into(ivUserPic);
            tvUsername.setText(firebaseUser.getDisplayName());
            FirebaseDatabase.getInstance().getReference("totals").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String totalCount  = dataSnapshot.child("totalCount").getValue()!=null?dataSnapshot.child("totalCount").getValue().toString():"0";
                    String totalArchivedCount  = dataSnapshot.child("totalArchivedCount").getValue()!=null?dataSnapshot.child("totalArchivedCount").getValue().toString():"0";
                    tvPasteCount.setText(getString(R.string.paste_count,totalCount,totalArchivedCount));
                    tvPasteCount.setVisibility(View.VISIBLE);
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_closed);
        //actionBarDrawerToggle.setDrawerSlideAnimationEnabled(true);
        //actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        toolbar.setNavigationIcon(new DrawerArrowDrawable(this));

        drawerLayout.addDrawerListener(this);
        tagMenus.getSubMenu().setGroupCheckable(R.id.drawer_item_tag_group,true,true);
        navigationView.setNavigationItemSelectedListener(onNavigationItemSelectedListener);
    }

    private void initUIProps(Bundle savedInstanceState){
        if(savedInstanceState==null) {
            uiProps = new UIProps();
            uiProps.setStatusBarBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
            uiProps.setSectionText(getString(R.string.pastes));
        }
        else
            uiProps = savedInstanceState.getParcelable(getString(R.string.key_ui_props));
        setTitle(uiProps.getSectionText());
        drawerLayout.setStatusBarBackgroundColor(uiProps.getStatusBarBackgroundColor());
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
            drawerLayout.closeDrawer(Gravity.START,true);
            return;
        }
        if(sectionText.equals(getString(R.string.about))){
            launchAboutActivity();
            drawerLayout.closeDrawer(Gravity.START,true);
            return;
        }
        switch (v.getId()) {
            default:
        }
        drawerLayout.closeDrawer(Gravity.START, true);
    }

    private void launchAboutActivity() {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    private void launchSettingsActivity() {
        Intent intent  = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        uiProps.setStatusBarBackgroundColor(((ColorDrawable)drawerLayout.getStatusBarBackgroundDrawable()).getColor());
        outState.putParcelable(getString(R.string.key_ui_props),uiProps);
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

                }
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

    private void unCheckTagMenuItems(){
        SubMenu subMenu = tagMenus.getSubMenu();
        for(int i=0;i<subMenu.size();i++){
            subMenu.getItem(i).setChecked(false);
        }
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onDrawerOpened(View drawerView) {
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onDrawerClosed(View drawerView) {
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
