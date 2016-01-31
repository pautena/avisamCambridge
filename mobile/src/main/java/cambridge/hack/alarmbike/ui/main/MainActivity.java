package cambridge.hack.alarmbike.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.callback.CreateAlarmCallback;
import cambridge.hack.alarmbike.callback.GetStationsCallback;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.Station;
import cambridge.hack.alarmbike.enums.OriginOrDestination;
import cambridge.hack.alarmbike.services.AlarmbikeInstanceIDListenerService;
import cambridge.hack.alarmbike.services.ApiAdapter;
import cambridge.hack.alarmbike.services.CityBikAdapter;
import cambridge.hack.alarmbike.services.LocationService;
import cambridge.hack.alarmbike.services.WearMessageService;
import cambridge.hack.alarmbike.services.NavigationService;
import cambridge.hack.alarmbike.services.RegisterGcm;
import cambridge.hack.alarmbike.ui.alarms.AlarmActivity;
import cambridge.hack.alarmbike.ui.main.customViews.infoDestination.InfoDestination;
import cambridge.hack.alarmbike.utils.LocationUtils;
import io.realm.Realm;


public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener,
        GetStationsCallback, GoogleMap.OnMarkerClickListener, LocationService.LocationServiceListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.info_destination)
    InfoDestination infoDestination;

    SupportMapFragment mapFragment;
    private GoogleMap map;

    LocationService locationService;
    NavigationService navigationService;
    Realm realm;
    private Station destinationStation;
    private List<Marker> markers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        realm = Realm.getInstance(this);
        new RegisterGcm(this).execute();

        setupServices();
        setupToolbar();
        setupMap();

        infoDestination.setOnClickOriginListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDestination.setState(OriginOrDestination.ORIGIN);
            }
        });

        infoDestination.setOnClickDestinationListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoDestination.setState(OriginOrDestination.DESTINATION);
            }
        });
    }

    @Override
    protected void onDestroy() {
        locationService.onDestroy();
        locationService.removeListener(this);
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        locationService.connect();
        locationService.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        locationService.disconnect();
        locationService.onPause();
        super.onPause();
    }

    private void setupServices() {
        locationService = LocationService.getInstance(this);
        locationService.addListener(this);

        navigationService= NavigationService.getInstance(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);

    }

    @OnClick(R.id.fab)
    public void onClickStartNavigation(View view){
        Log.d("MainActivity", "onClickStartNavigation");
        if(destinationStation!=null && !infoDestination.getState().equals(OriginOrDestination.NONE)) {
            ApiAdapter.getInstance(this).createAlarm(destinationStation, infoDestination.getState(), new CreateAlarmCallback() {
                @Override
                public void onCreateAlarm(Alarm alarm) {
                    navigationService.startNavigation(alarm);
                    //Wear
                    Intent intent = new Intent(MainActivity.this, WearMessageService.class);
                    intent.putExtra("message", Station.getJson(alarm.getStation()).toString());
                    intent.putExtra("path", "/startNavigation");
                    startService(intent);
                }

                @Override
                public void onError(Throwable t) {
                    Toast.makeText(MainActivity.this,R.string.error_create_alarm,Toast.LENGTH_SHORT).show();
                }
            });
        }else
            Toast.makeText(this,R.string.no_destination_selected,Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.info_destination)
    public void onClickInfoDestination(View view){
        if(destinationStation!=null){
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(Station.getLatLng(destinationStation));
            map.animateCamera(cameraUpdate);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MainActivity","onMapReady");
        map = googleMap;
        map.setOnMapClickListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMarkerClickListener(this);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        if (LocationUtils.checkLocationPermission(this)) {
            map.setMyLocationEnabled(true);
        }
        UiSettings settings = map.getUiSettings();
        settings.setAllGesturesEnabled(true);
        settings.setMapToolbarEnabled(false);


        map.animateCamera(locationService.getStartCamera());

        showStations();
        CityBikAdapter.getInstance(this).getLondonStations(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        destinationStation = realm.where(Station.class)
                .equalTo("latitude",marker.getPosition().latitude)
                .equalTo("longitude",marker.getPosition().longitude).findFirst();
        infoDestination.showStation(destinationStation);
        Log.d("MainActivity", "selected destination. station id: " + destinationStation.getUid());

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_my_location) {
            Log.d("MainActivity","map status: "+map);
            LatLng latLng = LocationService.getInstance(this).getCurrentPosition();

            if(latLng!=null) {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
                map.animateCamera(cameraUpdate);
            }else{
                Toast.makeText(this,R.string.msg_no_position,Toast.LENGTH_SHORT).show();
            }
            return true;
        }else if(id==R.id.action_show_hide_destination){
            infoDestination.swipeVisibility();
        }else if(id==R.id.action_show_alarms){
            Intent intent = new Intent(this, AlarmActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetStationsFinish(List<Station> stations) {
        showStations();
    }

    public void showStations(){
        for(Marker marker: markers)
            marker.remove();

        List<Station> stations = realm.where(Station.class).findAll();

        for (Station station : stations){
            LatLng latLng  = Station.getLatLng(station);

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(latLng);
            Station.setMarkerByBikes(station, markerOptions);

            map.addMarker(markerOptions);
        }
    }

    @Override
    public void onError(int code, String message) {
        Toast.makeText(this,R.string.error_get_stations,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(Throwable throwable) {
        Toast.makeText(this,R.string.error_get_stations,Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onLocationChanged(LatLng latLng) {

    }
}
