package cambridge.hack.alarmbike.ui.main;

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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.callback.GetStationsCallback;
import cambridge.hack.alarmbike.entities.Station;
import cambridge.hack.alarmbike.services.CityBikAdapter;
import cambridge.hack.alarmbike.services.LocationService;
import cambridge.hack.alarmbike.utils.LocationUtils;
import io.realm.Realm;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapClickListener, GoogleMap.OnMapLongClickListener, GetStationsCallback, GoogleMap.OnMarkerClickListener, LocationService.LocationServiceListener {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;

    @Bind(R.id.nav_view)
    NavigationView navigationView;

    SupportMapFragment mapFragment;
    private GoogleMap map;

    LocationService locationService;
    Realm realm;
    private Station destinationStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        realm = Realm.getInstance(this);

        setupServices();
        setupToolbar();
        setupFab();
        setupNavigationView();
        setupMap();
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
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setupNavigationView() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);
        mapFragment.getMapAsync(this);
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
        map.getUiSettings().setAllGesturesEnabled(true);
        map.animateCamera(locationService.getStartCamera());

        CityBikAdapter.getInstance(this).getLondonStations(this);


    }

    @Override
    public void onMapClick(LatLng latLng) {
        //TODO: Acció de quan es fa clic al mapa
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        //TODO: Acció de quan es fa long clic al mapa
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        destinationStation = realm.where(Station.class)
                .equalTo("latitude",marker.getPosition().latitude)
                .equalTo("longitude",marker.getPosition().longitude).findFirst();

        Log.d("MainActivity","selected destination. station id: "+destinationStation.getUid());

        return false;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latLng);
            map.animateCamera(cameraUpdate);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onGetStationsFinish(List<Station> stations) {
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
