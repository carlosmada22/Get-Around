package carlosmada22.com.get_around;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.google.android.gms.internal.adb;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        GoogleMap.SnapshotReadyCallback,
        View.OnClickListener{
    DBAdapter mDBAdapter;

    List<String> nombres = new ArrayList<String>();
    List<Double> latitudes = new ArrayList<Double>();
    List<Double> longitudes = new ArrayList<Double>();
    List<String> descripciones = new ArrayList<String>();
    List<Integer> categorias = new ArrayList<Integer>();
    List<Integer> id = new ArrayList<Integer>();

    private ImageButton btnClick;
    int category;
    private Button btnType;
    public static int nMap = 0;
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
    LatLng ltln;
    File out;
    static MapView gMapView;
    static GoogleMap gMap;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    Bitmap bitmap;
    Bitmap bm;
    boolean inlista;

    public MapFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        MapsInitializer.initialize(this.getContext());
        gMapView = (MapView) view.findViewById(R.id.mapView);
        gMapView.onCreate(savedInstanceState);
        gMapView.getMapAsync(this);
        location_tf = (EditText)view.findViewById(R.id.TFaddress);
        btnClick = (ImageButton) view.findViewById(R.id.Bsearch) ;
        btnClick.setOnClickListener(this);
        if(!hasPermissions(getActivity(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
        }
        /*btnType = (Button) view.findViewById(R.id.Btype);
        btnType.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(gMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL)
                {
                    gMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
                else
                    gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        });*/
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveScreen();


            }
        });



        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        gMapView.onResume();
        //Si estabamos en el mapa, volvemos al mapa
        /*if (ListFragment.currentTabPosition == 1) {
            TabLayout.Tab tab = MainActivity.tabLayout.getTabAt(1);
            tab.select();
        }*/
    }

    @Override
    public void onPause() {
        super.onPause();
        gMapView.onPause();
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



        gMap = googleMap;

        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                gMap.setMyLocationEnabled(true);
            }
        }
        else {
            buildGoogleApiClient();
            gMap.setMyLocationEnabled(true);
        }


        mDBAdapter = new DBAdapter((getContext()));
        mDBAdapter.open();


        id = new ArrayList<Integer>();
        nombres = new ArrayList<String>();
        latitudes = new ArrayList<Double>();
        longitudes = new ArrayList<Double>();
        descripciones = new ArrayList<String>();
        categorias = new ArrayList<Integer>();

        Cursor c = null;
        c = mDBAdapter.getMarkerLista();

        if (c.moveToFirst()) {
            do {
                id.add(c.getInt(0));
                Log.i("id",""+c.getInt(0));

                nombres.add(c.getString(1));
                Log.i("nombre",c.getString(1));

                latitudes.add(c.getDouble(2));
                Log.i("latitudes",""+c.getDouble(2));

                longitudes.add(c.getDouble(3));
                Log.i("longitudes",""+c.getDouble(3));

                descripciones.add(c.getString(4));
                Log.i("descripciones",c.getString(4));

                categorias.add(c.getInt(5));
                Log.i("categorias",""+c.getInt(5));
            } while (c.moveToNext());

        }
        LatLng location;
        double latitude, longitude;
        String name, desc;
        BitmapDescriptor colour;
        for (int i=0; i<id.size();i++){
            latitude = latitudes.get(i);
            longitude = longitudes.get(i);
            location = new LatLng(latitude, longitude);
            name = nombres.get(i);
            desc = descripciones.get(i);
            colour = markerColor(categorias.get(i));
            gMap.addMarker(new MarkerOptions().position(location).title(name).snippet(desc).icon(colour));
        }
        /*mCursor.moveToFirst();
        if(mCursor.getCount() > 0) do {
            latitude = mCursor.getDouble(mCursor
                    .getColumnIndex(Marker_Tabla.MarkerEntry.LAT));
            longitude = mCursor.getDouble(mCursor
                    .getColumnIndex(Marker_Tabla.MarkerEntry.LON));
            name = mCursor.getString(mCursor
                    .getColumnIndex(Marker_Tabla.MarkerEntry.NAME));
            desc = mCursor.getString(mCursor
                    .getColumnIndex(Marker_Tabla.MarkerEntry.DESCRIPTION));

            LatLng location = new LatLng(latitude, longitude);
            Log.i("Ubicaciones",location.toString());

            gMap.addMarker(new MarkerOptions().position(location).title(name));


        } while (mCursor.moveToNext());*/

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
                mspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

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
                        if(!mName.getText().toString().isEmpty()){
                            MarkerOptions marker = new MarkerOptions()
                                    .position(new LatLng(point.latitude, point.longitude))
                                    .title(mName.getText().toString())
                                    .snippet(mDescription.getText().toString())
                                    .icon(color);
                            gMap.addMarker(marker);
                            Toast.makeText(getContext(),
                                    "Punto de interés añadido",
                                    Toast.LENGTH_SHORT).show();
                            //addMarkerDB(point);
                            lastIdMarker = mDBAdapter.insertarMarker(mName.getText().toString(), point.latitude, point.longitude, mDescription.getText().toString(), (int)mspinner.getSelectedItemId());
                            ListFragment.loadList();
                            int id_marker = lastIdMarker;
                            if(mDBAdapter.checkLista(lista)&&mDBAdapter.crearListaMarker(id_marker, lista)){
                                Log.i("id_marker_map", "" + id_marker);
                                Toast.makeText(getContext(),
                                        "Marker añadido a la lista",
                                        Toast.LENGTH_SHORT).show();

                            }
                            Cursor c = mDBAdapter.getListMarkers(lista);
                            if (c.moveToFirst()){
                                do{
                                    Log.i("id_marker", "" + c.getInt(0));
                                    Log.i("name_marker", "" + c.getString(1));
                                }while (c.moveToNext());
                            }
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getContext(),
                                    "Campo *Nombre* obligatorio",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view){
                        dialog.dismiss();
                    }
                });

                bList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Cursor c =null;
                        Log.i("listas", "c=null");
                        c = mDBAdapter.getListaListas();
                        CharSequence lists[] = new CharSequence[c.getCount()];
                        int pos = 0;
                        if (c.moveToFirst()){
                            do{
                                lists[pos] = c.getString(1);
                                Log.i("listas",c.getString(1));
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
                                    for (int k=0;k<which;k++){
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
                /*AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_edit, null);
                TextView tvName = (TextView) view.findViewById(R.id.tvName);
                etName = (EditText) view.findViewById(R.id.etName);
                etDescription = (EditText) view.findViewById(R.id.etDescription);
                spinner = (Spinner) view.findViewById(R.id.spColor);
                ImageButton bDelete = (ImageButton) view.findViewById(R.id.Bsearch);
                Button bModify = (Button) view.findViewById(R.id.btnModify);
                Button bCancel = (Button) view.findViewById(R.id.btnCancel);
                tvName.setText(mMarker.getTitle());
                etName.setText(mMarker.getTitle());
                etDescription.setText(mMarker.getSnippet());
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

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
                mBuilder.setView(view);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                bModify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!etName.getText().toString().isEmpty()){
                            MarkerOptions marker = new MarkerOptions()
                                    .position(mMarker.getPosition())
                                    .title(etName.getText().toString())
                                    .snippet(etDescription.getText().toString())
                                    .icon(color);
                            mMarker.remove();
                            gMap.addMarker(marker);
                            Toast.makeText(getContext(),
                                    "Punto de interés añadido",
                                    Toast.LENGTH_SHORT).show();
                            updateMarkerDB(mMarker.getPosition());
                            //addMarkerDB(marker.getPosition());- Metodo modificar en base de datos
                            dialog.dismiss();
                        }else{
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
                                mMarker.remove();
                                mDBAdapter.deleteMarker(mMarker.getPosition().latitude,mMarker.getPosition().longitude);
                                dialogo.dismiss();
                                dialog.dismiss();
                                //mDBAdapter.deleteMarker() --> Metodo eliminar marker base de datos
                            } });


                        build.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogo, int which) {
                                dialogo.dismiss();
                            } });
                        build.show();
                    }
                });*/

                return false;
            }
        });
    }


