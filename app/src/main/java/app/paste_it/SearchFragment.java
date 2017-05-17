package app.paste_it;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.ArrayList;

import app.paste_it.models.firebase.Paste;
import app.paste_it.models.greendao.DaoSession;
import app.paste_it.models.greendao.PasteDao;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<Paste>>, View.OnClickListener{

    private static final int ID_LOADER_SEARCH_RESULT = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.etSearch)
    EditText etSearch;
    @BindView(R.id.ibClear)
    ImageButton ibClear;

    public SearchFragment() {
        // Required empty public constructor
    }


    private DaoSession daoSession;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }

        daoSession = ((PasteItApplication)getActivity().getApplication()).getDaoSession();

        getActivity().getSupportLoaderManager().initLoader(ID_LOADER_SEARCH_RESULT,null, this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getSupportLoaderManager().destroyLoader(ID_LOADER_SEARCH_RESULT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<ArrayList<Paste>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<Paste>>(getContext()) {
            @Override
            public ArrayList<Paste> loadInBackground() {
                ArrayList<Paste> pastes = new ArrayList<>();
                PasteDao pasteDao = daoSession.getPasteDao();
                //TODO: load based on search string

                return pastes;
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Paste>> loader, ArrayList<Paste> data) {

    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Paste>> loader) {

    }

    @Override
    public void onClick(View v) {
        //TODO: handle text clear action
    }
}
