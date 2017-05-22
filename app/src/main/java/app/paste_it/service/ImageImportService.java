package app.paste_it.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import app.paste_it.PasteItApplication;
import app.paste_it.PasteUtils;
import app.paste_it.R;
import app.paste_it.Utils;
import app.paste_it.models.DaoSession;
import app.paste_it.models.ImageModel;
import app.paste_it.models.ImageModelDao;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class ImageImportService extends IntentService {

    private DaoSession daoSession;


    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_IMPORT = "app.paste_it.service.action.FOO";
    private static final String ACTION_DELETE = "app.paste_it.service.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_URI = "app.paste_it.service.extra.PARAM1";
    private static final String EXTRA_ID = "app.paste_it.service.extra.PARAM2";
    private static final String TAG = ImageImportService.class.getSimpleName();

    public ImageImportService() {
        super("ImageImportService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        daoSession = ((PasteItApplication)getApplication()).getDaoSession();
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
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
    // TODO: Customize helper method
    public static void startActionDelete(Context context, Uri uri) {
        Intent intent = new Intent(context, ImageImportService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_URI, uri);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_IMPORT.equals(action)) {
                final Uri uri = intent.getParcelableExtra(EXTRA_URI);
                final ImageModel imageModel = intent.getParcelableExtra(EXTRA_ID);
                handleActionImport(uri, imageModel);
            } else if (ACTION_DELETE.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_URI);
                handleActionDelete();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionImport(Uri uri, ImageModel imageModel) {
        Log.d(TAG, "Starting image Import Service");
        String fileName = imageModel.getId()+"_"+PasteUtils.getFileName(this,uri);
        imageModel.setFileName(fileName);
        try {
            FileOutputStream fos = this.openFileOutput(fileName,MODE_PRIVATE);
            Bitmap bitmap = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(uri));

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();

            ImageModelDao imageModelDao = daoSession.getImageModelDao();
            imageModelDao.insert(imageModel);

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            Gson gson = new Gson();
            editor.putString(getString(R.string.key_image_model_updated),gson.toJson(imageModel));
            editor.commit();

            Log.d(TAG,"Image Imported");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionDelete() {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
