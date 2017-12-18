package carlosmada22.com.get_around.Vista;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import carlosmada22.com.get_around.Adaptadores.ListCursorAdapter;
import carlosmada22.com.get_around.BaseDeDatos.DBAdapter;
import carlosmada22.com.get_around.R;

/**
 * Created by carlosmada22 on 2/10/17.
 */


public class ListFragment extends Fragment {

    private ListView listList;
    private TextView empty;
    private static DBAdapter mDBAdapter;
    private static ListCursorAdapter mListAdapter;
    Cursor cur;
    Cursor cur2;

    List<String> nombres = new ArrayList<String>();
    List<Double> latitudes = new ArrayList<Double>();
    List<Double> longitudes = new ArrayList<Double>();
    List<String> descripciones = new ArrayList<String>();
    List<Integer> categorias = new ArrayList<Integer>();
    List<Integer> id = new ArrayList<Integer>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public ListFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        listList = (ListView) root.findViewById(R.id.list_list);
        mListAdapter = new ListCursorAdapter(getActivity(),null);
        FloatingActionButton addFab = (FloatingActionButton) root.findViewById(R.id.fabAdd);
        addFab.setFocusable(false);
        addFab.setFocusableInTouchMode(false);

        mDBAdapter = new DBAdapter(getContext());
        mDBAdapter.open();

        id = new ArrayList<Integer>();
        nombres = new ArrayList<String>();
        latitudes = new ArrayList<Double>();
        longitudes = new ArrayList<Double>();
        descripciones = new ArrayList<String>();
        categorias = new ArrayList<Integer>();


        listList.setAdapter(mListAdapter);

        empty = (TextView) root.findViewById(R.id.tvEmpty);
        Cursor listas = mDBAdapter.getListaListas();
        if(listas==null || listas.getCount()<=0) empty.setVisibility(View.VISIBLE);

