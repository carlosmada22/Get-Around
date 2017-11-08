package carlosmada22.com.get_around;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

/**
 * Created by carlosmada22 on 22/9/17.
 */
public class DBAdapter{

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Markers.db";

    private DBOpenHelper dbHelper;
    private SQLiteDatabase mDB;

    Marker_Tabla tabla_markers;
    Lista_Tabla tabla_lista;
    Lista_Marker_Tabla tabla_marker_lista;

    public DBAdapter(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    //Conexion DB
    public void open() {
        mDB = dbHelper.getWritableDatabase();
        tabla_markers = new Marker_Tabla(mDB);
        tabla_lista = new Lista_Tabla(mDB);
        tabla_marker_lista = new Lista_Marker_Tabla(mDB);
    }

    public void close() {
        mDB.close();
    }

    //Insertar marker
    public int insertarMarker(String nombreMarker, double lat, double lon, String descripcion, int categoria ){
        return tabla_markers.crearMarker(nombreMarker,lat,lon,descripcion,categoria);
    }

    //Crear nueva lista
    public boolean crearLista (String nombre){
        return tabla_lista.crearLista(nombre);
    }

    public boolean crearListaMarker (int id_marker, int id_lista) { return tabla_marker_lista.crearListaMarker(id_marker, id_lista);}
    //Lista markers
    public Cursor getMarkerLista(){
        return tabla_markers.listaMarkers();
    }

    //Categoria marker
    public int getCategoria(double lat, double lon){
        return tabla_markers.getCategoria(lat,lon);
    }
    //Lista listas
    public Cursor getListaListas() {return tabla_lista.listaListas(); }

    public Cursor getListofMarker(int id_marker){
        return tabla_marker_lista.getListofMarker(id_marker);
    }

    public boolean checkLista (int id_lista) {
        return tabla_lista.checkList(id_lista);
    }

    //Markers de una lista
    public Cursor getMarkersFromList(int idlista) {return tabla_marker_lista.getMarkerFromLista(idlista);}

    public Cursor getListMarkers(int idlista){
        Cursor c = null;
        c = getMarkersFromList(idlista);
        MatrixCursor matrixCursor = null;
        String columns[] = new String[]{"_id", "name", "lat", "lon", "description", "category"};
        matrixCursor = new MatrixCursor(columns);
        Object[] newRow = new Object[6];
        if (c.moveToFirst()) {
            do {
                String[] args = new String[]{String.valueOf(c.getInt(0))};
                Cursor mid = mDB.rawQuery("SELECT * FROM marker WHERE _id=?", args);
                if (mid.moveToFirst()) {
                    do {
                        newRow[0] = mid.getInt(0);
                        newRow[1] = mid.getString(1);
                        newRow[2] = mid.getDouble(2);
                        newRow[3] = mid.getDouble(3);
                        newRow[4] = mid.getString(4);
                        newRow[5] = mid.getInt(5);
                        matrixCursor.addRow(newRow);
                    } while (mid.moveToNext());
                }
            } while (c.moveToNext());
        }
            return matrixCursor;
    }

    //Borrar marker
    public void deleteMarker(double latitud, double longitud){
        tabla_markers.deleteMarker(latitud, longitud);
    }
    public int deleteList(int id_lista){
        return tabla_lista.deleteLista(id_lista);
    }
    public void deleteMarkerFromList(int id_marker, int id_lista){
        tabla_marker_lista.deleteListaMarker(id_marker,id_lista);
    }
    public void deleteMarkerFromList(String id_marker, String id_lista){
        tabla_marker_lista.deleteListaMarker(id_marker,id_lista);
    }
    public void deleteMarkerFromAll(int id_marker){
        tabla_marker_lista.deleteMarkerFromAll(id_marker);
    }
    public void deleteAllFromList(int id_list){
        tabla_marker_lista.deleteAllFromList(id_list);
    }
    public int updateMarker(String nombreMarker, double lat, double lon, String descripcion, int categoria){
        return tabla_markers.updateMarker(nombreMarker,lat,lon,descripcion,categoria);
    }
    public int updateLista(int id, String nombre){
        return tabla_lista.updateLista(id, nombre);
    }

    public int getIdMarker(double lat, double lon){ return tabla_markers.getIdMarker(lat, lon);}

    public String getNameList(int id_lista){ return tabla_lista.getNombreLista(id_lista);}

    public boolean checkMarker (double lat, double lon){ return tabla_markers.checkMarker(lat, lon);}


    private class DBOpenHelper extends SQLiteOpenHelper{

        public DBOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(Marker_Tabla.CREATE_TABLE);
            db.execSQL(Lista_Tabla.CREATE_TABLE);
            db.execSQL(Lista_Marker_Tabla.CREATE_TABLE);

        }

        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {

            db.execSQL(Marker_Tabla.DELETE_TABLE);
            db.execSQL(Lista_Tabla.DELETE_TABLE);
            db.execSQL(Lista_Marker_Tabla.DELETE_TABLE);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldV, int newV){
            onUpgrade(db, oldV, newV);
        }
    }


}
