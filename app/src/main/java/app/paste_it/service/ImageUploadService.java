package app.paste_it.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import app.paste_it.PasteItApplication;
import app.paste_it.R;
import app.paste_it.models.DaoSession;
import app.paste_it.models.ImageModel;
import app.paste_it.models.ImageModelDao;

/**
 * Created by Madeyedexter on 21-05-2017.
 */

public class ImageUploadService extends IntentService {

    public static final String ACTION_IMAGE_UPLOAD = "app.paste_it.action.ACTION_IMAGE_UPLOAD";

    private static final String TAG = ImageUploadService.class.getSimpleName();

    private StorageReference firebaseStorage = FirebaseStorage.getInstance().getReference("images/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
    private DaoSession daoSession;

    public ImageUploadService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        daoSession = ((PasteItApplication) getApplication()).getDaoSession();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        if (action != null && action.equals(ACTION_IMAGE_UPLOAD)) {
            ImageModelDao imageModelDao = daoSession.getImageModelDao();
            //get all image models whose dload url is null

            List<ImageModel> imageModels = imageModelDao.queryBuilder().where(ImageModelDao.Properties.DownloadURL.isNull()).build().list();

            Log.d(TAG, "Image Models not uploaded: " + imageModels);

            //start the upload
            for (ImageModel imageModel : imageModels) {
                InputStream inputStream = null;
                UploadTask task = null;
                try {
                    inputStream = this.openFileInput(imageModel.getFileName());
                    task = firebaseStorage.child(imageModel.getFileName()).putStream(inputStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                try {
                    Tasks.await(task);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                task.isSuccessful();
                if (task.isSuccessful()) {
                    @SuppressWarnings("VisibleForTests")
                    Uri dloadUri = task.getResult().getDownloadUrl();
                    imageModel.setDownloadURL(dloadUri.toString());
                    imageModelDao.update(imageModel);
                    Log.d(TAG, "Image Model after insert is: " + imageModel);
                    FirebaseDatabase.getInstance().getReference("pastes/" + FirebaseAuth.getInstance().getCurrentUser().getUid()).child(imageModel.getPasteId())
                            .child("urls").child(imageModel.getId()).setValue(imageModel);

                    //notify via shared preferences that the download uri has been updated
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
                    editor.putString(getString(R.string.key_dload_uri_available), new Gson().toJson(imageModel));
                    editor.commit();
                } else {
                    Log.d(TAG, "Task was not successful");
                }
                Log.d(TAG, "Task was succeesful: " + task.isSuccessful());
            }
        }
    }
}
