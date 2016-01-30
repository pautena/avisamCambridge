package cambridge.hack.alarmbike.services;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.utils.LocationUtils;


public class LocationService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener{
    private static LocationService locationService;
    private CameraPosition cameraPosition;

    public static LocationService getInstance(Context context) {
        if(locationService ==null) locationService =new LocationService(context.getApplicationContext());
        return locationService;
    }



    public interface LocationServiceListener{
        void onLocationChanged(LatLng latLng);
    }

    private final static float START_ZOOM=12;
    private final int measureTime;
    private final int measureMinDist;
    private List<LocationServiceListener> listenerList;

    public void addListener(LocationServiceListener listener){
        if(!listenerList.contains(listener))
            listenerList.add(listener);
    }

    public void removeListener(LocationServiceListener listener){
        if(listenerList.contains(listener))
            listenerList.remove(listener);
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    private final Context context;
    private final GoogleApiClient mGoogleApiClient;

    private LocationManager locationManager;
    private boolean locationManagerIsRunning;

    private LatLng latLng;

    public LocationService(Context context) {
        this.context = context;
        listenerList= new ArrayList<>();
        MapsInitializer.initialize(context);
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
        locationManagerIsRunning=false;
    }

    private void startLocationManager(){
        locationManagerIsRunning=true;

        if(LocationUtils.checkLocationPermission(context)){
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    measureTime,
                    measureMinDist, this);

            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    measureTime,
                    measureMinDist, this);
        }
    }


    
    private void stopLocationManager(){
        locationManagerIsRunning=false;
        if(LocationUtils.checkLocationPermission(context))
            locationManager.removeUpdates(this);
    }
    
   public void onResume() {
       if(!locationManagerIsRunning)        
           startLocationManager();
   }

   public void onDestroy() {
       if(locationManagerIsRunning)
           stopLocationManager();
   }

   public void onPause() {
       if(locationManagerIsRunning)
           stopLocationManager();
   }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("LocationService","onLocationChanged: "+location);
        latLng= new LatLng(location.getLatitude(),location.getLongitude());

        for(LocationServiceListener listener : listenerList)
            listener.onLocationChanged(latLng);
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

    public void connect(){
        mGoogleApiClient.connect();
    }

    public void disconnect(){
        mGoogleApiClient.disconnect();
    }

    public GoogleApiClient getApiClient(){ return mGoogleApiClient;}

    public LatLng getCurrentPosition() {
        return latLng;
    }

    public CameraUpdate getStartCamera() {
        LatLng latLng;
        float zoom;

        if(cameraPosition!=null){
            return CameraUpdateFactory.newLatLngZoom(cameraPosition.target, cameraPosition.zoom);
        }
        latLng = getCurrentPosition();
        zoom=14;
        Log.d("LocationService","getStartCamera. latLng: "+latLng);
        if(latLng==null) {
            Log.d("LocationService","setCenter bcn");
            latLng = new LatLng(51.499955, -0.115994);
            zoom=START_ZOOM;
        }

        return CameraUpdateFactory.newLatLngZoom(latLng, zoom);
    }

    public void onCameraChange(CameraPosition cameraPosition) {
        this.cameraPosition = cameraPosition;
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }
}

