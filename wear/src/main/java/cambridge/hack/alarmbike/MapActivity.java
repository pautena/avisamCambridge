package cambridge.hack.alarmbike;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityRecord;
import android.widget.FrameLayout;

import cambridge.hack.alarmbike.model.Station;
import cambridge.hack.alarmbike.receivers.MessageReceiver;

public class MapActivity extends WearableActivity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    public static boolean running = false;

    /**
     * Overlay that shows a short help text when first launched. It also provides an option to
     * exit the app.
     */
    private DismissOverlayView mDismissOverlay;
    private MapFragment mMapFragment;
    private LatLng mCurrentMarker;

    /**
     * The map. It is initialized when the map has been fully loaded and is ready to be used.
     *
     * @see #onMapReady(com.google.android.gms.maps.GoogleMap)
     */
    private GoogleMap mMap;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        Intent intent = getIntent();

        if (intent.getBooleanExtra("end", false))
            finish();

        running = true;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set the layout. It only contains a MapFragment and a DismissOverlay.
        setContentView(R.layout.activity_map);

        setAmbientEnabled();

        // Retrieve the containers for the root of the layout and the map. Margins will need to be
        // set on them to account for the system window insets.
        final FrameLayout topFrameLayout = (FrameLayout) findViewById(R.id.root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) findViewById(R.id.map_container);

        // Set the system view insets on the containers when they become available.
        topFrameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Call through to super implementation and apply insets
                insets = topFrameLayout.onApplyWindowInsets(insets);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                // Add Wearable insets to FrameLayout container holding map as margins
                params.setMargins(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                mapFrameLayout.setLayoutParams(params);

                return insets;
            }
        });

        // Obtain the DismissOverlayView and display the introductory help text.
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.intro_text);
        mDismissOverlay.showIntroIfNecessary();

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        mMapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);

        mMapFragment.getMapAsync(this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        running = false;
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        mMapFragment.onEnterAmbient(ambientDetails);
    }

    @Override
    public void onExitAmbient() {
        super.onExitAmbient();
        mMapFragment.onExitAmbient();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;

        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver(new MapHandler(mMap, this));

        // Set the long click listener as a way to exit the map.
        mMap.setOnMapLongClickListener(this);
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location loc) {
                mMap.moveCamera(CameraUpdateFactory
                        .newLatLngZoom(new LatLng(loc.getLatitude(), loc.getLongitude()), 16f));
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, messageFilter);

        if (mCurrentMarker != null) {
            mMap.addMarker(new MarkerOptions().position(mCurrentMarker));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentMarker));
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Display the dismiss overlay with a button to exit this activity.
        mDismissOverlay.show();
    }

    public static class MapHandler extends Handler {

        GoogleMap mMap;
        Activity mParent;

        public MapHandler(GoogleMap map, Activity parent) {
            mMap = map;
            mParent = parent;
        }

        public void post(final Station station) {
            mParent.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LatLng pos = Station.getLatLng(station);
                    MarkerOptions mo = new MarkerOptions().position(pos)
                            .title(station.getName()); // TODO <-
                    mMap.clear();
                    mMap.addMarker(mo);
                }
            });
        }
    }
}
