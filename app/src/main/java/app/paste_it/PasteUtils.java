package app.paste_it;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import app.paste_it.adapters.PasteAdapter;
import app.paste_it.models.Identity;
import app.paste_it.models.ImageModel;
import app.paste_it.models.Paste;
import app.paste_it.models.Tag;

/**
 * Created by 834619 on 5/18/2017.
 */

public class PasteUtils {

    private static final String TAG = PasteUtils.class.getSimpleName();

    private PasteUtils() {
    }

    public static String getPasteIds(List<? extends Identity> pastes) {
        List<String> pasteIds = new ArrayList<>();
        for (Identity identity : pastes) {
            pasteIds.add(identity.getId());
        }
        String pasteIdsString = TextUtils.join(",", pasteIds);
        return pasteIdsString;
    }

    private static int containsPaste(List<Paste> pastes, Paste paste){
        for(int i=0;i<pastes.size();i++){
            if(pastes.get(i).getId().equals(paste.getId())){
                return i;
            }
        }
        return -1;
    }

    public static void resolvePaste(Context context, Paste paste, PasteAdapter adapter) {
        int index = containsPaste(adapter.getPastes(),paste);
        if(index > -1){
            adapter.setPaste(index,paste);
        }
        else
            adapter.addPaste(0,paste);

    }

    public static int findIndex(List<ImageModel> imageModelList, ImageModel imageModel){
        if(imageModelList!=null)
            for(int i=0;i<imageModelList.size();i++){
                if(imageModelList.get(i).getId().equals(imageModel.getId()))
                    return i;
            }
        return -1;
    }

    public static int findIndexOfPaste(List<Paste> pasteList, String pasteId ){
        if(pasteList!=null)
            for(int i=0;i<pasteList.size();i++){
                if(pasteList.get(i).getId().equals(pasteId))
                    return i;
            }
        return -1;
    }

    public static String getFileName(Context ccontext, Uri uri){
        Cursor cursor = ccontext.getContentResolver().query(uri,null,null,null,null);
        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
        cursor.moveToFirst();
        return cursor.getString(nameIndex);
    /*
     * Get the column indexes of the data in the Cursor,
     * move to the first row in the Cursor, get the data,
     * and display it.
     */
    }

    public static int findIndexOfTag(List<Tag> tags, String id) {
        if(tags!=null)
            for(int i=0;i<tags.size();i++){
                if(tags.get(i).getId().equals(id))
                    return i;
            }
        return -1;
    }
}
