package app.paste_it.models.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
        FirebaseDatabase.getInstance().getReference("totals").child(firebaseUser.getUid()).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String totalCount = dataSnapshot.child("totalCount").getValue().toString();
                        String totalArchivedCount = dataSnapshot.child("totalArchivedCount").getValue().toString();
                        tvPasteCount.setText(tvPasteCount.getContext().getString(R.string.paste_count,totalCount,totalArchivedCount));
                        tvPasteCount.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

    }
}
