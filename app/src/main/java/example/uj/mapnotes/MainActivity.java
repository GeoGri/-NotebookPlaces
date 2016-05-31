package example.uj.mapnotes;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback {

    public static final String PLACE_NAME = "PLACE_NAME",
                                LAT_LANG = "LAT_LANG";
    public static String Map_Type = "MAP_TYPE_NORMAL";
    private DrawerLayout drawer;
    private GoogleMap mMap;
    private List<Marker> mapPlaces = new ArrayList<>();
    ArrayList<Points> listPlaces = new ArrayList<>();
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        if(savedInstanceState!=null){
            mMap = mapFragment.getMap();
            listPlaces = savedInstanceState.getParcelableArrayList("points");
        }

        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                setMapType(Map_Type);
                setUpMap();
            }
        }else{
            setMapType(Map_Type);
            setUpMap();
        }
    }

    private void setMapType(String type){
        switch (type){
            case "MAP_TYPE_NORMAL":
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case "MAP_TYPE_TERRAIN":
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
            case "MAP_TYPE_SATELLITE":
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case "MAP_TYPE_HYBRID":
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
        }
    }

    private void setUpMap() {
        if(listPlaces!=null)
                for(int i=0;i<listPlaces.size();i++)
                    drawMarkers(listPlaces.get(i).getLat(),listPlaces.get(i).getLang(),listPlaces.get(i).getNamePlace());
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
    public boolean onNavigationItemSelected(MenuItem item) {

        //Checking if the item is in checked state or not, if not make it in checked state
        if(item.isChecked()) item.setChecked(false);
        else item.setChecked(true);

        //Closing drawer on item click
        drawer.closeDrawers();

        switch(item.getItemId()){
            case R.id.nav_my_location:
                if(mMap.getMyLocation()!= null) {
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(new LatLng(
                                    mMap.getMyLocation().getLatitude(),mMap.getMyLocation().getLongitude()))      // Sets the center of the map to Mountain View
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();                   // Creates a CameraPosition from the builder
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    Toast.makeText(this, "Moja lokalizacja", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getApplicationContext(), "Nie mozna nawiązać połącznia z gps",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_save_loc:
                if(mMap.getMyLocation()!= null) addNewMarker(mMap.getMyLocation());
                else Toast.makeText(getApplicationContext(), "Nie mozna nawiązać połącznia z gps",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_remove_markers:
                if(!listPlaces.isEmpty()){
                    listPlaces.clear();
                    mMap.clear();
                    Toast.makeText(getApplicationContext(), "Usunieto punkty",
                            Toast.LENGTH_SHORT).show();
                } else Toast.makeText(getApplicationContext(),"Brak punktów do usuniecia",
                        Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_map_normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                Map_Type = "MAP_TYPE_NORMAL";
                break;
            case R.id.nav_map_hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                Map_Type = "MAP_TYPE_HYBRID";
                break;
            case R.id.nav_map_terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                Map_Type = "MAP_TYPE_TERRAIN";
                break;
            case R.id.nav_map_satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                Map_Type = "MAP_TYPE_SATELLITE";
                break;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                addMarker(latLng);
            }
        });
    }

    public void addNewMarker(Location location){
        addMarker(new LatLng(location.getLatitude(), location.getLongitude()));
    }

    public void addMarker(LatLng latLang){
        Intent newMarker = new Intent(this, AddNewPlace.class);
        Bundle extras = new Bundle();
        extras.putParcelable(LAT_LANG, latLang);
        newMarker.putExtras(extras);
        startActivityForResult(newMarker, 1);
    }

    /*@Override
    public void onMapClick(LatLng latLng) {
        //addMarker(latLng);
        // Creating a marker
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting the position for the marker
        markerOptions.position(latLng);

        // Setting the title for the marker.
        // This will be displayed on taping the marker
        markerOptions.title(latLng.latitude + " : " + latLng.longitude);

        // Clears the previously touched position
        mMap.clear();

        // Animating to the touched position
        mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));

        // Placing a marker on the touched position
        mMap.addMarker(markerOptions);
    }*/

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Bundle date = data.getExtras();
                String namePlace = date.getString(PLACE_NAME);
                LatLng latLng = date.getParcelable(LAT_LANG);

                drawMarker(latLng, namePlace);

                //mMap.addMarker(new MarkerOptions().position(latLng).title(namePlace));
                listPlaces.add(new Points(latLng,namePlace));
                //mapPlaces.add(marker);
            }
        }
    }

    private void drawMarker(LatLng point, String title){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        // Setting latitude and longitude for the marker
        markerOptions.position(point).title(title);

        // Adding marker on the Google Map
        mMap.addMarker(markerOptions);
    }

    private void drawMarkers(double lat, double lang, String title){
        // Creating an instance of MarkerOptions
        MarkerOptions markerOptions = new MarkerOptions();

        LatLng point = new LatLng(lat, lang);
        // Setting latitude and longitude for the marker
        markerOptions.position(point).title(title);

        // Adding marker on the Google Map
        mMap.addMarker(new MarkerOptions().position(point).title(title));
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Adding the pointList arraylist to Bundle
        outState.putParcelableArrayList("points", listPlaces);

        // Saving the bundle
        super.onSaveInstanceState(outState);
    }

    public class Points implements Parcelable{

        String lat;
        String lang;
        String namePlace;

        public Points(LatLng point, String namePlace){
            this.lat = String.valueOf(point.latitude);
            this.lang = String.valueOf(point.longitude);
            this.namePlace = namePlace;
        }

        public Points(Parcel in){
            lat = in.readString();
            lang = in.readString();
            namePlace = in.readString();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            out.writeString(lat);
            out.writeString(lang);
            out.writeString(namePlace);
        }

        public LatLng getPiont(){
            return (new LatLng(Double.parseDouble(lat), Double.parseDouble(lang)));
        }

        public double getLat(){
            return Double.parseDouble(lat);
        }

        public double getLang(){
            return Double.parseDouble(lang);
        }

        public String getNamePlace(){
            return namePlace;
        }

        public final Parcelable.Creator<Points> CREATOR = new Parcelable.Creator<Points>() {
            public Points createFromParcel(Parcel in) {
                return new Points(in);
            }

            public Points[] newArray(int size) {
                return new Points[size];
            }
        };
    }

}
