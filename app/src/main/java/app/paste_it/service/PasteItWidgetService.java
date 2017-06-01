package app.paste_it.service;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import app.paste_it.PasteItWidgetConfigureActivity;
import app.paste_it.R;
import app.paste_it.models.Paste;
import app.paste_it.models.WidgetPreference;

public class PasteItWidgetService extends RemoteViewsService {
    public PasteItWidgetService() {
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        return new ListRemoteViewsFactory(getApplicationContext(), intent, appWidgetManager);
    }

    private class ListRemoteViewsFactory implements RemoteViewsFactory {

        private AppWidgetManager appWidgetManager;

        private int appWidgetId=-1;

        private int noOfItems=3;

        ListRemoteViewsFactory(Context applicationContext, Intent intent , AppWidgetManager appWidgetManager) {
            this.appWidgetManager=appWidgetManager;
            appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,-1);
            String jsonConfiguration =applicationContext.getSharedPreferences(PasteItWidgetConfigureActivity.PREFS_NAME,0).getString(PasteItWidgetConfigureActivity.PREF_PREFIX_KEY+appWidgetId,null);
            WidgetPreference widgetPreference = jsonConfiguration!=null?new
                    GsonBuilder().create().fromJson(jsonConfiguration,WidgetPreference.class):null;
            noOfItems = widgetPreference==null?3:widgetPreference.getNoOfItems();
        }

        private final String TAG = ListRemoteViewsFactory.class.getSimpleName();

        private List<Paste> pastes = new ArrayList<>();

        private ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String,Paste> map = dataSnapshot.getValue(new GenericTypeIndicator<HashMap<String, Paste>>() {});
                pastes.addAll(map.values());
                appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId,R.id.listView);
                Log.d(TAG,"Pastes added!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };



        @Override
        public void onCreate() {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser!=null){
                FirebaseDatabase.getInstance().getReference("pastes").child(firebaseUser.getUid()).limitToLast(noOfItems).addListenerForSingleValueEvent(valueEventListener);
            }
        }

        @Override
        public void onDataSetChanged() {

        }

        @Override
        public void onDestroy() {

        }

        @Override
        public int getCount() {
            return pastes==null?0:pastes.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            RemoteViews rv = new RemoteViews(getPackageName(), R.layout.item_widget);
            rv.setTextViewText(R.id.tvTitle, pastes.get(position).getTitle());
            rv.setTextViewText(R.id.tvContent, pastes.get(position).getText());

            Intent fillinIntent = new Intent();
            fillinIntent.putExtra(getString(R.string.key_paste),pastes.get(position));
            rv.setOnClickFillInIntent(R.id.llWidgetItemWrapper,fillinIntent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
