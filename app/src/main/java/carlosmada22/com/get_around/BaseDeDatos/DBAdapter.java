package carlosmada22.com.get_around.BaseDeDatos;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

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
    Mapa_Tabla tabla_mapa;

    public DBAdapter(Context context) {
        dbHelper = new DBOpenHelper(context);
    }

    //Conexion DB
    public void open() {
        mDB = dbHelper.getWritableDatabase();
        tabla_markers = new Marker_Tabla(mDB);
        tabla_lista = new Lista_Tabla(mDB);
        tabla_marker_lista = new Lista_Marker_Tabla(mDB);
        tabla_mapa = new Mapa_Tabla(mDB);
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

    //Crear un marker dentro de una lista
    public boolean crearListaMarker (int id_marker, int id_lista) { return tabla_marker_lista.crearListaMarker(id_marker, id_lista);}

    //Crear mapa
    public int crearMapa (String name, double latne, double longne, double latsw, double longsw, float zoom, double latc, double longc) { return tabla_mapa.crearMapa(name, latne, longne, latsw, longsw, zoom, latc, longc);}

    //Lista markers
    public Cursor getMarkerLista(){
        return tabla_markers.listaMarkers();
    }

    //Marker
    public Cursor getMarker(double lat, double lon) { return tabla_markers.getMarker(lat, lon);}

    //Categoria marker
    public int getCategoria(double lat, double lon){
        return tabla_markers.getCategoria(lat,lon);
    }
    //Lista listas
    public Cursor getListaListas() {return tabla_lista.listaListas(); }

    //Lista mapas
    public Cursor listaMapas(){
        return tabla_mapa.listaMapas();
    }

    //Mapa
    public Cursor getMapa(String name) { return tabla_mapa.getMapa(name);}

    //existe lista?
    public boolean checkLista (int id_lista) {
        return tabla_lista.checkList(id_lista);
    }

    //existe mapa?
    public boolean checkMapa (String name){ return tabla_mapa.checkMapa(name);}

    //Markers de una lista
    public Cursor getMarkersFromList(int idlista) {return tabla_marker_lista.getMarkerFromLista(idlista);}

    //Nombre mapa
    public String getMapName (LatLng latLng) {return tabla_mapa.getName(latLng);}

    //Lista de markers de una lista
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
    //Borrar lista
    public int deleteList(int id_lista){
        return tabla_lista.deleteLista(id_lista);
    }

    //Borrar marker de una lista
    public void deleteMarkerFromList(String id_marker, String id_lista){
        tabla_marker_lista.deleteListaMarker(id_marker,id_lista);
    }
    //Borrar marker de todas las listas
    public void deleteMarkerFromAll(int id_marker){
        tabla_marker_lista.deleteMarkerFromAll(id_marker);
    }
    //Borrar todos los markers de una lista
    public void deleteAllFromList(int id_list){
        tabla_marker_lista.deleteAllFromList(id_list);
    }

    //Borrar mapa
    public int deleteMapa(int id_mapa){
        return tabla_mapa.deleteMapa(id_mapa);
    }

    //vaciar tabla de mapas
    public void clearMapas() { tabla_mapa.clearMapas();}

    //actualizar marker
    public int updateMarker(String nombreMarker, double lat, double lon, String descripcion, int categoria){
        return tabla_markers.updateMarker(nombreMarker,lat,lon,descripcion,categoria);
    }
    //actualizar lista
    public int updateLista(int id, String nombre){
        return tabla_lista.updateLista(id, nombre);
    }

    //actualizar mapa
    public int updateMapa(int id, String nombre){
        return tabla_mapa.updateMapa(id, nombre);
    }

    //id de marker
    public int getIdMarker(double lat, double lon){ return tabla_markers.getIdMarker(lat, lon);}

    //existe marker?
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
            db.execSQL(Mapa_Tabla.CREATE_TABLE);

        }

        public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {

            db.execSQL(Marker_Tabla.DELETE_TABLE);
            db.execSQL(Lista_Tabla.DELETE_TABLE);
            db.execSQL(Lista_Marker_Tabla.DELETE_TABLE);
            db.execSQL(Mapa_Tabla.DELETE_TABLE);
            onCreate(db);
        }

        public void onDowngrade(SQLiteDatabase db, int oldV, int newV){
            onUpgrade(db, oldV, newV);
        }
    }


}
