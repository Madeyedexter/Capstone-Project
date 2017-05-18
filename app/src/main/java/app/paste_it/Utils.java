package app.paste_it;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.view.inputmethod.InputMethodManager;

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

    public static app.paste_it.models.firebase.Paste toFirebasePaste(Paste paste) {
        app.paste_it.models.firebase.Paste pasteFB = new app.paste_it.models.firebase.Paste();
        pasteFB.setId(paste.getId());
        pasteFB.setCreated(paste.getCreated());
        pasteFB.setModified(paste.getModified());
        pasteFB.setTitle(paste.getTitle());
        pasteFB.setText(paste.getText());
        List<String> tags = new ArrayList<>();
        for (Tag tag : paste.getTags()) {
            tags.add(tag.getLabel());
        }
        pasteFB.setTags(tags);
        List<String> urls = new ArrayList<>();
        for (ImageURL url : paste.getUrls()) {
            urls.add(url.getUrl());
        }
        pasteFB.setUrls(urls);

        return pasteFB;
    }

    public static List<app.paste_it.models.firebase.Paste> toFirebasePasteList(List<Paste> pastes) {
        List<app.paste_it.models.firebase.Paste> firebasePasteList = new ArrayList<>();
        for (Paste paste : pastes) {
            firebasePasteList.add(toFirebasePaste(paste));
        }
        return firebasePasteList;
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

    public static List<Paste> toGreenDaoPasteList(List<app.paste_it.models.firebase.Paste> pastes) {
        List<Paste> greenDaoPastesList = new ArrayList<>();
        for (app.paste_it.models.firebase.Paste paste : pastes) {
            Paste pasteGD = toGreenDaoPaste(paste);
            greenDaoPastesList.add(pasteGD);
        }
        return greenDaoPastesList;
    }

    public static void closeKeyboard(Context c, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }
}