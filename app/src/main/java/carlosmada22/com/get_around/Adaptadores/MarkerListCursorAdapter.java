package carlosmada22.com.get_around.Adaptadores;

import android.content.Context;
import android.database.Cursor;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import carlosmada22.com.get_around.BaseDeDatos.DBAdapter;
import carlosmada22.com.get_around.BaseDeDatos.Marker_Tabla;
import carlosmada22.com.get_around.Vista.MarkerListFragment;
import carlosmada22.com.get_around.R;
import carlosmada22.com.get_around.Vista.ListFragment;


/**
 * Created by root on 10/3/17.
 */
public class MarkerListCursorAdapter extends CursorAdapter {

    private DBAdapter mDBAdapter;


    public MarkerListCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override

    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.marker_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        // Referencias UI.
        mDBAdapter = new DBAdapter(context);
        mDBAdapter.open();
        TextView nameText = (TextView) view.findViewById(R.id.marker_name);
        TextView descriptionText = (TextView) view.findViewById(R.id.marker_description);
        TextView categoryText = (TextView) view.findViewById(R.id.marker_category);
        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.b_delete);

        // Get valores.
        String name = cursor.getString(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.NAME));
        String description = cursor.getString(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.DESCRIPTION));

        int category = cursor.getInt(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.CATEGORY));
        String categoria = markerCategory(category);
        final String id_marker = cursor.getString(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.ID));
        final int idlist = MarkerListFragment.getNumLista();
        final String id_lista = "" + idlist;
        String laCategoria = "Categoría: "  + categoria;
        // Setup.
        nameText.setText(name);
        descriptionText.setText(description);
        categoryText.setText(laCategoria );
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDBAdapter.deleteMarkerFromList(id_marker,id_lista);
                ListFragment.loadList();
                Toast.makeText(context,
                        "Punto de interés eliminado",
                        Toast.LENGTH_SHORT).show();
                Log.i ("id_marker_delete", "" + id_marker);
                Cursor c = mDBAdapter.getListMarkers(idlist);
                if(c==null || c.getCount()<=0) MarkerListFragment.empty.setVisibility(View.VISIBLE);
                else MarkerListFragment.empty.setVisibility(View.INVISIBLE);
                swapCursor(c);
                notifyDataSetChanged();
            }
        });

    }

    public static String markerCategory(int categoria) {
        String category = "";
        switch (categoria) {
            case 0:
                category = "Alojamiento";
                break;
            case 1:
                category = "Restaurante";
                break;
            case 2:
                category = "Comercio";
                break;
            case 3:
                category = "Estación de servicio";
                break;
            case 4:
                category = "Estación de tren/autobús";
                break;
            case 5:
                category = "Aeropuerto";
                break;
            case 6:
                category = "Museo";
                break;
            case 7:
                category = "Iglesia";
                break;
            case 8:
                category = "Geográfico";
                break;
            default:
                category = "Alojamiento";


        }
        return category;
    }

}