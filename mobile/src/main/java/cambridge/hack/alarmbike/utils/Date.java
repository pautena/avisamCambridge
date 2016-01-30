package cambridge.hack.alarmbike.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by pau on 22/01/15.
 */
public class Date extends java.util.Date {
    private static final String STR_NODE="yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    public static final String STR_INIT_DATE="2010-01-01 00:00:00";
    public static final String STR_DATE_FORMAT="yyyy-MM-dd HH:mm:ss";
    private static final String STR_DATE_FORMAT_SUPER="yyyy/MM/dd HH:mm:ss";
    private static final String STR_BIZI_FORMAT="yyyy/MM/dd";
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(STR_DATE_FORMAT);
    private static final SimpleDateFormat DATE_FORMAT_SUPER= new SimpleDateFormat(STR_DATE_FORMAT_SUPER);
    private static final SimpleDateFormat DATE_FORMAT_NODE= new SimpleDateFormat(STR_NODE);
    private static final SimpleDateFormat DATE_FORMAT_BIZI= new SimpleDateFormat(STR_BIZI_FORMAT);

    public Date(){super();}

    public Date(java.util.Date date){
        super(DATE_FORMAT_SUPER.format(date));
    }

    public Date(String data) throws ParseException{
        super(DATE_FORMAT_SUPER.format((DATE_FORMAT.parse(data))));
    }

    public Date(Calendar calendar){
        super(DATE_FORMAT_SUPER.format(calendar.getTime()));
    }


    public static Date getInitDate(){
        try {
            return new Date(STR_INIT_DATE);
        }catch(ParseException e){ return null;}
    }

    public static Date getApiDate(String str) {
        java.util.Date date = null;
        try {
            date = DATE_FORMAT_NODE.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date(date);
    }

    public String toApiString(){
        return DATE_FORMAT_NODE.format(this);
    }

    @Override
    public String toString(){
        return DATE_FORMAT.format(this);
    }

    @Override
    public int compareTo(java.util.Date date) {
        String d1 = toString();
        String d2 = DATE_FORMAT.format(date);
        return d1.compareTo(d2);
    }

    public String getBiziString(){
        return DATE_FORMAT_BIZI.format(this);
    }
}
