package carlosmada22.com.get_around.Modelo;

/**
 * Created by root on 12/18/17.
 */
public class Mapa {

    private int id;
    private String name;
    private double latne;
    private double longne;
    private double latsw;
    private double longsw;
    private float zoom;
    private double latc;
    private double longc;

    public Mapa(int id, String name, double latne, double longne, double latsw, double longsw, float zoom, double latc, double longc) {
        this.id = id;
        this.name = name;
        this.latne = latne;
        this.longne = longne;
        this.latsw = latsw;
        this.longsw = longsw;
        this.zoom = zoom;
        this.latc = latc;
        this.longc = longc;
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

    public double getLatne() {
        return latne;
    }

    public void setLatne(double latne) {
        this.latne = latne;
    }

    public double getLongne() {
        return longne;
    }

    public void setLongne(double longne) {
        this.longne = longne;
    }

    public double getLatsw() {
        return latsw;
    }

    public void setLatsw(double latsw) {
        this.latsw = latsw;
    }

    public double getLongsw() {
        return longsw;
    }

    public void setLongsw(double longsw) {
        this.longsw = longsw;
    }

    public float getZoom() {
        return zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;
    }

    public double getLatc() {
        return latc;
    }

    public void setLatc(double latc) {
        this.latc = latc;
    }

    public double getLongc() {
        return longc;
    }

    public void setLongc(double longc) {
        this.longc = longc;
    }
}