//                mBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//
//                    }
//                });
//
//                mBuilder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                    }
//                });


//                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (!mEmail.getText().toString().isEmpty() && !mPassword.getText().toString().isEmpty()) {
//                            Toast.makeText(MainActivity.this,
//                                    R.string.success_login_msg,
//                                    Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(MainActivity.this, Main2Activity.class));
//                            dialog.dismiss();
//                        } else {
//                            Toast.makeText(MainActivity.this,
//                                    R.string.error_login_msg,
//                                    Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });


                /*MarkerOptions marker = new MarkerOptions()
                        .position(new LatLng(point.latitude, point.longitude))
                        .title("New Marker");
                gMap.addMarker(marker);*/

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
        if (ContextCompat.checkSelfPermission(getContext(),
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

        //Place current location marker
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = gMap.addMarker(markerOptions);

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
    public boolean checkLocationPermission(){
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @TargetApi(23)
    protected void askPermissions() {
        String[] permissions = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"
        };
        ActivityCompat.requestPermissions(getActivity(),permissions,MY_PERMISSIONS_REQUEST_STORAGE);
    }

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



            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    public void onFabClick (final Marker mMarker){
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){

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
                Cursor c =null;
                Log.i("listas", "c=null");
                c = mDBAdapter.getListaListas();
                CharSequence lists[] = new CharSequence[c.getCount()];
                int pos = 0;
                if (c.moveToFirst()){
                    do{
                        lists[pos] = c.getString(1);
                        Log.i("listas",c.getString(1));
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
                            for (int k=0;k<which;k++){
                                sLista.moveToNext();
                            }
                        }
                        lista = sLista.getInt(0);
                        //if (lista != "") inlista = true;
                        Log.i("id_lista_map", "" + lista);
                    }
                });
                builder.show();
            }
        });

        bModify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etName.getText().toString().isEmpty()){
                    MarkerOptions marker = new MarkerOptions()
                            .position(mMarker.getPosition())
                            .title(etName.getText().toString())
                            .snippet(etDescription.getText().toString())
                            .icon(color);
                    mMarker.remove();
                    gMap.addMarker(marker);
                    ListFragment.loadList();
                    int id_marker;
                    if (mDBAdapter.checkMarker(marker.getPosition().latitude,marker.getPosition().longitude)){
                        id_marker = mDBAdapter.getIdMarker(marker.getPosition().latitude,marker.getPosition().longitude);
                        updateMarkerDB(mMarker.getPosition());
                    }
                    else{
                        Cursor c = mDBAdapter.getMarkerLista();
                        c.moveToLast();
                        id_marker = c.getInt(0) +1;
                        mDBAdapter.insertarMarker(etName.getText().toString(),marker.getPosition().latitude,marker.getPosition().longitude,etDescription.getText().toString(), category);
                    }
                    if(mDBAdapter.checkLista(lista)&&mDBAdapter.crearListaMarker(id_marker, lista)){
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
                    //addMarkerDB(marker.getPosition());- Metodo modificar en base de datos
                    dialog.dismiss();
                }else{
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
                        int id_marker = mDBAdapter.getIdMarker(mMarker.getPosition().latitude,mMarker.getPosition().longitude);
                        mMarker.remove();
                        mDBAdapter.deleteMarker(mMarker.getPosition().latitude,mMarker.getPosition().longitude);
                        mDBAdapter.deleteMarkerFromAll(id_marker);
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
                        //mDBAdapter.deleteMarker() --> Metodo eliminar marker base de datos
                    } });


                build.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int which) {
                        dialogo.dismiss();
                        //fab.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.ic_action_download));
                    } });
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

    public void onClick(View view)
    {

        location = location_tf.getText().toString();
        List<Address> addressList = null;
        if(!location.equals(""))
        {
            Geocoder geocoder = new Geocoder(getContext());
            try {
                addressList = geocoder.getFromLocationName(location , 1);


            } catch (IOException e) {
                e.printStackTrace();
            }

            if (addressList != null && !addressList.isEmpty() && addressList.get(0) != null ) {
                Address address = addressList.get(0);
                LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                gMap.addMarker(new MarkerOptions().position(latLng).title(location_tf.getText().toString()));
                gMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            }
            else {
                Toast.makeText(getContext(),
                        "No se ha encontrado ningún resultado",
                        Toast.LENGTH_SHORT).show();

            }

        }
    }
    private void addMarkerDB(LatLng point){
        String nombre = "";
        if (mName!= null){
            nombre = mName.getText().toString();
        }
        if(TextUtils.isEmpty(nombre)){
            mName.setError("Introduzca un nombre");
        }

        new addMarkerBDTask(nombre, point.latitude, point.longitude, mDescription.getText().toString(), (int)mspinner.getSelectedItemId()).execute();

    }

    @Override
    public void onSnapshotReady(Bitmap bitmap) {

    }

    public int getCategoria(Marker marker){
        double lat = marker.getPosition().latitude;
        double lon = marker.getPosition().longitude;
        int categoria = mDBAdapter.getCategoria(lat, lon);
        return categoria;
    }

    private class addMarkerBDTask extends AsyncTask<Void, Void, Boolean>{
        String nombre, descripcion;
        double latitud, longitud;
        int categoria;

        public addMarkerBDTask(String nombre,double latitud, double longitud, String descripcion, int categoria){
            this.nombre=nombre;
            this.descripcion=descripcion;
            this.latitud=latitud;
            this.longitud=longitud;
            this.categoria=categoria;
        }
        @Override
        protected Boolean doInBackground(Void... voids){
            int themark = mDBAdapter.insertarMarker(nombre,latitud,longitud,descripcion,categoria);
            return (themark > 0);
        }
        @Override
        protected void onPostExecute(Boolean bool){
            getActivity().setResult(Activity.RESULT_OK);

        }
    }
    private void updateMarkerDB(LatLng point){
        String nombre = "";
        if (etName != null){
            nombre = etName.getText().toString();
        }
        if(TextUtils.isEmpty(nombre)){
            mName.setError("Introduzca un nombre");
        }

        new updateMarkerBDTask(nombre, point.latitude, point.longitude, etDescription.getText().toString(), (int)spinner.getSelectedItemId()).execute();

    }

    private class updateMarkerBDTask extends AsyncTask<Void, Void, Boolean>{
        String nombre, descripcion;
        double latitud, longitud;
        int categoria;

        public updateMarkerBDTask(String nombre,double latitud, double longitud, String descripcion, int categoria){
            this.nombre=nombre;
            this.descripcion=descripcion;
            this.latitud=latitud;
            this.longitud=longitud;
            this.categoria=categoria;
        }
        @Override
        protected Boolean doInBackground(Void... voids){
            mDBAdapter.updateMarker(nombre,latitud,longitud,descripcion,categoria);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){
            getActivity().setResult(Activity.RESULT_OK);

        }
    }

    public static BitmapDescriptor markerColor (int categoria){
        BitmapDescriptor colour = null;
        switch (categoria){
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
    public Bitmap captureScreen()
    {
        GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback()
        {

            @Override
            public void onSnapshotReady(Bitmap snapshot)
            {
                // TODO Auto-generated method stub
                bitmap = snapshot;
                /*File folder = new File(getContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/Maps/");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                String fileName = "Map"+ nMap + ".jpg";
                //File file = new File (new File(getContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/Maps/"), fileName);
                nMap++;
                if(createDirectoryAndSaveFile(bitmap,fileName)){
                    Toast.makeText(getContext(),
                            "Descarga realizada",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),
                            "Descarga fallida",
                            Toast.LENGTH_SHORT).show();
                }
                    /*String saved = MediaStore.Images.Media.insertImage(getContext().getApplicationContext().getContentResolver(), bitmap, "" + nMap + ".jpg", "screen");
                    //addImageToGallery(getContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/Maps", getContext().getApplicationContext());
                    if(saved!=null){
                        Toast.makeText(getContext(),
                                "Descarga realizada",
                                Toast.LENGTH_SHORT).show();
                    }
                    else{
                        Toast.makeText(getContext(),
                                "Error en la descarga",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Do something else on failure
                }

                /*String filepath = getContext().getApplicationContext().getFilesDir().getAbsolutePath().toString() + "/Maps/";
                filepath += "Map.jpg";
                //File myPath = new File(extr, "Map.jpg");
                FileOutputStream fos = null;*/
                    /*fos = new FileOutputStream(new File(filepath));
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                    fos.flush();
                    fos.close();*/

            }
        };

        gMap.snapshot(callback);
        return bitmap;
    }

    public void saveScreen(){
        captureScreen();
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        View crear = getActivity().getLayoutInflater().inflate(R.layout.save_screen_dialog, null);
        final EditText nombre = (EditText) crear.findViewById(R.id.nombre_mapa) ;
        ImageView imageView= (ImageView) crear.findViewById(R.id.selectedImage);
        //imageView.setImageBitmap(bitmap);
        build.setTitle("Guardar mapa");

        build.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int which) {
                File folder = new File(getContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/Maps/");
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                String fileName = nombre.getText().toString() + ".jpg";
                //File file = new File (new File(getContext().getApplicationContext().getFilesDir().getAbsolutePath() + "/Maps/"), fileName);
                if(createDirectoryAndSaveFile(bitmap,fileName)){
                    Toast.makeText(getContext(),
                            "Descarga realizada",
                            Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getContext(),
                            "Descarga fallida",
                            Toast.LENGTH_SHORT).show();
                }
            } });


        build.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int which) {
                dialogo.dismiss();
            } });
        build.setView(crear);
        AlertDialog nueva_lista = build.create();
        nueva_lista.show();
    }

    /*public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, values);
    }*/

    private boolean createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Maps");
        boolean creado = false;
        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Maps/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File(new File("/sdcard/Maps/"), fileName);
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
        return creado;
    }


    public void onBackPressed() {
        getActivity().moveTaskToBack(true);
    }
    /*private void loadMarkers() {
        new MarkersLoadTask().execute();
    }
    private class MarkersLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mDBAdapter.getMarkerLista();
        }
        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
            } else {
                // Mostrar empty state
            }
        }

    }*/
}