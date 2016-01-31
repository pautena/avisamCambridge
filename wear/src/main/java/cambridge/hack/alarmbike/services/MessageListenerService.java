package cambridge.hack.alarmbike.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import cambridge.hack.alarmbike.MapActivity;
import cambridge.hack.alarmbike.R;

/**
 * Created by joel on 1/30/16.
 */
public class MessageListenerService extends WearableListenerService {
    private static final String TAG = MessageListenerService.class.getSimpleName();

    private void startNavigation(final String list) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (!MapActivity.running)
            startActivity(intent);

        new Handler(getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("delay", "delay");
                Intent messageIntent = new Intent();
                messageIntent.setAction(Intent.ACTION_SEND);
                messageIntent.putExtra("message", list);
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
            }
        }, 3000);
    }

    private void endNavigation() {
        Intent intent = new Intent(this, MapActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("end", true);

        startActivity(intent);

        Notification.Builder builder = new Notification.Builder(this)
                // TODO Cool icon
                //.setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(false);

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        nm.notify(0, builder.build());
    }

    private void changeNavigation(final String list) {
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("message", list);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(messageIntent);
    }

    public void onMessageReceived(MessageEvent messageEvent) {
        final String list = new String(messageEvent.getData());

        String nodeId = messageEvent.getSourceNodeId();

        Log.d(TAG, "Connected node: " + nodeId);

        Log.d(TAG, "Received: " + list);

        Log.d(TAG, "Route: " + messageEvent.getPath());

        switch (messageEvent.getPath()) {
            case "/startNavigation":
                startNavigation(list);
                break;
            case "/changeNavigation":
                changeNavigation(list);
                break;
            case "/stopNavigation":
                System.exit(0);
                break;
            case "/endNavigation":
                endNavigation();
                break;
        }
    }
}
