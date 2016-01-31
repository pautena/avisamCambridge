package cambridge.hack.alarmbike.entities;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Duffman on 30/1/16.
 */
public class Alarm extends RealmObject {

    @PrimaryKey
    private int id;
    private Station station;

    public Alarm(){}

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
}
