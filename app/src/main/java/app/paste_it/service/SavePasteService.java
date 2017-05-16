package app.paste_it.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.prefs.Preferences;

import app.paste_it.PasteItApplication;
import app.paste_it.R;
import app.paste_it.Utils;
import app.paste_it.models.firebase.Paste;
import app.paste_it.models.greendao.DaoSession;
import app.paste_it.models.greendao.ImageURL;
import app.paste_it.models.greendao.PasteDao;

/**
 * Created by Madeyedexter on 14-05-2017.
 */

public class SavePasteService extends IntentService {

    private static final String TAG = SavePasteService.class.getSimpleName();

    private FirebaseStorage mFirebaseStorage;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseUser mFireBaseUser;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMddyyyy");

    private CountDownLatch countDownLatch;
    private CountDownLatch greenDaoLatch;

    private DaoSession daoSession;

    //private OnFailureListener onFailureListener =;

    /*private OnSuccessListener onSuccessListener = new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot o) {
            @SuppressWarnings("VisibleForTests")
            Uri downloadUri = o.getDownloadUrl();
            countDownLatch.countDown();
            Log.d(TAG,"Task Success: " + downloadUri);
        }
    };

*/
    public SavePasteService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PasteItApplication pasteItApplication = (PasteItApplication)getApplication();
        daoSession = pasteItApplication.getDaoSession();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFireBaseUser = FirebaseAuth.getInstance().getCurrentUser();

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //the firebase Paste model
        final Paste paste = intent.getParcelableExtra("paste");
        //get a reference to push this paste to
        DatabaseReference ref = mFirebaseDatabase.getReference("pastes/"+mFireBaseUser.getUid());
        final String key = ref.push().getKey();
        paste.setId(key);
        //We have a key, persist this paste in database
        PasteDao pasteDao = daoSession.getPasteDao();
        //convert the firebase paste instance to greendao paste instance
        final app.paste_it.models.greendao.Paste greendaoPaste = Utils.toGreenDaoPaste(paste);
        pasteDao.insertOrReplace(greendaoPaste);

        //notify via preferences that the paste has been added
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.key_paste_added),key);
        editor.commit();

        //A new list for download urls, this list will replace the list in Paste object
        //once all upload tasks are successful
        final ArrayList<ImageURL> urls = new ArrayList<>();
        countDownLatch = new CountDownLatch(paste.getUrls().size());
        for(final String url  : paste.getUrls()){
            Uri uri  = Uri.parse(url);
            try {
                InputStream inputStream = this.getContentResolver().openInputStream(uri);
                UploadTask task = mFirebaseStorage.getReference("/images/"+mFireBaseUser.getUid()+"/Image_"+ System.currentTimeMillis()+".jpg").putStream(inputStream);
                task.addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //TODO: An upload operation failed, need to setup a retry queue,
                        //or mark this paste for retry
                        countDownLatch.countDown();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot o) {
                        @SuppressWarnings("VisibleForTests")
                        Uri downloadUri = o.getDownloadUrl();
                        urls.add(new ImageURL(null,downloadUri.toString(),key));
                        countDownLatch.countDown();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"Error opening file.");
            }
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // All Upload operations complete, replace content uris with download uris
        greendaoPaste.setUrls(urls);
        pasteDao.insertOrReplace(greendaoPaste);
        //persist the paste to firebase realtime database
        ref.child(key).setValue(paste).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //TODO: remove the paste id from the upload queue
            }
        });


    }
}
