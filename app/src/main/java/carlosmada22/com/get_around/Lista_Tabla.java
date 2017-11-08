package carlosmada22.com.get_around;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by carlosmada22 on 29/9/17.
 */
public class Lista_Tabla {
    private static final String NOMBRE_TABLA = "lista";
    private SQLiteDatabase sqlDB;

    public Lista_Tabla(SQLiteDatabase sqlDB){
        this.sqlDB = sqlDB;
    }

    public static abstract class ColumnsLista implements BaseColumns {
        public static final String NOMBRE_TABLA ="lista";


        public static final String NAME ="name";
    }

    public static final String CREATE_TABLE = "create table if not exists "
            + NOMBRE_TABLA + " ("
            + Lista_Tabla.ColumnsLista._ID + " integer primary key autoincrement, "
            + Lista_Tabla.ColumnsLista.NAME + " varchar (30) not null)";

    public static final String DELETE_TABLE =
            "DROP TABLE IF EXISTS " + NOMBRE_TABLA;

    public boolean crearLista (String nombre){
        ContentValues values = new ContentValues();
        values.put(ColumnsLista.NAME, nombre);

        return sqlDB.insert(NOMBRE_TABLA,null,values)>0;

    }

    public int deleteLista(int id_lista){
        String[] args = {String.valueOf(id_lista)};
        return sqlDB.delete(NOMBRE_TABLA, ColumnsLista._ID + " LIKE ?",new String[]{String.valueOf(id_lista)});
    }

    public Cursor listaListas(){
        String[] columns = {Lista_Tabla.ColumnsLista._ID, Lista_Tabla.ColumnsLista.NAME};
        return sqlDB.query(this.NOMBRE_TABLA,columns,null,null,null,null,null);
    }

    public String getNombreLista(int id_lista){
        String[] columns = {ColumnsLista.NAME};
        String[] args = {String.valueOf(id_lista)};
        String where = ColumnsLista._ID + "=?";
        Cursor c = sqlDB.query(NOMBRE_TABLA,columns,where,args,null,null,null);
        String name = "Lista";
        if (c.moveToFirst()) {
            do {
                name = c.getString(0);

            } while (c.moveToNext());
        }
        return name;
    }

    public int updateLista(int id, String nombre){
        ContentValues values = new ContentValues();
        values.put(ColumnsLista.NAME, nombre);
        String[] args = {String.valueOf(id)};
        String where = ColumnsLista._ID + "=?";
        return sqlDB.update(NOMBRE_TABLA,values,where,args);
    }

    public boolean checkList(int id_lista){
        String[] columns = {ColumnsLista._ID};
        String[] args = {String.valueOf(id_lista)};
        String where = ColumnsLista._ID + "=?";
        Cursor c = sqlDB.query(NOMBRE_TABLA,columns,where,args,null,null,null);
        if (!(c.moveToFirst()) || c.getCount() ==0) return false;
        else return true;
    }

}
