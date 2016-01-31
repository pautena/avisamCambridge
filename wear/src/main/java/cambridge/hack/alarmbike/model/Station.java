package cambridge.hack.alarmbike.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonObject;

public class Station {

    private String id;
    private int uid;
    private String name;
    private int bikes;
    private int slots;
    private double latitude;
    private double longitude;

    public Station(JsonObject object){
        id = object.get("id").getAsString();
        uid=object.get("uid").getAsInt();
        name=object.get("name").getAsString();
        bikes=object.get("bikes").getAsInt();
        slots=object.get("slots").getAsInt();
        latitude=object.get("latitude").getAsDouble();
        longitude = object.get("longitude").getAsDouble();
    }

    public static LatLng getLatLng(Station station){
        return new LatLng(station.getLatitude(),station.getLongitude());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBikes() {
        return bikes;
    }

    public void setBikes(int bikes) {
        this.bikes = bikes;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}