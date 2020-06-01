package com.liminal.arexploreadmin;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MapControlPanelActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private static final String TAG = "EAG_Google_Maps";
    private GoogleMap mMap;

    // Default location (Mumbai)
    private final LatLng mDefaultLocation = new LatLng(19.0760, 72.8777);
    // Default zoom
    private static final int DEFAULT_ZOOM = 17;
    // Access to the system location services.
    private LocationManager locationManager;

    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private Location mLastSavedLocation;
    private boolean isMapOpened = false;

    // String to store selected AR activity
    static String selectedActivityID = "NA";

    // Keys for storing activity state.
    private static final String KEY_LOCATION = "location";

    // Firebase connectivity
    private DatabaseReference locationBasedActivityTableReference;

    //Enable GPS alert dialog box
    private AlertDialog alert;


    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_control_panel);

        locationBasedActivityTableReference = FirebaseDatabase.getInstance().getReference().child("locationBasedActivityTable");

        // Retrieve location from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Implement button to select activity
        FloatingActionButton selectActivityButton = findViewById(R.id.activitySelectButton);
        selectActivityButton.setOnClickListener(view -> manageSelectActivityPopup());
    }



    // Function to show alert box to ask user to enable GPS
    private void enableGPS(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("GPS is disabled in your device. Enable GPS to continue.")
                .setCancelable(false)
                .setPositiveButton("Enable GPS",
                        (dialog, id) -> {
                            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(callGPSSettingIntent);
                        });
        alert = alertDialogBuilder.create();
        alert.show();
    }



    @Override
    public void onResume()
    {
        super.onResume();
        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            Log.d(TAG,"GPS enabled by user");
            getDeviceLocation();
        }
        else
            enableGPS();
    }



    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG,"Location changed");
        mLastKnownLocation = location;
        if(!isMapOpened) {
            isMapOpened = true;
            // Move camera to new user location
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), DEFAULT_ZOOM));
        }
    }



    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) { }



    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(this,"GPS Enabled", Toast.LENGTH_SHORT).show();
        alert.dismiss();
        if(mLastSavedLocation == null)
            getDeviceLocation();
        else
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom
                    (new LatLng(mLastSavedLocation.getLatitude(), mLastSavedLocation.getLongitude()), DEFAULT_ZOOM));

    }



    @Override
    public void onProviderDisabled(String s) {
        Toast.makeText(this,"GPS Disabled", Toast.LENGTH_SHORT).show();
        mLastSavedLocation = mLastKnownLocation;
        if(alert==null || !alert.isShowing())
            enableGPS();
    }



    @Override
    public boolean onMarkerClick(Marker marker) {
        LocationBasedActivity activity = (LocationBasedActivity) marker.getTag();
        final Dialog showActivityDialogBox = new Dialog(this);
        showActivityDialogBox.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        showActivityDialogBox.setContentView(R.layout.dialog_box_show_activity);

        Button cancelButton = showActivityDialogBox.findViewById(R.id.dialogBoxCancelButton);
        Button deleteButton = showActivityDialogBox.findViewById(R.id.deleteMarkerButton);

        TextView activityIDTextView = showActivityDialogBox.findViewById(R.id.activityIDTextView);
        TextView latitudeValueTextView = showActivityDialogBox.findViewById(R.id.latitudeValueTextView);
        TextView longitudeValueTextView = showActivityDialogBox.findViewById(R.id.longitudeValueTextView);
        
        activityIDTextView.setText(activity.activityName + " : " + activity.markerID);
        latitudeValueTextView.setText(Double.toString(activity.latitude));
        longitudeValueTextView.setText(Double.toString(activity.longitude));

        // Dismiss dialog box
        cancelButton.setOnClickListener(v -> showActivityDialogBox.dismiss());

        // Remove marker location from firebase
        deleteButton.setOnClickListener(v -> {
            ValueEventListener eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    // Get last location index
                    long lastLocationIndex = dataSnapshot.child(activity.activityID).child("locations").getChildrenCount();

                    // Get values stored in the last index
                    Double latitude = (Double) dataSnapshot.child(activity.activityID).child("locations").child(String.valueOf(lastLocationIndex)).child("latitude").getValue();
                    Double longitude = (Double) dataSnapshot.child(activity.activityID).child("locations").child(String.valueOf(lastLocationIndex)).child("longitude").getValue();

                    // Add values to current index
                    locationBasedActivityTableReference.child(activity.activityID).child("locations").child(String.valueOf(activity.markerID)).child("latitude").setValue(latitude);
                    locationBasedActivityTableReference.child(activity.activityID).child("locations").child(String.valueOf(activity.markerID)).child("longitude").setValue(longitude);

                    // Remove last index entry from firebase
                    locationBasedActivityTableReference.child(activity.activityID).child("locations").child(String.valueOf(lastLocationIndex)).removeValue().addOnSuccessListener(aVoid -> showActivityDialogBox.dismiss());

                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Read failed
                    Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
                }
            };
            locationBasedActivityTableReference.addListenerForSingleValueEvent(eventListener);
        });

        showActivityDialogBox.show();
        return false;
    }



    // Function to setup the map and add custom markers
    @Override
    public void onMapReady(GoogleMap googleMap) {

        // Setup and Customize the map
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(Objects.requireNonNull(this), R.raw.ar_explore_custom_map));
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // Mechanism to add new markers
        mMap.setOnMapClickListener(latLng -> {
            if(!selectedActivityID.equals("NA")) {
                ValueEventListener eventListener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        // Get last location index
                        long lastLocationIndex = dataSnapshot.child(selectedActivityID).child("locations").getChildrenCount() + 1;

                        // Add new marker
                        MapMarker mapMarker = new MapMarker(latLng.latitude, latLng.longitude, "", 0);
                        locationBasedActivityTableReference.child(selectedActivityID).child("locations").child(String.valueOf(lastLocationIndex)).setValue(mapMarker);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        // Read failed
                        Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
                    }
                };
                locationBasedActivityTableReference.addListenerForSingleValueEvent(eventListener);
            }
            else {
                Toast.makeText(this, "Select activity to place it on the Map.", Toast.LENGTH_SHORT).show();
            }
        });

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Clear the current map
                mMap.clear();

                for (DataSnapshot activity : dataSnapshot.getChildren()) {

                    // Get activity ID
                    String activityID = activity.getKey();

                    // Get activity details from firebase
                    String activityName = activity.child("name").getValue().toString();
                    String category = activity.child("category").getValue().toString();

                    // Get activity locations from firebase
                    for (int i = 1; i <= activity.child("locations").getChildrenCount(); i++) {

                        // Check validity of location
                        if(!activity.child("locations").child(String.valueOf(i)).hasChild("latitude") || !activity.child("locations").child(String.valueOf(i)).hasChild("longitude"))
                            break;

                        double latitude = (double) activity.child("locations").child(String.valueOf(i)).child("latitude").getValue();
                        double longitude = (double) activity.child("locations").child(String.valueOf(i)).child("longitude").getValue();

                        Marker newMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title(activityName));
                        newMarker.setTitle(null);
                        newMarker.setTag(new LocationBasedActivity(i, activityID, activityName, latitude, longitude));

                        switch (category) {
                            case "event":
                                newMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.event_marker), 100, 100, false)));
                                break;
                            case "game":
                                newMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.game_marker), 100, 100, false)));
                                break;
                            case "scan":
                                newMarker.setIcon(BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.scan_marker), 100, 100, false)));
                                break;
                        }
                    }

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Read failed
                Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
            }
        };
        locationBasedActivityTableReference.addValueEventListener(eventListener);
    }



    // Gets the current location of the device, and positions the map's camera.
    private void getDeviceLocation() {
        Log.d(TAG,"Device location requested");
        try {
            Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(Objects.requireNonNull(this), task -> {
                if (task.isSuccessful())
                {
                    // Set the map's camera position to the current location of the device.
                    Log.d(TAG,"Location obtained");
                    mLastKnownLocation = task.getResult();

                    if (mLastKnownLocation != null)
                    {
                        Log.d(TAG, "User's location is : " + mLastKnownLocation.getLatitude() + " " + mLastKnownLocation.getLongitude() );
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                    }
                    else {
                        Log.d(TAG, "Last known location is null");
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 10));
                        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                            getDeviceLocation();
                    }
                }
                else
                {
                    Log.d(TAG, "Unable to retrieve location. Using default location instead.");
                    Log.e(TAG, "Exception: %s", task.getException());

                    // Set the map's camera position to the default location.
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                    mMap.getUiSettings().setMyLocationButtonEnabled(false);
                }
            });
        }
        catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    // Updates the map's UI settings based on whether the user has granted location permission.
    private void updateLocationUI() {
        if (mMap != null) {
            try
            {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }
            catch (SecurityException e)
            {
                Log.e("Exception: %s", Objects.requireNonNull(e.getMessage()));
            }
        }
    }



    // Function to manage activity selection popup window
    private void manageSelectActivityPopup()
    {
        // Inflate the popup window layout
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View selectActivityView = inflater.inflate(R.layout.popup_activity_select, null);

        //Setup recycler view within popup window
        List<LocationBasedActivity> activityARList = new ArrayList<>();
        RecyclerView liveUpdatesRecyclerView = selectActivityView.findViewById(R.id.recycler_view);
        SelectActivityAdapter selectActivityAdapter = new SelectActivityAdapter(activityARList, position -> Toast.makeText(this, activityARList.get(position).activityName, Toast.LENGTH_SHORT).show());

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        liveUpdatesRecyclerView.setLayoutManager(mLayoutManager);
        liveUpdatesRecyclerView.setAdapter(selectActivityAdapter);

        // Setup popup window
        final PopupWindow popupWindow = new PopupWindow(selectActivityView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        // Show popup view at the center
        popupWindow.showAtLocation(findViewById(R.id.activitySelectButton) , Gravity.CENTER, 0,0);

        // Button to dismiss popup
        Button button = selectActivityView.findViewById(R.id.quitSelectActivityPopupButton);
        button.setOnClickListener(v -> popupWindow.dismiss());

        //Listen for values on Firebase
        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Read data from firebase
                activityARList.clear();
                for (DataSnapshot activity : dataSnapshot.getChildren()) {
                    String activityID = activity.getKey();
                    String activityName = activity.child("name").getValue().toString();

                    LocationBasedActivity locationBasedActivity = new LocationBasedActivity(activityID, activityName);
                    activityARList.add(locationBasedActivity);
                }
                selectActivityAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Read failed
                Log.d("EAG_FIREBASE_DB", "Failed to read data from Firebase : ", databaseError.toException());
            }
        };
        FirebaseDatabase.getInstance().getReference().child("locationBasedActivityTable").addValueEventListener(eventListener);
    }



    // Needed to handle button on clicks within recycler view
    public interface ClickListener
    {
        void onPositionClicked(int position);
    }

}
