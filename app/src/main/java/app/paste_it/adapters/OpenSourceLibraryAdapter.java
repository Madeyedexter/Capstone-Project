package app.paste_it.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import app.paste_it.R;
import app.paste_it.models.OpenSourceLibrary;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Madeyedexter on 31-05-2017.
 */

public class OpenSourceLibraryAdapter extends RecyclerView.Adapter {

    private List<OpenSourceLibrary> libraries = new ArrayList<>();

    public OpenSourceLibraryAdapter(List<OpenSourceLibrary> libraries){
        this.libraries=libraries;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new OpenSourceLibraryHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_open_source_library,parent,false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        OpenSourceLibraryHolder openSourceLibraryHolder = (OpenSourceLibraryHolder) holder;
        openSourceLibraryHolder.bindData(libraries.get(position));
    }

    @Override
    public int getItemCount() {
        return libraries==null?0:libraries.size();
    }

    public class OpenSourceLibraryHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tvName)
        TextView tvName;
        @BindView(R.id.tvCopyright)
        TextView tvCopyright;
        @BindView(R.id.tvLicense)
        TextView tvLicense;
        @BindView(R.id.tvWebsite)
        TextView tvWebsite;

        public OpenSourceLibraryHolder(View rootView){
            super(rootView);
            ButterKnife.bind(this,rootView);
        }

        public void bindData(OpenSourceLibrary openSourceLibrary){
            tvName.setText(openSourceLibrary.getLibraryName());
            tvCopyright.setText(openSourceLibrary.getCopyright());
            tvLicense.setText(String.format(tvLicense.getContext().getString(R.string.license),openSourceLibrary.getLicense()));
            tvWebsite.setText(String.format(tvWebsite.getContext().getString(R.string.website),openSourceLibrary.getWebsite()));
        }
    }

}
