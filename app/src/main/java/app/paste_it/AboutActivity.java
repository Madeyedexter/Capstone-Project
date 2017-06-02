package app.paste_it;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import app.paste_it.adapters.OpenSourceLibraryAdapter;
import app.paste_it.models.OpenSourceLibrary;
import butterknife.BindView;
import butterknife.ButterKnife;

public class AboutActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<OpenSourceLibrary>>{

    @BindView(R.id.rvOpenSourceContribution)
    RecyclerView rvOpenSourceContribution;

    private static final int LIBRARY_LOADER_ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        rvOpenSourceContribution.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        if(savedInstanceState!=null){
            rvOpenSourceContribution.getLayoutManager().onRestoreInstanceState(savedInstanceState.getParcelable(getString(R.string.key_ll_os)));
        }

        getSupportLoaderManager().restartLoader(LIBRARY_LOADER_ID,null,this).forceLoad();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(getString(R.string.key_ll_os),rvOpenSourceContribution.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        getSupportLoaderManager().destroyLoader(LIBRARY_LOADER_ID);
        super.onDestroy();
    }

    @Nullable
    @Override
    public Loader<List<OpenSourceLibrary>> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<List<OpenSourceLibrary>>(this) {
            @Override
            public List<OpenSourceLibrary> loadInBackground() {
                AssetManager assetManager = getAssets();
                InputStream inputStream=null;
                try {
                    inputStream = assetManager.open("open_source_contrib_list.json");
                    Gson gson = new GsonBuilder().create();
                    Reader reader = new InputStreamReader(inputStream);
                    Type type = new TypeToken<List<OpenSourceLibrary>>(){}.getType();
                    return gson.fromJson(reader,type);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                finally {
                    if(inputStream!=null){
                        try {
                            inputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<OpenSourceLibrary>> loader, List<OpenSourceLibrary> data) {
        rvOpenSourceContribution.setAdapter(new OpenSourceLibraryAdapter(data));
    }

    @Override
    public void onLoaderReset(Loader<List<OpenSourceLibrary>> loader) {
    }
}
