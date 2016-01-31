package cambridge.hack.alarmbike.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;

import android.location.LocationListener;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.Station;
import cambridge.hack.alarmbike.enums.OriginOrDestination;
import cambridge.hack.alarmbike.ui.main.customViews.infoDestination.InfoDestination;
import cambridge.hack.alarmbike.utils.LocationUtils;
import cambridge.hack.alarmbike.utils.MapsUtils;


/**
 * Created by Duffman on 30/1/16.
 */
public class NavigationService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private static final int NAVIGATION_NOTIFICATION_ID=1;
    private static final int NAVIGATION_FINISH_NOTIFICATION_ID=2;
    private static final int NAVIGATION_ALARM_PUSH_NOTIFICATION_ID=3;



    public static class StopNavigationIntent extends IntentService{

        public StopNavigationIntent() {
            super(StopNavigationIntent.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Log.d("NavitaionService","StopNavigationIntent.onHandleIntent");
            //Server
            ApiAdapter.getInstance(getApplicationContext())
                    .finishAlarm(NavigationService.getInstance(getApplicationContext()).getAlarm());

            //Wear
            Intent i = new Intent(this, WearMessageService.class);
            i.putExtra("path", "/stopNavigation");
            startService(i);
            NavigationService.getInstance(getApplicationContext()).stopNavigation();
        }
    }


    private static NavigationService instance;

    public static NavigationService getInstance(Context context){
        if(instance==null) instance= new NavigationService(context.getApplicationContext());
        return instance;
    }


    private Context context;
    private boolean navigationIsRun;
    private LocationManager locationManager;
    private GoogleApiClient mGoogleApiClient;
    private int measureTime,measureMinDist;
    private Alarm alarm;
    private int stationUid;
    private String stationName;


    private NavigationService(Context context){
        this.context = context;
        navigationIsRun =false;

        locationManager = (LocationManager) context
                .getSystemService(context.LOCATION_SERVICE);

        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .build();

        //Get system location Listeners
        measureTime = context.getResources().getInteger(R.integer.measure_time);
        measureMinDist=context.getResources().getInteger(R.integer.measure_min_dist);
    }

    public void startNavigation(Alarm alarm){
        if(!navigationIsRun && LocationUtils.checkLocationPermission(context)){
            navigationIsRun=true;
            this.alarm=alarm;
            stationUid = alarm.getStation().getUid();
            stationName = alarm.getStation().getName();
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    measureTime,
                    measureMinDist, this);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    measureTime,
                    measureMinDist, this);

            showNavigateNotification();
        }
    }

    private void showNavigateNotification(){
        String notifTitle= context.getResources().getString(R.string.notif_title_navigation);
        String notifContent= stationName;
        String actionNotif = context.getResources().getString(R.string.notif_stop_navigation);

        Intent intent = new Intent(context, StopNavigationIntent.class);
        PendingIntent pIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_directions_bike_white_24dp)
                .setContentTitle(notifTitle)
                .setContentText(notifContent)
                .setOngoing(true)
                .addAction(R.drawable.ic_close_white_24dp, actionNotif, pIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NAVIGATION_NOTIFICATION_ID, mBuilder.build());
    }

    public int getStationUid(){ return stationUid;}

    public void stopNavigation(){
        if(navigationIsRun && LocationUtils.checkLocationPermission(context)) {

            destroyNavigationNotification();
            navigationIsRun=false;
            alarm =null;
            locationManager.removeUpdates(this);
        }
    }

    private void destroyNavigationNotification(){



        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(NAVIGATION_NOTIFICATION_ID);

    }

    public Alarm getAlarm(){
        return alarm;
    }

    public void alarmPushRecived(Station newStation) {
        String part;
        if(alarm.getState().equals(OriginOrDestination.DESTINATION)){
            part = context.getResources().getString(R.string.slots);
        }else{
            part = context.getResources().getString(R.string.bikes);
        }

        String notifTitle = context.getResources().getString(R.string.notif_title_alarm_push,part);
        String notifContent = context.getResources().getString(R.string.notif_title_alarm_content,newStation.getName());

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_warning_white_24dp)
                .setContentTitle(notifTitle)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentText(notifContent);


        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.notify(NAVIGATION_ALARM_PUSH_NOTIFICATION_ID,mBuilder.build());

        stationUid= newStation.getUid();
        stationName = newStation.getName();
        showNavigateNotification();
    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng newPosition= new LatLng(location.getLatitude(),location.getLongitude());
        double distance = MapsUtils.distBetween(newPosition,Station.getLatLng(alarm.getStation()));
        Log.d("NavigationService", "new location: (" + location.getLatitude() +","+location.getLongitude()+"), distance: "+distance+" m");

        if(distance<20){
            //TODO: Enviar al server que s'ha arrivat al destí
            Intent i = new Intent(context, WearMessageService.class);
            i.putExtra("path", "/endNavigation");
            context.startService(i);
            String notifTitle = context.getResources().getString(R.string.notif_destination_arrive);
            String notifContent = alarm.getStation().getName();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_place_white_24dp)
                    .setContentTitle(notifTitle)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentText(notifContent);

            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.notify(NAVIGATION_FINISH_NOTIFICATION_ID, mBuilder.build());
            stopNavigation();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
