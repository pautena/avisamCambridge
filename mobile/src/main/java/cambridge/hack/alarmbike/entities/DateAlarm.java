package cambridge.hack.alarmbike.entities;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

import cambridge.hack.alarmbike.enums.OriginOrDestination;
import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Duffman on 31/1/16.
 */
public class DateAlarm extends RealmObject {
    public static int getNextId(Context context){
        Realm realm = Realm.getInstance(context);
        int nextID = (int) (realm.where(DateAlarm.class).maximumInt("id") + 1);
        realm.close();
        return nextID;
    }

    public static OriginOrDestination getOriginOrDestination(DateAlarm alarm){
        if(alarm.originOrDestination==1)
            return OriginOrDestination.DESTINATION;
        else if(alarm.originOrDestination==-1)
            return OriginOrDestination.ORIGIN;
        else
            return OriginOrDestination.NONE;
    }

    public static String getFormatTime(DateAlarm entrada) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(entrada.getInitDate())+" - "+format.format(entrada.getFinishDate());
    }

    @PrimaryKey
    private int id;
    private Date initDate;
    private Date finishDate;
    private boolean tomorrowOnly;
    private Station station;
    private int originOrDestination;


    public DateAlarm(){}


    public DateAlarm(Context context,Date initDate,Date finishDate,boolean tomorrowOnly,Station station,OriginOrDestination originOrDestination){
        this.id=getNextId(context);
        this.initDate=initDate;
        this.finishDate=finishDate;
        this.tomorrowOnly=tomorrowOnly;
        this.station=station;
        if(originOrDestination.equals(OriginOrDestination.DESTINATION))
            this.originOrDestination=1;
        else if(originOrDestination.equals(OriginOrDestination.ORIGIN))
            this.originOrDestination=-1;
        else
            this.originOrDestination=0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getInitDate() {
        return initDate;
    }

    public void setInitDate(Date initDate) {
        this.initDate = initDate;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public boolean isTomorrowOnly() {
        return tomorrowOnly;
    }

    public void setTomorrowOnly(boolean tomorrowOnly) {
        this.tomorrowOnly = tomorrowOnly;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public int getOriginOrDestination() {
        return originOrDestination;
    }

    public void setOriginOrDestination(int originOrDestination) {
        this.originOrDestination = originOrDestination;
    }
}
