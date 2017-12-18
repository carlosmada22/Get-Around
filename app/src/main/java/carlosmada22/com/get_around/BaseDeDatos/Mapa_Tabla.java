package carlosmada22.com.get_around.BaseDeDatos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by carlosmada22 on 2/10/17.
 */
public class Mapa_Tabla {
    private static final String NOMBRE_TABLA = "mapa";
    private SQLiteDatabase sqlDB;

    public Mapa_Tabla(SQLiteDatabase sqlDB){
        this.sqlDB = sqlDB;
    }

    public static abstract class ColumnsMapa implements BaseColumns {

        public static final String NOMBRE_TABLA ="mapa";

        public static final String NAME ="name";
        public static final String LATNE ="latne";
        public static final String LONGNE ="longne";
        public static final String LATSW ="latsw";
        public static final String LONGSW ="longsw";
        public static final String ZOOM ="zoom";
        public static final String LATC ="latc";
        public static final String LONGC ="longc";

    }

    public static final String CREATE_TABLE = "create table if not exists "
            + NOMBRE_TABLA + " ("
            + Mapa_Tabla.ColumnsMapa._ID + " integer primary key autoincrement, "
            + Mapa_Tabla.ColumnsMapa.NAME + " varchar (30) not null, "
            + Mapa_Tabla.ColumnsMapa.LATNE + " double not null, "
            + Mapa_Tabla.ColumnsMapa.LONGNE + " double not null, "
            + Mapa_Tabla.ColumnsMapa.LATSW + " double not null, "
            + Mapa_Tabla.ColumnsMapa.LONGSW + " double not null, "
            + Mapa_Tabla.ColumnsMapa.ZOOM + " float not null, "
            + Mapa_Tabla.ColumnsMapa.LATC + " double not null, "
            + Mapa_Tabla.ColumnsMapa.LONGC + " double not null)";

    public static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + NOMBRE_TABLA;

    public int crearMapa (String name, double latne, double longne, double latsw, double longsw, float zoom, double latc, double longc){
        ContentValues values = new ContentValues();
        values.put(ColumnsMapa.NAME, name);
        values.put(ColumnsMapa.LATNE, latne);
        values.put(ColumnsMapa.LONGNE, longne);
        values.put(ColumnsMapa.LATSW, latsw);
        values.put(ColumnsMapa.LONGSW, longsw);
        values.put(ColumnsMapa.ZOOM, zoom);
        values.put(ColumnsMapa.LATC, latc);
        values.put(ColumnsMapa.LONGC, longc);

        return (int)sqlDB.insert(NOMBRE_TABLA,null,values);

    }
    public Cursor listaMapas(){
        String[] columns = {ColumnsMapa._ID, ColumnsMapa.NAME, ColumnsMapa.LATNE, ColumnsMapa.LONGNE,
                ColumnsMapa.LATSW, ColumnsMapa.LONGSW, ColumnsMapa.ZOOM, ColumnsMapa.LATC, ColumnsMapa.LONGC};
        return sqlDB.query(NOMBRE_TABLA,columns,null,null,null,null,null);
    }

    public int deleteMapa(int id_mapa){
        String[] args = {String.valueOf(id_mapa)};
        return sqlDB.delete(NOMBRE_TABLA, ColumnsMapa._ID + " LIKE ?",new String[]{String.valueOf(id_mapa)});
    }

    public void clearMapas(){
        sqlDB.execSQL("delete from "+ NOMBRE_TABLA);
        sqlDB.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" + NOMBRE_TABLA + "'");
    }

    public boolean checkMapa(String name){
        String[] columns = {ColumnsMapa.NAME};
        String[] args = {String.valueOf(name)};
        String where = ColumnsMapa.NAME + "=?";
        Cursor c = sqlDB.query(NOMBRE_TABLA,columns,where,args,null,null,null);
        if (!(c.moveToFirst()) || c.getCount() ==0) return false;
        else return true;
    }

    public Cursor getMapa(String name){
        String[] columns = {ColumnsMapa._ID, ColumnsMapa.NAME, ColumnsMapa.LATNE, ColumnsMapa.LONGNE,
                ColumnsMapa.LATSW, ColumnsMapa.LONGSW, ColumnsMapa.ZOOM, ColumnsMapa.LATC, ColumnsMapa.LONGC};
        String[] args = {String.valueOf(name)};
        String where = ColumnsMapa.NAME + "=?";
        return sqlDB.query(NOMBRE_TABLA,columns,where,args,null,null,null);
    }

    public int updateMapa(int id, String nombre){
        ContentValues values = new ContentValues();
        values.put(ColumnsMapa.NAME, nombre);
        String[] args = {String.valueOf(id)};
        String where = ColumnsMapa._ID + "=?";
        return sqlDB.update(NOMBRE_TABLA,values,where,args);
    }

    public String getName(LatLng punto){
        double lat = punto.latitude;
        double lon = punto.longitude;
        String[] columns = {ColumnsMapa.NAME};
        String[] args = {String.valueOf(lat), String.valueOf(lon)};
        String where = ColumnsMapa.LATC + "=? AND " + ColumnsMapa.LONGC + "=?";
        Cursor c = sqlDB.query(NOMBRE_TABLA, columns, where, args, null, null, null);
        String name = "";
        if (c.moveToFirst()) name = c.getString(0);
        c.close();
        return name;
    }
}
