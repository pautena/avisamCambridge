package cambridge.hack.alarmbike.services;

import android.app.IntentService;
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
import cambridge.hack.alarmbike.entities.Station;
import cambridge.hack.alarmbike.utils.LocationUtils;
import cambridge.hack.alarmbike.utils.MapsUtils;


/**
 * Created by Duffman on 30/1/16.
 */
public class NavigationService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener {
    private static final int NAVIGATION_NOTIFICATION_ID=1;
    private static final int NAVIGATION_FINISH_NOTIFICATION_ID=2;
    public static class StopNavigationIntent extends IntentService{

        public StopNavigationIntent() {
            super(StopNavigationIntent.class.getSimpleName());
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            Log.d("NavitaionService","StopNavigationIntent.onHandleIntent");
            //TODO: enviar al server que s'ha finalitzat la navegació
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
    private Station destinationStation;


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

    public void startNavigation(Station destinationStation){
        if(!navigationIsRun && LocationUtils.checkLocationPermission(context)){
            navigationIsRun=true;
            this.destinationStation = destinationStation;
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    measureTime,
                    measureMinDist, this);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    measureTime,
                    measureMinDist, this);

            String notifTitle= context.getResources().getString(R.string.notif_title_navigation);
            String notifContent= destinationStation.getName();
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
    }

    public void stopNavigation(){
        if(navigationIsRun && LocationUtils.checkLocationPermission(context)) {

            destroyNavigationNotification();
            navigationIsRun=false;
            destinationStation =null;
            locationManager.removeUpdates(this);
        }
    }

    private void destroyNavigationNotification(){
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotifyMgr.cancel(NAVIGATION_NOTIFICATION_ID);

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
        double distance = MapsUtils.distBetween(newPosition,Station.getLatLng(destinationStation));
        Log.d("NavigationService", "new location: (" + location.getLatitude() +","+location.getLongitude()+"), distance: "+distance+" m");

        if(distance<20){
            //TODO: Enviar al server que s'ha arrivat al destí
            Intent i = new Intent(context, WearMessageService.class);
            i.putExtra("path", "/endNavigation");
            context.startService(i);
            String notifTitle = context.getResources().getString(R.string.notif_destination_arrive);
            String notifContent = destinationStation.getName();

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.drawable.ic_place_white_24dp)
                    .setContentTitle(notifTitle)
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
