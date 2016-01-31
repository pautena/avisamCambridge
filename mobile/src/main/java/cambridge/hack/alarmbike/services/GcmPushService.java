package cambridge.hack.alarmbike.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.callback.CreateAlarmCallback;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.Station;
import cambridge.hack.alarmbike.enums.OriginOrDestination;
import cambridge.hack.alarmbike.utils.MapsUtils;
import io.realm.Realm;

/**
 * Created by Duffman on 30/1/16.
 */
public class GcmPushService extends GcmListenerService{

    @Override
    public void onMessageReceived(String from, Bundle data) {
        Log.d("GcmPushService","messageReceived. from: "+from );
        Log.d("GcmPushService", "messageReceived. data: " + data);
        Realm realm = Realm.getInstance(getApplicationContext());
        Alarm alarm = NavigationService.getInstance(getApplicationContext()).getAlarm();


        if(true){
            Station station = alarm.getStation();
            List<Station> stations = realm.where(Station.class).findAll();
            double minDist = Double.MAX_VALUE;
            Station minStation=null;

            for(Station aux : stations){
                double newDist = MapsUtils.distBetween(Station.getLatLng(station),Station.getLatLng(aux));
                if(minDist> newDist){
                    minDist=newDist;
                    minStation= aux;
                }
            }
            NavigationService.getInstance(getApplicationContext()).alarmPushRecived(minStation);

            //Server
            ApiAdapter.getInstance(getApplicationContext()).createAlarm(minStation, alarm.getState(), new CreateAlarmCallback() {
                @Override
                public void onCreateAlarm(Alarm alarm) {

                }

                @Override
                public void onError(Throwable t) {

                }
            });

            //Watch
            Intent intent = new Intent(getApplicationContext(), WearMessageService.class);
            intent.putExtra("message", Station.getJson(alarm.getStation()).toString());
            intent.putExtra("path", "/changeNavigation");
            startService(intent);

            sendPebbleNotif(alarm,minStation);

        }
        realm.close();
    }

    private void sendPebbleNotif(Alarm alarm,Station station){
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");
        String part;
        if(alarm.getState().equals(OriginOrDestination.DESTINATION)){
            part = getResources().getString(R.string.slots);
        }else{
            part = getResources().getString(R.string.bikes);
        }

        String notifTitle = getResources().getString(R.string.notif_title_alarm_push,part);
        String notifContent = getResources().getString(R.string.notif_title_alarm_content,station.getName());

        final Map<String, String> pebbleData = new HashMap<>();
        pebbleData.put("title", notifTitle);
        pebbleData.put("body", notifContent);

        final JSONObject jsonData = new JSONObject(pebbleData);
        final String notificationData = new JSONArray().put(jsonData).toString();
        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "Test");
        i.putExtra("notificationData", notificationData);

        Log.d("Test", "Sending to Pebble: " + notificationData);
        sendBroadcast(i);
    }

}
