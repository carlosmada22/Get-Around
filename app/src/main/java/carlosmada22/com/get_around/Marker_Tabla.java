package carlosmada22.com.get_around;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by carlosmada22 on 22/9/17.
 */
public class Marker_Tabla {

    private static final String NOMBRE_TABLA = "marker";
    private SQLiteDatabase sqlDB;

    public Marker_Tabla (SQLiteDatabase sqlDB){
        this.sqlDB = sqlDB;
    }

    public static abstract class ColumnsMarker implements BaseColumns {

        public static final String NOMBRE_TABLA ="marker";

        public static final String LAT ="lat";
        public static final String LON ="lon";
        public static final String NAME ="name";
        public static final String DESCRIPTION ="description";
        public static final String CATEGORY ="category";
        public static final String ID= "_id";

    }

    public static final String CREATE_TABLE = "create table if not exists "
            + NOMBRE_TABLA + " ("
            + Marker_Tabla.ColumnsMarker.ID + " integer primary key autoincrement, "
            + Marker_Tabla.ColumnsMarker.NAME + " varchar(30) not null, "
            + Marker_Tabla.ColumnsMarker.LAT + " double not null, "
            + Marker_Tabla.ColumnsMarker.LON + " double not null, "
            + Marker_Tabla.ColumnsMarker.DESCRIPTION + " text , "
            + Marker_Tabla.ColumnsMarker.CATEGORY + " integer not null)";


    public static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + NOMBRE_TABLA;

    public int crearMarker (String nombre, double lat, double lon, String descripcion, int categoria){
        ContentValues values = new ContentValues();
        values.put(ColumnsMarker.NAME, nombre);
        values.put(ColumnsMarker.LAT, lat);
        values.put(ColumnsMarker.LON, lon);
        values.put(ColumnsMarker.DESCRIPTION, descripcion);
        values.put(ColumnsMarker.CATEGORY, categoria);

        return (int)sqlDB.insert(NOMBRE_TABLA,null,values);

    }
    public Cursor listaMarkers(){
        String[] columns = {ColumnsMarker._ID, ColumnsMarker.NAME, ColumnsMarker.LAT,
                ColumnsMarker.LON, ColumnsMarker.DESCRIPTION, ColumnsMarker.CATEGORY};
        return sqlDB.query(NOMBRE_TABLA,columns,null,null,null,null,null);
    }

    public int getCategoria(double latitud, double longitud){
        String[] columns = {ColumnsMarker.CATEGORY};
        String[] args = {String.valueOf(latitud), String.valueOf(longitud)};
        String where = ColumnsMarker.LAT + "=? AND " + ColumnsMarker.LON + "=?";
        Cursor cur = sqlDB.query(NOMBRE_TABLA, columns, where, args, null, null, null);
        int category = 0;
        if (cur.moveToFirst()) category = cur.getInt(0);
        cur.close();
        return category;
    }
    public void deleteMarker(double latitud, double longitud){
        Double[] args = new Double[2];
        args[0] = latitud;
        args[1] = longitud;
        sqlDB.execSQL("DELETE FROM marker WHERE lat=? AND lon=?", args);
    }
    public int updateMarker(String nombre, double lat, double lon, String descripcion, int categoria){
        ContentValues values = new ContentValues();
        values.put(ColumnsMarker.NAME, nombre);
        values.put(ColumnsMarker.LAT, lat);
        values.put(ColumnsMarker.LON, lon);
        values.put(ColumnsMarker.DESCRIPTION, descripcion);
        values.put(ColumnsMarker.CATEGORY, categoria);
        String[] args = {String.valueOf(lat), String.valueOf(lon)};
        String where = ColumnsMarker.LAT + "=? AND " + ColumnsMarker.LON + "=?";
        return sqlDB.update(NOMBRE_TABLA,values,where,args);
    }

    public void llenarTabla(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DELETE_TABLE);
        crearMarker("Mi casa", 43.537811, -5.653821, "Mi casa en Gijon", 1);
        crearMarker("Hotel ABBA Gijon", 43.541397, -5.643160, "Hotel ABBA en la playa de Gijón", 1);
        crearMarker("A feira do pulpo", 43.538371, -5.648429, "Restaurante Gallego del barrio de la Arena, Gijon", 2);
        crearMarker("Parroquia San Antonio de Padua", 43.538029, -5.653342, "Iglesia de los Franciscanos Capuchinos", 3);
    }

    public int getIdMarker(double lat, double lon){
        String[] columns = {ColumnsMarker.ID};
        String[] args = {String.valueOf(lat), String.valueOf(lon)};
        String where = ColumnsMarker.LAT + "=? AND " + ColumnsMarker.LON + "=?";
        Cursor c = sqlDB.query(NOMBRE_TABLA, columns, where, args, null, null, null);
        int id = 0;
        if (c.moveToFirst()) id = c.getInt(0);
        c.close();
        return id;
    }

    public boolean checkMarker(double lat, double lon){
        String[] columns = {ColumnsMarker.ID};
        String[] args = {String.valueOf(lat), String.valueOf(lon)};
        String where = ColumnsMarker.LAT + "=? AND " + ColumnsMarker.LON + "=?";
        Cursor c = sqlDB.query(NOMBRE_TABLA, columns, where, args, null, null, null);
        if (!(c.moveToFirst()) || c.getCount() ==0){
            return false;
        }
        else return true;
    }

}
