package cambridge.hack.alarmbike.entities;

import cambridge.hack.alarmbike.enums.OriginOrDestination;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Duffman on 30/1/16.
 */
public class Alarm {

    private int id;
    private Station station;
    private OriginOrDestination state;

    public Alarm(Station station, OriginOrDestination state) {
        this(-1,station,state);
    }

    public Alarm(int id, Station station, OriginOrDestination state){
        this.id=id;
        this.station=station;
        this.state=state;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public OriginOrDestination getState() {
        return state;
    }

    public void setState(OriginOrDestination state) {
        this.state = state;
    }
}
