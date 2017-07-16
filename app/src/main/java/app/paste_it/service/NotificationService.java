package app.paste_it.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import app.paste_it.MainActivity;
import app.paste_it.PasteUtils;
import app.paste_it.R;
import app.paste_it.models.Paste;

public class NotificationService extends IntentService {

    private static final String TAG = NotificationService.class.getSimpleName();

    public static final String ACTION_SHOW_NOTIFICATION = "ACTION_SHOW_NOTIFICATION";
    public static final String EXTRA_SINCE = "EXTRA_SINCE";

    public NotificationService(){
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String action = intent.getAction();
        switch (action){
            case ACTION_SHOW_NOTIFICATION:
                String since = intent.getStringExtra(EXTRA_SINCE);
                handleActionShowNotification(since);
                break;
            default:break;
        }
    }

    private void handleActionShowNotification(final String since){
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser!=null){
            String userId = firebaseUser.getUid();
            final long millisElapsed =  getString(R.string.yesterday).equals(since)?PasteUtils.DAY_IN_MILLIS:PasteUtils.WEEK_IN_MILLIS;
            FirebaseDatabase.getInstance().getReference("pastes").child(userId).orderByChild("modified").addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> dataSnapshotInterator = dataSnapshot.getChildren().iterator();
                            int count = 0;
                            while(dataSnapshotInterator.hasNext()){
                                Paste paste = dataSnapshotInterator.next().getValue(Paste.class);
                                if(paste.getModified() > System.currentTimeMillis()- millisElapsed){
                                    count++;
                                }
                            }
                            if(count>0)
                                showNotification(String.valueOf(count),since);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    }
            );
        }
    }

    private void showNotification(String count, String since) {
        Context context = this;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, MainActivity.class), 0);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.notification_title))
                        .setContentText(getString(R.string.notification_text,count,since));
        mBuilder.setContentIntent(contentIntent);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}