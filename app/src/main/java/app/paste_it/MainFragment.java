package app.paste_it;


import android.app.SearchManager;
import android.content.Context;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;

import java.util.List;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import app.paste_it.adapters.PasteAdapter;
import app.paste_it.models.greendao.DaoSession;
import app.paste_it.models.greendao.Paste;
import app.paste_it.models.greendao.PasteDao;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A fragment with a Google +1 button.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Paste>>, View.OnClickListener, PasteAdapter.ThumbClickListener, SharedPreferences.OnSharedPreferenceChangeListener{

    private static final String TAG = MainFragment.class.getSimpleName();
    private static final int ID_PASTE_LOADER = 0;

    //views
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rvPaste)
    RecyclerView rvPaste;
    @BindView(R.id.fabNewPaste)
    FloatingActionButton fabNewPaste;

    private DaoSession daoSession;


    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daoSession = ((PasteItApplication)getActivity().getApplication()).getDaoSession();
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);

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
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

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
    public Loader<List<Paste>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<Paste>>(MainFragment.this.getContext()) {
            @Override
            public List<Paste> loadInBackground() {
                PasteDao pasteDao = daoSession.getPasteDao();
                return pasteDao.loadAll();
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Paste>> loader, List<Paste> data) {
        PasteAdapter pasteAdapter = ((PasteAdapter)rvPaste.getAdapter());
        pasteAdapter.setLoading(false);
        pasteAdapter.setEnded(true);
        pasteAdapter.setPastes(data);

    }

    @Override
    public void onLoaderReset(Loader<List<Paste>> loader) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getSupportLoaderManager().destroyLoader(ID_PASTE_LOADER);
        PreferenceManager.getDefaultSharedPreferences(getContext()).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fabNewPaste: startActivity(new Intent(getActivity(),PasteItActivity.class));
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(ID_PASTE_LOADER,null,this).forceLoad();
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
        if(key.equals(getString(R.string.key_paste_added))){
            Paste paste = daoSession.getPasteDao().load(sharedPreferences.getString(key,null));
            ((PasteAdapter)rvPaste.getAdapter()).addPaste(0,paste);
        }
    }
}
