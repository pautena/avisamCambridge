package cambridge.hack.alarmbike.entities;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Duffman on 31/1/16.
 */
public class DateAlarm extends RealmObject {
    public static String getFormatTime(DateAlarm entrada) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(entrada.getInitDate())+" - "+format.format(entrada.getFinishDate());
    }

    @PrimaryKey
    private int id;
    private Date initDate;
    private Date finishDate;
    private boolean tomorrowOnly;


    public DateAlarm(){}

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
}
