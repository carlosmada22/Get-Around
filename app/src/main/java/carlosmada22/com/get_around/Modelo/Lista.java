package carlosmada22.com.get_around.Modelo;

/**
 * Created by carlosmada22 on 22/9/17.
 */
public class Lista {

    private int id;
    private String name;

    public Lista(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
