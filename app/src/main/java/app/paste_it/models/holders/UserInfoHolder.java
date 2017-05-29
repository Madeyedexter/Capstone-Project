package app.paste_it.models.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

import app.paste_it.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 834619 on 5/25/2017.
 */

public class UserInfoHolder extends RecyclerView.ViewHolder {

    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    @BindView(R.id.ivUserpic)
    CircleImageView ivUserpic;
    @BindView(R.id.tvUsername)
    TextView tvUsername;
    @BindView(R.id.tvPasteCount)
    TextView tvPasteCount;

    public UserInfoHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }

    public void bindData() {
        tvUsername.setText(firebaseUser.getDisplayName());
        Picasso.with(ivUserpic.getContext()).load(firebaseUser.getPhotoUrl()).into(ivUserpic);
    }
}
