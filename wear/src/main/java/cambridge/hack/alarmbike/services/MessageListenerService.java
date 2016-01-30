package cambridge.hack.alarmbike.services;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import cambridge.hack.alarmbike.MapActivity;

/**
 * Created by joel on 1/30/16.
 */
public class MessageListenerService extends WearableListenerService {
    private static final String TAG = MessageListenerService.class.getSimpleName();
    public void onMessageReceived(MessageEvent messageEvent) {
        Intent intent = new Intent(this, MapActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(intent);

        String nodeId = messageEvent.getSourceNodeId();

        Log.d(TAG, "Connected node: " + nodeId);

        String list = new String(messageEvent.getData());

        Log.d(TAG, "Received: " + list);

        // Broadcast
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra("message", list);
        //LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
    }
}
