package carlosmada22.com.get_around.Modelo;

/**
 * Created by root on 12/18/17.
 */
public class Lista_Marker {

    private int id_lista;
    private int id_marker;

    public Lista_Marker(int id_lista, int id_marker) {
        this.id_lista = id_lista;
        this.id_marker = id_marker;
    }

    public int getId_lista() {
        return id_lista;
    }

    public void setId_lista(int id_lista) {
        this.id_lista = id_lista;
    }

    public int getId_marker() {
        return id_marker;
    }

    public void setId_marker(int id_marker) {
        this.id_marker = id_marker;
    }
}
