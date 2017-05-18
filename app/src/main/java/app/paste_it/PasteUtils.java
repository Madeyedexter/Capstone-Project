package app.paste_it;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import app.paste_it.models.Identity;

/**
 * Created by 834619 on 5/18/2017.
 */

public class PasteUtils {

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
}
