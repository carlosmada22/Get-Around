package carlosmada22.com.get_around;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;

/**
 * Created by root on 10/3/17.
 */
public class MarkerListCursorAdapter extends CursorAdapter {

    private DBAdapter mDBAdapter;
    double latitud;
    double longitud;

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
        TextView positionText = (TextView) view.findViewById(R.id.marker_position);
        ImageButton deleteButton = (ImageButton) view.findViewById(R.id.b_delete);

        // Get valores.
        String name = cursor.getString(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.NAME));
        String description = cursor.getString(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.DESCRIPTION));
        String lat = cursor.getString(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.LAT));
        String lon = cursor.getString(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.LON));
        final String id_marker = cursor.getString(cursor.getColumnIndex(Marker_Tabla.ColumnsMarker.ID));
        final int idlist = MarkerListFragment.getNumLista();
        final String id_lista = "" + idlist;
        String position = lat + "\n" + lon ;
        // Setup.
        nameText.setText(name);
        descriptionText.setText(description);
        positionText.setText(position);
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

}