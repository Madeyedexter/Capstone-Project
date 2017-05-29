package app.paste_it;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import app.paste_it.models.Paste;
import butterknife.ButterKnife;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {


    private static final String ARG1 = "ARG1";

    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(Paste paste) {
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARG1, paste);
        detailFragment.setArguments(bundle);
        return detailFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }


    }
}
