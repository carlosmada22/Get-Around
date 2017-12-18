package carlosmada22.com.get_around.Vista;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;

import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;

import java.io.FileOutputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import carlosmada22.com.get_around.BaseDeDatos.DBAdapter;
import carlosmada22.com.get_around.R;

/**
 * Created by carlosmada22 on 2/10/17.
 */


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.SnapshotReadyCallback,
        View.OnClickListener {
    static DBAdapter mDBAdapter;

    List<String> nombres = new ArrayList<String>();
    List<Double> latitudes = new ArrayList<Double>();
    List<Double> longitudes = new ArrayList<Double>();
    List<String> descripciones = new ArrayList<String>();
    List<Integer> categorias = new ArrayList<Integer>();
    List<Integer> id = new ArrayList<Integer>();

    public static List<LatLng> positionMarkers = new ArrayList<LatLng>();

    private ImageButton btnClick;
    int category;
    private EditText location_tf;
    private EditText mName;
    private EditText mDescription;
    private Spinner mspinner;
    private EditText etName;
    private EditText etDescription;
    private Spinner spinner;
    private FloatingActionButton fab;
    private ImageButton bList;
    private int lista = -1;
    int lastIdMarker;
    String location = "";
    BitmapDescriptor color;
    String categoria = "";
    Cursor sLista;
    static MapView gMapView;
    static GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Bitmap bitmap;

    Handler myHandler;
    int viewH;
    int viewW;
    LatLng ne;
    LatLng sw;
    float rotation;
    private static double latitud, longitud;

    LatLngBounds curScreen;

    public MapFragment() {

        //android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("onCreate", "onCreate");
        super.onCreate(savedInstanceState);
        myHandler = new Handler();


        mDBAdapter = new DBAdapter((getContext()));
        mDBAdapter.open();


        id = new ArrayList<Integer>();
        nombres = new ArrayList<String>();
        latitudes = new ArrayList<Double>();
        longitudes = new ArrayList<Double>();
        descripciones = new ArrayList<String>();
        categorias = new ArrayList<Integer>();

        Log.i("SavedInstanceState", ""+savedInstanceState);


        if (savedInstanceState == null) {
            Log.i("Primera vez", "onCreate");
            Cursor c = null;
            c = mDBAdapter.getMarkerLista();

            if (c.moveToFirst()) {
                do {
                    id.add(c.getInt(0));
                    Log.i("id", "" + c.getInt(0));

                    nombres.add(c.getString(1));
                    Log.i("nombre", c.getString(1));

                    latitudes.add(c.getDouble(2));
                    Log.i("latitudes", "" + c.getDouble(2));

                    longitudes.add(c.getDouble(3));
                    Log.i("longitudes", "" + c.getDouble(3));

                    descripciones.add(c.getString(4));
                    Log.i("descripciones", c.getString(4));

                    categorias.add(c.getInt(5));
                    Log.i("categorias", "" + c.getInt(5));
                } while (c.moveToNext());


            }

            LatLng location;
            double latitude, longitude;
            for (int i = 0; i < id.size(); i++) {
                latitude = latitudes.get(i);
                longitude = longitudes.get(i);
                location = new LatLng(latitude, longitude);
                positionMarkers.add(location);
            }
            setMarkers();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map, container, false);
        MapsInitializer.initialize(this.getContext());
        gMapView = (MapView) view.findViewById(R.id.mapView);
        gMapView.onCreate(savedInstanceState);
        gMapView.getMapAsync(this);
        location_tf = (EditText) view.findViewById(R.id.TFaddress);
        btnClick = (ImageButton) view.findViewById(R.id.Bsearch);
        btnClick.setOnClickListener(this);
        if (!hasPermissions(getActivity(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveScreen();


            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                view.post(new Runnable() {
                    public void run() {
                        viewH = view.getHeight(); //height is ready
                        viewW = view.getWidth(); //widht is ready
                    }
                });
            }
        });

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        gMapView.onResume();
        Log.i("onResume", "onResume");
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (gMap != null) {
            gMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            }
        }
        if(gMap != null) {
            imprimeVMarkers();
            gMap.clear();
            setMarkers();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        gMapView.onPause();
        Log.i("onPause", "onPause");
        imprimeVMarkers();
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        if (gMap != null) {
            gMap.setMyLocationEnabled(true);
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                gMap.animateCamera(CameraUpdateFactory.zoomTo(13));
            }
            gMap.clear();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        gMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        gMapView.onLowMemory();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        gMapView.onSaveInstanceState(outState);
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.i("onMapReady", "onMapReady");


        gMap = googleMap;



        gMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                setMarkers();
                Log.i("mapTypeMAP", "" + MainActivity.mapType);

                if(MainActivity.mapType) gMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                if(!MainActivity.eye) MainActivity.menu.findItem(R.id.show).setIcon(R.drawable.ic_action_hide);
            }
        });

        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                gMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }



        gMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_download));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveScreen();
                    }
                });
                Log.i("LatLng", "" + latLng);
            }
        });
        gMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(final LatLng point) {
                lista = -1;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_custom, null);
                mName = (EditText) mView.findViewById(R.id.etName);
                mDescription = (EditText) mView.findViewById(R.id.etDescription);
                mspinner = (Spinner) mView.findViewById(R.id.spColor);
                bList = (ImageButton) mView.findViewById(R.id.Badd);
                mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        switch (i) {
                            case 0:
                                categoria = "Alojamiento";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                                break;
                            case 1:
                                categoria = "Restaurante";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                                break;
                            case 2:
                                categoria = "Comercio";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                                break;
                            case 3:
                                categoria = "Estacion de servicio";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                                break;
                            case 4:
                                categoria = "Estacion de tren/autobus";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                                break;
                            case 5:
                                categoria = "Aeropuerto";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                                break;
                            case 6:
                                categoria = "Museo";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                                break;
                            case 7:
                                categoria = "Iglesia";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                                break;
                            case 8:
                                categoria = "Geografico";
                                color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                                break;

                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                Button mAdd = (Button) mView.findViewById(R.id.btnAdd);
                Button mCancel = (Button) mView.findViewById(R.id.btnCancel);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();
                mAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mName.getText().toString().isEmpty()) {
                            MarkerOptions marker = new MarkerOptions()
                                    .position(new LatLng(point.latitude, point.longitude))
                                    .title(mName.getText().toString())
                                    .snippet(mDescription.getText().toString())
                                    .icon(color);
                            gMap.addMarker(marker);
                            positionMarkers.add(new LatLng(point.latitude,point.longitude));
                            Toast.makeText(getContext(),
                                    "Punto de interés añadido",
                                    Toast.LENGTH_SHORT).show();
                            //addMarkerDB(point);
                            lastIdMarker = mDBAdapter.insertarMarker(mName.getText().toString(), point.latitude, point.longitude, mDescription.getText().toString(), (int) mspinner.getSelectedItemId());
                            ListFragment.loadList();
                            int id_marker = lastIdMarker;
                            if (mDBAdapter.checkLista(lista) && mDBAdapter.crearListaMarker(id_marker, lista)) {
                                Log.i("id_marker_map", "" + id_marker);
                                Toast.makeText(getContext(),
                                        "Marker añadido a la lista",
                                        Toast.LENGTH_SHORT).show();

                            }
                            Cursor c = mDBAdapter.getListMarkers(lista);
                            if (c.moveToFirst()) {
                                do {
                                    Log.i("id_marker", "" + c.getInt(0));
                                    Log.i("name_marker", "" + c.getString(1));
                                } while (c.moveToNext());
                            }
                            dialog.dismiss();
                        } else {
                            Toast.makeText(getContext(),
                                    "Campo *Nombre* obligatorio",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                bList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Cursor c = null;
                        Log.i("listas", "c=null");
                        c = mDBAdapter.getListaListas();
                        CharSequence lists[] = new CharSequence[c.getCount()];
                        int pos = 0;
                        if (c.moveToFirst()) {
                            do {
                                lists[pos] = c.getString(1);
                                Log.i("listas", c.getString(1));
                                pos++;
                            } while (c.moveToNext());
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Elige una lista");
                        builder.setItems(lists, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sLista = mDBAdapter.getListaListas();
                                if (sLista.moveToFirst()) {
                                    for (int k = 0; k < which; k++) {
                                        sLista.moveToNext();
                                    }
                                }
                                lista = sLista.getInt(0);
                                Log.i("id_lista_map", "" + lista);
                            }
                        });
                        builder.show();
                    }
                });
            }
        });
        gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker mMarker) {
                fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_edit_light));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onFabClick(mMarker);
                    }
                });


                return false;
            }
        });

        gMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                LatLngBounds bounds = gMap.getProjection().getVisibleRegion().latLngBounds;
                ne = bounds.northeast;
                sw = bounds.southwest;

                rotation = gMap.getCameraPosition().bearing;
                Log.i("LatLng NE moved", ""+ ne);
                Log.i("LatLng SW moved", ""+ sw);
            }
        });






    }




    public static GoogleMap getgMap() {
        return gMap;
    }

    public static void setgMap(GoogleMap gMap) {
        MapFragment.gMap = gMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission((Activity)getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        gMap.setMyLocationEnabled(true);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());


        //move map camera
        gMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        gMap.animateCamera(CameraUpdateFactory.zoomTo(11));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE = 200;
    public static final int MY_PERMISSIONS_REQUEST_STORAGE_READ = 201;



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    gMap.setMyLocationEnabled(true);

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(getContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        gMap.setMyLocationEnabled(true);
                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.
                    Toast.makeText(getActivity(), "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_STORAGE);
                    }
                }
            }
            case MY_PERMISSIONS_REQUEST_STORAGE_READ: {
                // If request is cancelled, the result arrays are empty.
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {

                        // Show an expanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.

                    } else {

                        // No explanation needed, we can request the permission.

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                MY_PERMISSIONS_REQUEST_STORAGE_READ);
                    }
                }
            }

        }
    }

    public void onFabClick(final Marker mMarker) {
        lista = -1;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit, null);
        TextView tvName = (TextView) view.findViewById(R.id.tvName);
        etName = (EditText) view.findViewById(R.id.etName);
        etDescription = (EditText) view.findViewById(R.id.etDescription);
        spinner = (Spinner) view.findViewById(R.id.spColor);
        ImageButton bDelete = (ImageButton) view.findViewById(R.id.Bsearch);
        ImageButton bList = (ImageButton) view.findViewById(R.id.Badd);
        Button bModify = (Button) view.findViewById(R.id.btnModify);
        Button bCancel = (Button) view.findViewById(R.id.btnCancel);
        tvName.setText(mMarker.getTitle());
        etName.setText(mMarker.getTitle());
        etDescription.setText(mMarker.getSnippet());
        spinner.setSelection(getCategoria(mMarker));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = i;
                switch (i) {
                    case 0:
                        categoria = "Alojamiento";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                        break;
                    case 1:
                        categoria = "Restaurante";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                        break;
                    case 2:
                        categoria = "Comercio";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                        break;
                    case 3:
                        categoria = "Estacion de servicio";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                        break;
                    case 4:
                        categoria = "Estacion de tren/autobus";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                        break;
                    case 5:
                        categoria = "Aeropuerto";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                        break;
                    case 6:
                        categoria = "Museo";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                        break;
                    case 7:
                        categoria = "Iglesia";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                        break;
                    case 8:
                        categoria = "Geografico";
                        color = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                        break;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        mBuilder.setView(view);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        bList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = null;
                Log.i("listas", "c=null");
                c = mDBAdapter.getListaListas();
                if (c.getCount() != 0 && c !=null) {
                    CharSequence lists[] = new CharSequence[c.getCount()];
                    int pos = 0;
                    if (c.moveToFirst()) {
                        do {
                            lists[pos] = c.getString(1);
                            Log.i("listas", c.getString(1));
                            pos++;
                        } while (c.moveToNext());
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Elige una lista");
                    builder.setItems(lists, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sLista = mDBAdapter.getListaListas();
                            if (sLista.moveToFirst()) {
                                for (int k = 0; k < which; k++) {
                                    sLista.moveToNext();
                                }
                            }
                            lista = sLista.getInt(0);

                            Log.i("id_lista_map", "" + lista);
                        }
                    });
                    builder.show();
                }
                else{
                    CharSequence lists[] = {"¿Cómo hacerlo?"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("No has añadido ninguna lista");
                    builder.setItems(lists, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(getContext(),
                                    "Para crear una lista, en la pestaña de listas (izquierda) pulsa sobre el botón (+), dale un nombre y pulsa en 'Añadir'",
                                    Toast.LENGTH_LONG).show();

                        }
                    });
                    builder.show();
                }
            }
        });

        bModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!etName.getText().toString().isEmpty()) {
                    MarkerOptions marker = new MarkerOptions()
                            .position(mMarker.getPosition())
                            .title(etName.getText().toString())
                            .snippet(etDescription.getText().toString())
                            .icon(color);
                    mMarker.remove();
                    gMap.addMarker(marker);
                    ListFragment.loadList();
                    int id_marker;
                    if (mDBAdapter.checkMarker(marker.getPosition().latitude, marker.getPosition().longitude)) {
                        id_marker = mDBAdapter.getIdMarker(marker.getPosition().latitude, marker.getPosition().longitude);
                        updateMarkerDB(mMarker.getPosition());
                    } else {
                        Cursor c = mDBAdapter.getMarkerLista();
                        c.moveToLast();
                        id_marker = c.getInt(0) + 1;
                        mDBAdapter.insertarMarker(etName.getText().toString(), marker.getPosition().latitude, marker.getPosition().longitude, etDescription.getText().toString(), category);
                    }
                    if (mDBAdapter.checkLista(lista) && mDBAdapter.crearListaMarker(id_marker, lista)) {
                        Toast.makeText(getContext(),
                                "Marker añadido a la lista",
                                Toast.LENGTH_SHORT).show();

                    }
                    fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_download));
                    fab.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            saveScreen();
                        }
                    });
                    dialog.dismiss();
                } else {
                    Toast.makeText(getContext(),
                            "Campo *Nombre* obligatorio",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                build.setTitle("¿Deseas eliminar el punto de interés?");
                build.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int which) {
                        int id_marker = mDBAdapter.getIdMarker(mMarker.getPosition().latitude, mMarker.getPosition().longitude);
                        mMarker.remove();
                        mDBAdapter.deleteMarker(mMarker.getPosition().latitude, mMarker.getPosition().longitude);
                        mDBAdapter.deleteMarkerFromAll(id_marker);
                        positionMarkers.remove(mMarker.getPosition());
                        ListFragment.loadList();
                        dialogo.dismiss();
                        dialog.dismiss();
                        fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_download));
                        fab.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                saveScreen();

                            }
                        });
                    }
                });


                build.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int which) {
                        dialogo.dismiss();
                    }
                });
                build.show();
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_download));
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        saveScreen();

                    }
                });
                dialog.dismiss();
            }
        });

    }

    public void onClick(View view) {

        location = location_tf.getText().toString();
        List<Address> addressList = null;
        if (!location.equals("")) {
            Geocoder geocoder = new Geocoder(getContext());
            try {
                addressList = geocoder.getFromLocationName(location, 1);


            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList != null && !addressList.isEmpty() && addressList.get(0) != null) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                gMap.addMarker(new MarkerOptions().position(latLng).title(location_tf.getText().toString()));
                gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            } else {
                Toast.makeText(getContext(),
                        "No se ha encontrado ningún resultado",
                        Toast.LENGTH_SHORT).show();

            }

        } else Toast.makeText(getContext(),
                "Introduzca una dirección",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {

    }

    public int getCategoria(Marker marker) {
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;
        int categoria = mDBAdapter.getCategoria(lat, lon);
        return categoria;
    }

    private class addMarkerBDTask extends AsyncTask<Void, Void, Boolean> {
        String nombre, descripcion;
        double latitud, longitud;
        int categoria;

        public addMarkerBDTask(String nombre, double latitud, double longitud, String descripcion, int categoria) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.latitud = latitud;
            this.longitud = longitud;
            this.categoria = categoria;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            int themark = mDBAdapter.insertarMarker(nombre, latitud, longitud, descripcion, categoria);
            return (themark > 0);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            getActivity().setResult(Activity.RESULT_OK);

        }
    }

    private void updateMarkerDB(LatLng point) {
        String nombre = "";
        if (etName != null) {
            nombre = etName.getText().toString();
        }
        if (TextUtils.isEmpty(nombre)) {
            mName.setError("Introduzca un nombre");
        }

        new updateMarkerBDTask(nombre, point.latitude, point.longitude, etDescription.getText().toString(), (int) spinner.getSelectedItemId()).execute();

    }

    private class updateMarkerBDTask extends AsyncTask<Void, Void, Boolean> {
        String nombre, descripcion;
        double latitud, longitud;
        int categoria;

        public updateMarkerBDTask(String nombre, double latitud, double longitud, String descripcion, int categoria) {
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.latitud = latitud;
            this.longitud = longitud;
            this.categoria = categoria;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            mDBAdapter.updateMarker(nombre, latitud, longitud, descripcion, categoria);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            getActivity().setResult(Activity.RESULT_OK);

        }
    }

    public static BitmapDescriptor markerColor(int categoria) {
        BitmapDescriptor colour = null;
        switch (categoria) {
            case 0:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE);
                break;
            case 1:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED);
                break;
            case 2:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE);
                break;
            case 3:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN);
                break;
            case 4:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE);
                break;
            case 5:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE);
                break;
            case 6:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
                break;
            case 7:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW);
                break;
            case 8:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
                break;
            default:
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA);


        }
        return colour;
    }

    public Bitmap captureScreen() {
        CameraPosition oldPos = gMap.getCameraPosition();
        CameraPosition pos = CameraPosition.builder(oldPos).bearing(0).build();
        gMap.moveCamera(CameraUpdateFactory.newCameraPosition(pos));
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {

            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                bitmap = snapshot;
            }
        };

        gMap.snapshot(callback);
        return bitmap;
    }

    public void saveScreen() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        gMap.setMyLocationEnabled(false);
        int time=500; // in milliseconds

        Handler h=new Handler();

        h.postDelayed(new Runnable() {

            @Override
            public void run() {

                captureScreen();
                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                View crear = getActivity().getLayoutInflater().inflate(R.layout.save_screen_dialog, null);
                final EditText nombre = (EditText) crear.findViewById(R.id.nombre_mapa) ;
                build.setTitle("Guardar mapa");

                build.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int which) {
                        File folder = new File(getContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/Maps/");
                        if (!folder.exists()) {
                            folder.mkdirs();
                        }
                        String fileNameNOJPG = nombre.getText().toString();

                        String final_name = compruebaMapa(fileNameNOJPG);

                        if(!final_name.isEmpty()) {



                            if (createDirectoryAndSaveFile(bitmap, final_name)) {
                                Toast.makeText(getContext(),
                                        "Descarga realizada",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(),
                                        "Descarga fallida",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else Toast.makeText(getContext(),
                                "Nombre obligatorio",
                                Toast.LENGTH_SHORT).show();
                    } });


                build.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int which) {
                        dialogo.dismiss();
                    } });
                build.setView(crear);
                AlertDialog nueva_lista = build.create();
                nueva_lista.show();
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    return;
                }
                gMap.setMyLocationEnabled(true);

            }

        },time);

    }


    private boolean createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        String final_name = fileName + ".jpg";
        File direct = new File(Environment.getExternalStorageDirectory() + "/Maps");
        boolean creado = false;
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Maps/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Maps/"), final_name);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            if(imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out)) creado = true;
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveValues(fileName);
        return creado;
    }

    public void saveValues (String fileName){


        File f = new File("/storage/emulated/0/Maps/" + fileName);


        curScreen = gMap.getProjection().getVisibleRegion().latLngBounds;



        double latC = curScreen.getCenter().latitude;
        double lonC = curScreen.getCenter().longitude;
        float zoom = gMap.getCameraPosition().zoom;
        double latne = ne.latitude;
        double longne = ne.longitude;
        double latsw = sw.latitude;
        double longsw = sw.longitude;


        Log.i("LatlngCenter", "" + latC + "///" + lonC);

        Log.i("latne", "" + latne);
        Log.i("longne", "" + longne);
        Log.i("latsw", "" + latsw);
        Log.i("longsw", "" + longsw);


        int id_map = mDBAdapter.crearMapa(fileName,latne,longne,latsw,longsw,rotation,latC,lonC);
        Log.i("id_map",""+id_map);


    }

    public static String compruebaMapa(String name){
        String final_name = name;
        int i=0;

        while (mDBAdapter.checkMapa(final_name)){
            final_name = name + "(" + i + ")";
            i++;
        }
        return final_name;
    }

    public static double getLatitud() {
        return latitud;
    }

    public static void setLatitud(double latitud) {
        MapFragment.latitud = latitud;
    }

    public static double getLongitud() {
        return longitud;
    }

    public static void setLongitud(double longitud) {
        MapFragment.longitud = longitud;
    }


    public static void setMarkers(){
        if (gMap != null) {
            gMap.clear();
            Cursor c = null;
            for (int i = 0; i < positionMarkers.size(); i++) {
                c = mDBAdapter.getMarker(positionMarkers.get(i).latitude, positionMarkers.get(i).longitude);
                if (c.moveToFirst()) {
                    MarkerOptions marker = new MarkerOptions()
                            .position(new LatLng(c.getDouble(2), c.getDouble(3)))
                            .title(c.getString(1))
                            .snippet(c.getString(4))
                            .icon(markerColor(c.getInt(5)));
                    gMap.addMarker(marker);
                }
            }
        }
    }

    public static void imprimeVMarkers(){
        for (int i=0; i<positionMarkers.size(); i++){
            Log.i("lat" + i, ""+positionMarkers.get(i).latitude);
            Log.i("lon" + i, ""+positionMarkers.get(i).longitude);
        }
    }
}
