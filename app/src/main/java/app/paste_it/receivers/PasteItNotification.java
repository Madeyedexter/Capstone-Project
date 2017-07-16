package app.paste_it.receivers;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import app.paste_it.service.NotificationService;

/**
 * Created by Madeyedexter on 22-06-2017.
 */

public class PasteItNotification extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, NotificationService.class);
        serviceIntent.setAction(NotificationService.ACTION_SHOW_NOTIFICATION);
        serviceIntent.putExtra(NotificationService.EXTRA_SINCE,intent.getStringExtra(NotificationService.EXTRA_SINCE));
        context.startService(serviceIntent);
    }
}