        listList.setClickable(true);
        listList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int id_lis;
                String name_lis;
                cur = mDBAdapter.getListaListas();
                if (cur.moveToFirst()) {
                    for (int k=0;k<i;k++){
                        cur.moveToNext();
                    }
                }
                id_lis = cur.getInt(0);
                name_lis = cur.getString(1);
                Intent intent = new Intent(getActivity(), MarkerListActivity.class);
                intent.putExtra("numLista", id_lis);
                Log.i("id_lista", ""+ id_lis);
                intent.putExtra("nameLista", name_lis);
                startActivity(intent);
            }
        });
        listList.setLongClickable(true);
        listList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> adapterView, View view, int position, long l) {
                final int posicion = position;
                Log.i("item", ""+posicion);
                CharSequence[] options = {"Cambiar nombre", "Mostrar puntos", "Eliminar Lista", "Cancelar"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Elige una opción");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                cur = mDBAdapter.getListaListas();
                                int id_lis;
                                String name_lis;
                                if (cur.moveToFirst()) {
                                    for (int k=0;k<posicion;k++){
                                        cur.moveToNext();
                                    }
                                }
                                id_lis = cur.getInt(0);
                                name_lis = cur.getString(1);
                                createDialog(id_lis, name_lis);
                                break;
                            case 1:
                                MapFragment.imprimeVMarkers();
                                MapFragment.positionMarkers.clear();
                                MainActivity.viewPager.setCurrentItem(0);
                                MapFragment.getgMap().clear();
                                cur = mDBAdapter.getListaListas();
                                int id_list;
                                if (cur.moveToFirst()){
                                    for (int h=0;h<posicion;h++){
                                        cur.moveToNext();
                                    }
                                }
                                id_list = cur.getInt(0);
                                Cursor c = null;
                                c = mDBAdapter.getListMarkers(id_list);
                                id.clear();
                                nombres.clear();
                                latitudes.clear();
                                longitudes.clear();
                                descripciones.clear();
                                categorias.clear();
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
                                for (int j=0; j<id.size();j++){
                                    latitude = latitudes.get(j);
                                    longitude = longitudes.get(j);
                                    location = new LatLng(latitude, longitude);
                                    name = nombres.get(j);
                                    desc = descripciones.get(j);
                                    colour = MapFragment.markerColor(categorias.get(j));
                                    MapFragment.positionMarkers.add(location);
                                    MapFragment.getgMap().addMarker(new MarkerOptions().position(location).title(name).snippet(desc).icon(colour));
                                }
                                MapFragment.imprimeVMarkers();
                                break;

                            case 2:
                                cur2 = mDBAdapter.getListaListas();
                                int id_lista;
                                if (cur2.moveToFirst()) {
                                    for (int j=0;j<posicion;j++){
                                        cur2.moveToNext();
                                    }
                                }
                                id_lista = cur2.getInt(0);
                                Log.i("id_lista", ""+ id_lista);
                                mDBAdapter = new DBAdapter(getActivity().getApplicationContext());
                                mDBAdapter.open();
                                boolean borrado = false;
                                if(mDBAdapter.deleteList(id_lista) > 0) borrado = true;
                                cur2 = mDBAdapter.getListaListas();
                                mListAdapter.swapCursor(cur2);
                                listList.setAdapter(mListAdapter);
                                if (borrado){
                                    Toast.makeText(getContext(),
                                            "Lista eliminada",
                                            Toast.LENGTH_SHORT).show();
                                    mDBAdapter.deleteAllFromList(id_lista);
                                }
                                else Toast.makeText(getContext(),
                                        "No ha sido posible eliminar la lista",
                                        Toast.LENGTH_SHORT).show();

                                if(cur2==null || cur2.getCount()<=0) empty.setVisibility(View.VISIBLE);
                                else empty.setVisibility(View.INVISIBLE);
                                break;
                            case 3:
                                break;
                        }
                    }
                });
                builder.show();
                return true;
            }
        });

        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder build = new AlertDialog.Builder(getContext());
                View crear = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_edit_list, null);
                final EditText nombre = (EditText) crear.findViewById(R.id.nombre) ;
                build.setTitle("Crear nueva lista");
                build.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo, int which) {
                        if (!nombre.getText().toString().isEmpty()){
                            mDBAdapter.crearLista(nombre.getText().toString());
                            loadList();
                            empty.setVisibility(View.INVISIBLE);
                            dialogo.dismiss();
                        }
                        else{
                            Toast.makeText(getContext(),
                                    "Nombre obligatorio",
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
        });

        loadList();
        return root;

    }

    public static void loadList() {

        new ListLoadTask().execute();
    }

    private static class ListLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            return mDBAdapter.getListaListas();
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mListAdapter.swapCursor(cursor);
            } else {

            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case 101:
                    mListAdapter.notifyDataSetChanged();
                    listList.setAdapter(mListAdapter);
                    break;
            }
        }

    }

    public void createDialog(final int id_lis, String nombre_lis){
        AlertDialog.Builder build = new AlertDialog.Builder(getContext());
        View crear = getActivity().getLayoutInflater().inflate(R.layout.dialog_new_edit_list, null);
        final EditText nombre = (EditText) crear.findViewById(R.id.nombre) ;
        nombre.setText(nombre_lis);
        build.setTitle("Cambiar nombre lista");
        build.setPositiveButton("Cambiar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo, int which) {
                String newname = nombre.getText().toString();
                if (!newname.isEmpty()){
                    updateListaDB(id_lis, newname);
                    loadList();
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

    private void updateListaDB(int id, String nombre){

        new updateListaBDTask(nombre, id).execute();

    }

    private class updateListaBDTask extends AsyncTask<Void, Void, Boolean>{
        String nombre;
        int id;

        public updateListaBDTask(String nombre, int id){
            this.nombre=nombre;
            this.id=id;
        }
        @Override
        protected Boolean doInBackground(Void... voids){
            mDBAdapter.updateLista(id, nombre);
            return true;
        }
        @Override
        protected void onPostExecute(Boolean bool){
            getActivity().setResult(Activity.RESULT_OK);

        }
    }


}
