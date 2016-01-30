package cambridge.hack.alarmbike.entities;

import android.content.Context;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import cambridge.hack.alarmbike.utils.Date;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Station extends RealmObject{

    public static void setMarkerByBikes(Station station,MarkerOptions markerOptions) {
        if(station.getBikes()>5){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        }else if(station.getBikes()!=0){
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
        }else{
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        }
    }

    public static JsonObject getJson(Station station){
        JsonObject object = new JsonObject();

        object.addProperty("id",station.getId());
        object.addProperty("uid",station.getUid());
        object.addProperty("name",station.getName());
        object.addProperty("bikes",station.getBikes());
        object.addProperty("slots",station.getSlots());
        object.addProperty("latitude",station.getLatitude());
        object.addProperty("longitude",station.getLongitude());

        return object;
    }

    public static LatLng getLatLng(Station station){
        return new LatLng(station.getLatitude(),station.getLongitude());
    }

    @PrimaryKey
    private String id;
    private int uid;
    private String name;
    private int bikes;
    private int slots;
    private double latitude;
    private double longitude;

    public Station(){
    }

    public Station(JsonObject object){
        id = object.get("id").getAsString();
        uid=object.get("uid").getAsInt();
        name=object.get("name").getAsString();
        bikes=object.get("bikes").getAsInt();
        slots=object.get("slots").getAsInt();
        latitude=object.get("latitude").getAsDouble();
        longitude = object.get("longitude").getAsDouble();
    }

    public Station( String id, int uid,String name, int bikes, int slots, double latitude, double longitude){
        this.id=id;
        this.uid=uid;
        this.name=name;
        this.bikes=bikes;
        this.slots=slots;
        this.latitude=latitude;
        this.longitude=longitude;
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