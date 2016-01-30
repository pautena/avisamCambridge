package cambridge.hack.alarmbike.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

/**
 * Created by Duffman on 30/1/16.
 */
public class GcmPushService extends GcmListenerService{

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("GcmPushService","messageReceived. from: "+from );
        Log.d("GcmPushService","messageReceived. data: "+data );

    }

}
