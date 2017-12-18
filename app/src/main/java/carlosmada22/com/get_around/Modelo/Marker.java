package carlosmada22.com.get_around.Modelo;

/**
 * Created by carlosmada22 on 22/9/17.
 */
public class Marker {
    private int id;
    private String name;
    private double lat;
    private double lon;
    private String description;
    private int category;


    public Marker(int id, String name, double lat, double lon, String description, int category) {

        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.description = description;
        this.category = category;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }
}
