package carlosmada22.com.get_around;

/**
 * Created by carlosmada22 on 14/9/17.
 */
public class Ciudades {

    double lat;
    double lon;
    String wikipedia;
    String city;

    public Ciudades(double lat, double  lon, String wikipedia, String city) {
        this.lat = lat;
        this.lon = lon;
        this.wikipedia = wikipedia;
        this.city = city;
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

    public String getWikipedia() {
        return wikipedia;
    }

    public void setWikipedia(String wikipedia) {
        this.wikipedia = wikipedia;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }



}
