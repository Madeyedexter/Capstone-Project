package app.paste_it;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import org.greenrobot.greendao.database.Database;

import app.paste_it.models.DaoMaster;
import app.paste_it.models.DaoSession;


/**
 * Created by Madeyedexter on 13-05-2017.
 */

public class PasteItApplication extends Application {

    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "pasteit-db");
        Database db = helper.getWritableDb();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        daoSession = new DaoMaster(db).newSession();
    }

    public DaoSession getDaoSession() {
        return daoSession;
    }
}
