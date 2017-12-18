package carlosmada22.com.get_around.Vista;



import android.app.Activity;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.location.Location;

import android.os.AsyncTask;

import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;

import android.widget.EditText;

import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


import java.io.File;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import carlosmada22.com.get_around.Adaptadores.MapAdapter;
import carlosmada22.com.get_around.BaseDeDatos.DBAdapter;
import carlosmada22.com.get_around.ImageLoader.ImageLoader;
import carlosmada22.com.get_around.R;


/**
 * Created by carlosmada22 on 2/10/17.
 */
public class MapScreensFragment extends Fragment implements OnMapReadyCallback {

    static ListView list;
    static MapAdapter adapter;
    GroundOverlay imageOverlay;
    static Activity activity;
    int nmap;

    public static int getOrder() {
        return order;
    }

    public static void setOrder(int order) {
        MapScreensFragment.order = order;
    }

    public static int order = 3;

    int posicion;

    File file;
    private static String[] mFileStrings;
    private File[] listFile;
    public static ImageLoader imageLoader;

    Bitmap myBitmap;
    public static DBAdapter mDBAdapter;
    Cursor cur;



    public static MapScreensFragment newInstance() {
        return new MapScreensFragment();
    }

    public MapScreensFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_map_screens, container, false);

        mDBAdapter = new DBAdapter(getContext());
        mDBAdapter.open();

        activity = getActivity();

        file = new File("/storage/emulated/0/Maps/");
        Log.i("directorio", "" + file.toString());

        if (file.isDirectory()) {
            Log.i("directorio?", "si");
            listFile = file.listFiles();
            mFileStrings = new String[listFile.length];

            Log.i("vacio?", "" + listFile.length);
            for (int i = 0; i < listFile.length; i++) {
                mFileStrings[i] = listFile[i].getAbsolutePath();
                Log.i("archivos", "" + mFileStrings[i]);
            }
        } else {
            file.mkdirs();
            Log.i("directorio?", "no");
        }

        list = (ListView) v.findViewById(R.id.list);
        adapter = new MapAdapter(getActivity(), mFileStrings);
        list.setAdapter(adapter);


        imageLoader = new ImageLoader(getActivity().getApplicationContext());

        list.setClickable(true);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Dialog dialog = new Dialog(getActivity());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.image_display);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.MATCH_PARENT;
                dialog.show();
                dialog.getWindow().setAttributes(lp);



                MapView mMapView = (MapView) dialog.findViewById(R.id.mapView);
                MapsInitializer.initialize(getActivity());

                nmap = i;

                mMapView.onCreate(dialog.onSaveInstanceState());
                mMapView.onResume();// needed to get the map to display immediately
                mMapView.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(final GoogleMap map) {

                        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                            @Override
                            public void onMapClick(LatLng latLng) {
                                Log.i("LatLng", "" + latLng);
                            }
                        });

                        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                            return;
                        }
                        map.setMyLocationEnabled(true);

                        int id_map = 0;
                        double LatitudNE = 0;
                        double LongitudNE = 0;
                        double LatitudSW = 0;
                        double LongitudSW = 0;
                        float zoom = 0;
                        double LatC = 0;
                        double LongC = 0;

                        String path_name = mFileStrings[nmap];
                        String file_name=path_name.substring(path_name.indexOf("Maps/") + 5,path_name.indexOf(".jpg"));
                        File imgFile = new  File(path_name);
                        myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());


                        cur = mDBAdapter.getMapa(file_name);
                        if(cur.moveToFirst()) {
                            id_map = cur.getInt(0);
                            LatitudNE = cur.getDouble(2);
                            LongitudNE = cur.getDouble(3);
                            LatitudSW = cur.getDouble(4);
                            LongitudSW = cur.getDouble(5);
                            zoom = cur.getFloat(6);
                            LatC = cur.getDouble(7);
                            LongC = cur.getDouble(8);
                        }


                        Log.i("id_mapa", "" + id_map);
                        Log.i("latneVISTA", "" + LatitudNE);
                        Log.i("longneVISTA", "" + LongitudNE);
                        Log.i("latswVISTA", "" + LatitudSW);
                        Log.i("longswVISTA", "" + LongitudSW);


                        LatLng NE = new LatLng(LatitudNE,LongitudNE);
                        LatLng SW = new LatLng(LatitudSW,LongitudSW);
                        LatLng center = new LatLng(LatC, LongC);

                        LatLngBounds extremos = new LatLngBounds(SW, NE);


                        Log.i("LatLngCenter", "" + center);

                        GroundOverlayOptions offlineMap = new GroundOverlayOptions()
                                .image(BitmapDescriptorFactory.fromBitmap(myBitmap))
                                .transparency(0)
                                .positionFromBounds(extremos);
                        imageOverlay = map.addGroundOverlay(offlineMap);

                        map.setMapType(GoogleMap.MAP_TYPE_NONE);

                        map.animateCamera(CameraUpdateFactory.newLatLngBounds(extremos,25));

                        map.getUiSettings().setAllGesturesEnabled(false);

                        map.getUiSettings().setZoomGesturesEnabled(true);

                        map.getUiSettings().setRotateGesturesEnabled(true);


                    }
                });

            }
        });

        list.setLongClickable(true);
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int position, long l) {
                posicion = position;
                CharSequence[] options = {"Cambiar nombre", "Eliminar Mapa", "Cancelar"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                final int posicion = position;
                builder.setTitle("Elige una opción");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                String pathName = mFileStrings[posicion];
                                String fileName=pathName.substring(pathName.indexOf("Maps/") + 5,pathName.indexOf(".jpg"));
                                cur = mDBAdapter.getMapa(fileName);
                                if (cur.moveToFirst()) {
                                    int id_mapa = cur.getInt(0);
                                    createDialog(id_mapa,fileName);
                                }
                                break;

                            case 1:
                                File archivo = new File(mFileStrings[posicion]);
                                String path_name = mFileStrings[posicion];
                                String file_name=path_name.substring(path_name.indexOf("Maps/") + 5,path_name.indexOf(".jpg"));
                                cur = mDBAdapter.getMapa(file_name);
                                if (cur.moveToFirst()) {
                                    int id_mapa = cur.getInt(0);
                                    int borrado = mDBAdapter.deleteMapa(id_mapa);
                                    Log.i("mapa-borrado", ""+borrado);
                                }
                                boolean deleted = archivo.delete();
                                if (deleted) Toast.makeText(getContext(),
                                        "Mapa eliminado",
                                        Toast.LENGTH_SHORT).show();
                                listFile = file.listFiles();
                                mFileStrings = new String[listFile.length];
                                for (int j = 0; j < listFile.length; j++) {
                                    mFileStrings[j] = listFile[j].getAbsolutePath();
                                }
                                adapter = new MapAdapter(getActivity(), mFileStrings);
                                list.setAdapter(adapter);
                                if (getOrder()==1){
                                    sortAlph(getActivity());
                                }
                                else if (getOrder() == 2){
                                    sortProx(getActivity());
                                }
                                break;

                            case 2:
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });


        return v;
    }

    @Override
    public void onDestroy() {
        list.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap map) {
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        map.setMyLocationEnabled(true);
    }

    public static void sortProx(Activity a) {

        //Localizacion actual
        double mylat = MapFragment.getLatitud();
        double mylon = MapFragment.getLongitud();
        final LatLng myLocation = new LatLng(mylat,mylon);

        double latcentro;
        double loncentro;

        Cursor c = mDBAdapter.listaMapas();
        LatLng[] centros = new LatLng[c.getCount()];
        int i = 0;
        if (c.moveToFirst()) {
            do {
                latcentro = c.getDouble(7);
                loncentro = c.getDouble(8);
                LatLng latlngcentro = new LatLng(latcentro, loncentro);
                if (i <= c.getCount()) centros[i] = latlngcentro;
                i++;
            } while (c.moveToNext());
        }
        List<LatLng> centrosList = Arrays.asList(centros);
        //Haces el orden con la lista nueva
        Collections.sort(centrosList, new Comparator<LatLng>() {
            @Override
            public int compare(LatLng l1, LatLng l2) {

                Location loc1 = new Location("");
                loc1.setLatitude(l1.latitude);
                loc1.setLongitude(l1.longitude);

                Location loc2 = new Location("");
                loc2.setLatitude(l2.latitude);
                loc2.setLongitude(l2.longitude);

                Location myloc = new Location("");
                myloc.setLatitude(myLocation.latitude);
                myloc.setLongitude(myLocation.longitude);

                float distanceToL1 = loc1.distanceTo(myloc);
                float distanceToL2 = loc2.distanceTo(myloc);

                if (distanceToL1 == distanceToL2) {
                    return 0;
                } else if (distanceToL1 < distanceToL2) {
                    return 1;
                } else {
                    return -1;
                }

            }
        });
        String[] orderList = new String[centrosList.size()];
        LatLng Laux;
        String nameaux;


        for(int j = 0; j < centrosList.size(); j++){
            Laux = centrosList.get(j);
            nameaux = mDBAdapter.getMapName(Laux);
            orderList[j] = nameaux;

        }

        for(int k = 0; k < orderList.length; k++){
            orderList[k] = "/storage/emulated/0/Maps/" + orderList[k] + ".jpg";
        }
        mFileStrings = orderList;

        adapter = new MapAdapter(a, mFileStrings);
        list.setAdapter(adapter);
        imageLoader = new ImageLoader(a.getApplicationContext());
        imageLoader.clearCache();

    }

    public static void sortAlph(Activity a) {

        Arrays.sort(mFileStrings);
        adapter = new MapAdapter(a, mFileStrings);
        list.setAdapter(adapter);
        imageLoader = new ImageLoader(a.getApplicationContext());
        imageLoader.clearCache();

        adapter.notifyDataSetChanged();
    }

    public static void sortDate(Activity a) {
        Cursor c = mDBAdapter.listaMapas();
        String[] names = new String[c.getCount()];
        int i = 0;
        if (c.moveToFirst()) {
            do {
                names[i] = c.getString(1);
                i++;
            } while (c.moveToNext());
        }

        for(int k = 0; k < names.length; k++){
            names[k] = "/storage/emulated/0/Maps/" + names[k] + ".jpg";
        }

        mFileStrings = names;

        adapter = new MapAdapter(a, mFileStrings);
        list.setAdapter(adapter);
        imageLoader = new ImageLoader(a.getApplicationContext());
        imageLoader.clearCache();

        adapter.notifyDataSetChanged();


    }

    public void createDialog(final int id_map, final String nombre_map){
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        View crear = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_edit_list, null);
        final EditText nombre = (EditText) crear.findViewById(R.id.nombre) ;
        nombre.setText(nombre_map);
        build.setTitle("Cambiar nombre del mapa");
        build.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int which) {
                String newname = nombre.getText().toString();
                if (!newname.isEmpty()&&!newname.equals(nombre_map)){
                    newname = MapFragment.compruebaMapa(newname);
                    File antes = new File("/storage/emulated/0/Maps/" + nombre_map + ".jpg");
                    File despues = new File("/storage/emulated/0/Maps/" + newname + ".jpg");
                    antes.renameTo(despues);
                    updateMapaDB(id_map, newname);
                    listFile = file.listFiles();

                    for (int i = 0; i < listFile.length; i++) {
                        mFileStrings[i] = listFile[i].getAbsolutePath();
                    }


                    adapter = new MapAdapter(getActivity(), mFileStrings);
                    list.setAdapter(adapter);
                    imageLoader = new ImageLoader(getActivity().getApplicationContext());
                    imageLoader.clearCache();

                    adapter.notifyDataSetChanged();
                } else if (newname.isEmpty()) {
                    Toast.makeText(getContext(),
                            "Introduzca un nombre",
                            Toast.LENGTH_SHORT).show();
                }
            } });


        build.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int which) {
                dialogo.dismiss();
            } });
        build.setView(crear);
        AlertDialog cambiar_mapa = build.create();
        cambiar_mapa.show();

    }

    private void updateMapaDB(int id, String nombre){

        new updateMapaBDTask(nombre, id).execute();

    }

    private class updateMapaBDTask extends AsyncTask<Void, Void, Boolean> {
        String nombre;
        int id;

        public updateMapaBDTask(String nombre, int id){
            this.nombre=nombre;
            this.id=id;
        }
        @Override
        protected Boolean doInBackground(Void... voids){
            mDBAdapter.updateMapa(id, nombre);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){
            getActivity().setResult(Activity.RESULT_OK);

        }
    }



}
