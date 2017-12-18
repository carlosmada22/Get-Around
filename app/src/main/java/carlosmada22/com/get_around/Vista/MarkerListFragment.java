package carlosmada22.com.get_around.Vista;


import android.database.Cursor;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import carlosmada22.com.get_around.Adaptadores.MarkerListCursorAdapter;
import carlosmada22.com.get_around.BaseDeDatos.DBAdapter;
import carlosmada22.com.get_around.R;


/**
 * Created by carlosmada22 on 2/10/17.
 */
public class MarkerListFragment extends Fragment {

    private static DBAdapter mDBAdapter;

    static int numLista;
    static String nameLista;
    private ListView mMarkersList;
    public static TextView empty;
    private static MarkerListCursorAdapter mMarkerListAdapter;
    ImageButton bDelete;

    public static int getNumLista() {
        return numLista;
    }



    public MarkerListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ...

        setHasOptionsMenu(true);
    }

    public static MarkerListFragment newInstance() {
        return new MarkerListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_marker_list, container, false);

        //Referencias UI
        mMarkersList = (ListView) root.findViewById(R.id.list_markers);
        mMarkerListAdapter = new MarkerListCursorAdapter(getActivity(), null);

        //Setup
        mMarkersList.setAdapter(mMarkerListAdapter);

        //Instancia Adapter BD
        mDBAdapter = new DBAdapter(getActivity());
        mDBAdapter.open();




        numLista = getActivity().getIntent().getExtras().getInt("numLista");
        Log.i("id_lista_fragment", "" + numLista);
        nameLista = getActivity().getIntent().getExtras().getString("nameLista");

        empty = (TextView) root.findViewById(R.id.tvEmpty);
        Cursor markers = mDBAdapter.getListMarkers(numLista);
        if(markers==null || markers.getCount()<=0) empty.setVisibility(View.VISIBLE);


        //Carga de datos
        loadMarkers();


        return root;
    }

    public static void loadMarkers() {
        new MarkersLoadTask().execute();
    }

    private static class MarkersLoadTask extends AsyncTask<Void, Void, Cursor> {

        @Override
        protected Cursor doInBackground(Void... voids) {
            Cursor c = mDBAdapter.getListMarkers(numLista);
            if (c.moveToFirst()){
                do{
                    Log.i("id_marker", "" + c.getInt(0));
                    Log.i("name_marker", "" + c.getString(1));
                }while (c.moveToNext());
            }
           return c;
        }

        @Override
        protected void onPostExecute(Cursor cursor) {
            if (cursor != null && cursor.getCount() > 0) {
                mMarkerListAdapter.swapCursor(cursor);
            } else {

            }
        }
    }

}
