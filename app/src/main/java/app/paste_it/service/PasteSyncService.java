package app.paste_it.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import app.paste_it.MainActivity;
import app.paste_it.PasteItApplication;
import app.paste_it.PasteUtils;
import app.paste_it.R;
import app.paste_it.Utils;
import app.paste_it.models.greendao.Paste;
import app.paste_it.models.greendao.PasteDao;

/**
 * Created by madeyedexter on 5/18/2017.
 */

public class PasteSyncService extends IntentService {

    private static final String TAG = PasteSyncService.class.getSimpleName();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private DatabaseReference pasteReference = FirebaseDatabase.getInstance().getReference("pastes/" + firebaseUser.getUid());
    private CountDownLatch countDownLatch;
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<app.paste_it.models.firebase.Paste> pastes = dataSnapshot.getValue(new GenericTypeIndicator<List<app.paste_it.models.firebase.Paste>>() {
            });
            Log.d(TAG, pastes.toString());
            //we need all items other than the first item
            //TODO: uncomment the lines after appropriate testing
            PasteDao pasteDao = ((PasteItApplication) getApplication()).getDaoSession().getPasteDao();
            pasteDao.saveInTx(Utils.toGreenDaoPasteList(pastes).subList(1, pastes.size()));
            //notify via shared preferences that multiple pastes have been added
            String ids = PasteUtils.getPasteIds(pastes);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PasteSyncService.this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(getString(R.string.key_pastes_added), ids);
            //editor.commit();
            if (countDownLatch != null)
                countDownLatch.countDown();
            Log.d(TAG, "All Opeartions Completed.");
            MainActivity.PASTES_SYNCED = true;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public PasteSyncService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //there won't be any data in the intent, we query firebase for items
        //not present in our local cache

        //One operation to complete, the firebase callback
        countDownLatch = new CountDownLatch(1);
        PasteDao pasteDao = ((PasteItApplication) getApplication()).getDaoSession().getPasteDao();
        Paste paste = pasteDao.queryBuilder().orderDesc(PasteDao.Properties.Id).limit(1).build().list().get(0);
        pasteReference.startAt(paste.getId()).addListenerForSingleValueEvent(valueEventListener);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
