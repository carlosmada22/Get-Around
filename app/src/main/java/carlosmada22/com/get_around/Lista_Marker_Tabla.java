package carlosmada22.com.get_around;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by carlosmada22 on 29/9/17.
 */
public class Lista_Marker_Tabla {
    private static final String NOMBRE_TABLA = "lista_marker";
    private SQLiteDatabase sqlDB;

    public Lista_Marker_Tabla(SQLiteDatabase sqlDB){
        this.sqlDB = sqlDB;
    }

    public static abstract class ColumnsListaMarker implements BaseColumns {
        public static final String NOMBRE_TABLA ="lista_marker";

        public static final String ID_MARKER ="id_marker";
        public static final String ID_LISTA ="id_lista";
    }

    public static final String CREATE_TABLE = "create table if not exists "
            + NOMBRE_TABLA + " ("
            + ColumnsListaMarker.ID_MARKER + " integer, "
            + ColumnsListaMarker.ID_LISTA + " integer, "
            + "PRIMARY KEY(" + ColumnsListaMarker.ID_LISTA + ", " + ColumnsListaMarker.ID_MARKER + "), "
            + "FOREIGN KEY(" + ColumnsListaMarker.ID_LISTA+ ") REFERENCES "
            + Lista_Tabla.ColumnsLista.NOMBRE_TABLA + "(" + Lista_Tabla.ColumnsLista._ID + ") "
            + "FOREIGN KEY(" + ColumnsListaMarker.ID_MARKER+ ") REFERENCES "
            + Marker_Tabla.ColumnsMarker.NOMBRE_TABLA + "(" + Marker_Tabla.ColumnsMarker._ID + ") )";

    public static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + NOMBRE_TABLA;


    public boolean crearListaMarker (int id_marker, int id_lista){
        ContentValues values = new ContentValues();
        values.put(ColumnsListaMarker.ID_MARKER, id_marker);
        values.put(ColumnsListaMarker.ID_LISTA, id_lista);

        return sqlDB.insert(NOMBRE_TABLA,null,values)>0;

    }

    public void deleteListaMarker(int id_marker, int id_lista){
        String[] args = new String[]{String.valueOf(id_marker), String.valueOf(id_lista)};
        sqlDB.execSQL("DELETE FROM lista_marker WHERE id_marker=? AND id_lista=?", args);
    }
    public void deleteListaMarker(String id_marker, String id_lista){
        String[] args = new String[]{id_marker, id_lista};
        sqlDB.execSQL("DELETE FROM lista_marker WHERE id_marker=? AND id_lista=?", args);
    }

    public void deleteMarkerFromAll(int id_marker){
        String[] args = {String.valueOf(id_marker)};
        String where = ColumnsListaMarker.ID_MARKER + "=?";
        sqlDB.delete(NOMBRE_TABLA,where,args);
    }

    public void deleteAllFromList(int id_lista){
        String[] args = {String.valueOf(id_lista)};
        String where = ColumnsListaMarker.ID_LISTA + "=?";
        sqlDB.delete(NOMBRE_TABLA,where,args);
    }


    public Cursor getMarkerFromLista(int idlista){
        String[] columns = {ColumnsListaMarker.ID_MARKER};
        String[] args = {String.valueOf(idlista)};
        String where = ColumnsListaMarker.ID_LISTA + "=?";
        return sqlDB.query(NOMBRE_TABLA,columns,where,args,null,null,null);
    }

    public Cursor getListofMarker(int idmarker){
        String[] columns = {ColumnsListaMarker.ID_LISTA};
        String[] args = {String.valueOf(idmarker)};
        String where = ColumnsListaMarker.ID_MARKER + "=?";
        return sqlDB.query(NOMBRE_TABLA,columns,where,args,null,null,null);
    }


}
