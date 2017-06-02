package app.paste_it.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.paste_it.PasteItApplication;
import app.paste_it.R;
import app.paste_it.Utils;
import app.paste_it.models.DaoSession;
import app.paste_it.models.ImageModel;
import app.paste_it.models.ImageModelDao;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class ImageImportService extends IntentService {


    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_IMPORT = "app.paste_it.service.action.FOO";
    private static final String ACTION_IMPORT_PICTURE = "app.paste_it.service.action.FOOPICTURE";
    private static final String ACTION_DELETE = "app.paste_it.service.action.BAZ";

    private static final String EXTRA_URI = "app.paste_it.service.extra.PARAM1";
    private static final String EXTRA_ID = "app.paste_it.service.extra.PARAM2";
    private static final String EXTRA_DATA = "app.paste_it.service.extra.PARAM3";
    private static final String TAG = ImageImportService.class.getSimpleName();
    private DaoSession daoSession;

    private String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

    public ImageImportService() {
        super("ImageImportService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionImport(Context context, Uri uri, ImageModel imageModel) {
        Intent intent = new Intent(context, ImageImportService.class);
        intent.setAction(ACTION_IMPORT);
        intent.putExtra(EXTRA_URI, uri);
        intent.putExtra(EXTRA_ID, imageModel);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionDelete(Context context, ImageModel imageModel) {
        Log.d(TAG,"Starting delete operation.");
        Intent intent = new Intent(context, ImageImportService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_DATA, imageModel );
        context.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        daoSession = ((PasteItApplication) getApplication()).getDaoSession();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            Log.d(TAG,"Action is: "+intent.getAction());
            final String action = intent.getAction();
            if (ACTION_IMPORT.equals(action)) {
                final Uri uri = intent.getParcelableExtra(EXTRA_URI);
                final ImageModel imageModel = intent.getParcelableExtra(EXTRA_ID);
                handleActionImport(uri, imageModel);
            } else if (ACTION_DELETE.equals(action)) {
                final ImageModel imageModel = intent.getParcelableExtra(EXTRA_DATA);
                handleActionDelete(imageModel);
            } else if (ACTION_IMPORT_PICTURE.equals(action)){
                final Bitmap bitmap = intent.getParcelableExtra(EXTRA_DATA);
                final ImageModel imageModel = intent.getParcelableExtra(EXTRA_ID);
                handleActionImport(bitmap, imageModel);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionImport(Uri uri, ImageModel imageModel) {
        Log.d(TAG, "Starting image Import Service");
        try {
            FileOutputStream fos = this.openFileOutput(imageModel.getFileName(), MODE_PRIVATE);
            Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            ImageModelDao imageModelDao = daoSession.getImageModelDao();
            imageModelDao.insert(imageModel);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            editor.putString(getString(R.string.key_image_model_updated), gson.toJson(imageModel));
            editor.commit();

            Log.d(TAG, "Image Imported");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void handleActionImport(Bitmap bitmap, ImageModel imageModel){
        Log.d(TAG, "Starting picture Import Service");
        try {
            FileOutputStream fos = this.openFileOutput(imageModel.getFileName(), MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            ImageModelDao imageModelDao = daoSession.getImageModelDao();
            imageModelDao.insert(imageModel);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            editor.putString(getString(R.string.key_image_model_updated), gson.toJson(imageModel));
            editor.commit();

            Log.d(TAG, "Image Imported");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     * @param imageModel the imageModel corresponding to the image
     */
    private void handleActionDelete(ImageModel imageModel) {
        ImageModelDao imageModelDao = daoSession.getImageModelDao();
        String path = Utils.getFullPath(this,imageModel.getFileName());
        File imageFile = new File(path);
        Log.d(TAG,"File exists: "+imageFile.exists());
        //This call will trigger a firebase function which will remove the corresponding file from firebase storage
        FirebaseDatabase.getInstance().getReference("pastes").child(uid).child(imageModel.getPasteId()).child("urls").child(imageModel.getId()).removeValue();
        if(imageFile.exists()){
            Log.d(TAG, "File Deleted: "+ imageFile.delete());
        }
        //remove greendao entry
        imageModelDao.deleteByKey(imageModel.getId());

        //notify via sp that the image has been deleted
        SharedPreferences.Editor editor =  PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString(getString(R.string.key_sp_image_removed_id),imageModel.getId());
        editor.commit();
    }

    public static void startActionImportPicture(Context context, Bitmap bitmap, ImageModel imageModel) {
        Intent intent = new Intent(context, ImageImportService.class);
        intent.setAction(ACTION_IMPORT_PICTURE);
        intent.putExtra(EXTRA_DATA, bitmap);
        intent.putExtra(EXTRA_ID, imageModel);
        Log.d(TAG,"Starting Picture Import Service.");
        context.startService(intent);
    }
}
