package app.paste_it;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.List;

import app.paste_it.models.greendao.ImageURL;
import app.paste_it.models.greendao.Paste;
import app.paste_it.models.greendao.Tag;

public final class Utils {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {Manifest.permission.WRITE_EXTERNAL_STORAGE};


    /**
     * Checks if the app has permission to write to device storage
     * <p/>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        // Check if we have write permission
        if (currentapiVersion >= 23) {
            int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(
                        activity,
                        PERMISSIONS_STORAGE,
                        REQUEST_EXTERNAL_STORAGE
                );
            }
        }
    }

    public static Paste toGreenDaoPaste(app.paste_it.models.firebase.Paste paste){
        Paste pasteGD = new Paste();

        pasteGD.setId(paste.getId());
        pasteGD.setTitle(paste.getTitle());
        pasteGD.setCreated(paste.getCreated());
        pasteGD.setModified(paste.getModified());
        pasteGD.setText(paste.getText());
        List<Tag> tags = new ArrayList<>();
        for(String tag : paste.getTags()){
            tags.add(new Tag(null,tag,paste.getId()));
        }
        pasteGD.setTags(tags);

        List<ImageURL> urls = new ArrayList<>();
        for(String url : paste.getUrls()){
            urls.add(new ImageURL(null,url,paste.getId()));
        }
        pasteGD.setUrls(urls);

        return pasteGD;
    }
}