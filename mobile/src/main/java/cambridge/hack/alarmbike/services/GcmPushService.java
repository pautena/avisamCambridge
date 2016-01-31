package cambridge.hack.alarmbike.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;

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


        if(false){
            Station station = realm.where(Station.class).findFirst();
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
            Alarm alarm = NavigationService.getInstance(getApplicationContext()).getAlarm();
            NavigationService.getInstance(getApplicationContext()).alarmPushRecived(minStation);


            ApiAdapter.getInstance(getApplicationContext()).createAlarm(minStation, alarm.getState(), new CreateAlarmCallback() {
                @Override
                public void onCreateAlarm(Alarm alarm) {

                }

                @Override
                public void onError(Throwable t) {

                }
            });


            //TODO: Enviar al watch la nova estació

        }


        realm.close();
    }

}
