package app.paste_it;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filterable;

import java.util.Arrays;
import java.util.List;

import app.paste_it.adapters.PasteAdapter;
import app.paste_it.models.Paste;
import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment implements View.OnClickListener, PasteAdapter.ThumbClickListener, SearchView.OnQueryTextListener{

    private static final int ID_LOADER_SEARCH_RESULT = 1;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.svPaste)
    SearchView svPaste;
    @BindView(R.id.rvSearchResults)
    RecyclerView rvSearchResult;


    public SearchFragment() {
        // Required empty public constructor
    }

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_search, container, false);
        ButterKnife.bind(this, rootView);
        AppCompatActivity appCompatActivity = (AppCompatActivity)getActivity();
        appCompatActivity.setSupportActionBar(mToolbar);
        appCompatActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setHasOptionsMenu(true);

        EditText editText = (EditText) svPaste.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        editText.setTextColor(ContextCompat.getColor(getContext(),R.color.white));
        svPaste.setOnQueryTextListener(this);
        rvSearchResult.setAdapter(new PasteAdapter(this));

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_search_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        }
    }

    @Override
    public void onThumbClicked(Paste paste) {
        updateRecentSearch(paste);
        viewPaste(paste);
    }

    private void viewPaste(Paste paste) {

    }

    private void updateRecentSearch(Paste paste) {
        //clicked on a search item, save the id of the paste as a recently seacrhed paste.
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        List<String> recentSearches = Arrays.asList((sharedPreferences.getString(getString(R.string.key_recent_searches),"").split(",")));
        //if size > 5, retrieve last 4 searches
        recentSearches = recentSearches.size() > 4 ? recentSearches.subList(1, recentSearches.size()) : recentSearches;
        //add this search
        recentSearches.add(0,paste.getId());
        //join the ids
        String newRecentSearches = TextUtils.join(",",recentSearches);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.key_recent_searches),newRecentSearches);
        editor.apply();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Filterable filterable = (Filterable) rvSearchResult.getAdapter();
        filterable.getFilter().filter(newText);
        return false;
    }
}
