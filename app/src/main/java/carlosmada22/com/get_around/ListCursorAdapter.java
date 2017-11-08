package carlosmada22.com.get_around;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by carlosmada22 on 2/10/17.
 */
public class ListCursorAdapter extends CursorAdapter {

    public DBAdapter mDBAdapter;


    public ListCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.list_item_list, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Referencias UI.
        TextView nameText = (TextView) view.findViewById(R.id.list_name);
        TextView numberText = (TextView) view.findViewById(R.id.n_markers);

        // Get valores.
        String name = cursor.getString(cursor.getColumnIndex(Lista_Tabla.ColumnsLista.NAME));
        int id = cursor.getInt(cursor.getColumnIndex(Lista_Tabla.ColumnsLista._ID));

        mDBAdapter = new DBAdapter(context);
        mDBAdapter.open();

        Cursor c = mDBAdapter.getMarkersFromList(id);
        int nMarkers = c.getCount();


        // Setup.
        nameText.setText(name);
        numberText.setText("Puntos de interés: " + nMarkers);

        LinearLayout click = (LinearLayout) view.findViewById(R.id.click);
    }

    /*@Override
    public boolean isEnabled(int position)
    {
        return true;
    }
    /*@Override
    protected void onContentChanged() {
        super.onContentChanged();
        notifyDataSetChanged();
    }*/
    @Override
    public boolean areAllItemsEnabled()
    {
        return true;
    }

    @Override
    public boolean isEnabled(int arg0)
    {
        return true;
    }

}
