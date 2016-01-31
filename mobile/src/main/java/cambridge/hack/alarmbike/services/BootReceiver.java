package cambridge.hack.alarmbike.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.Calendar;
import java.util.List;

import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.DateAlarm;
import io.realm.Realm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public static void registerAlarm(Context context,DateAlarm alarm){
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent newIntent = new Intent().setAction("init").putExtra("id", alarm.getId());
        PendingIntent pending = PendingIntent.getService(context, 0, newIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getInitDate().getTime(), pending);

        newIntent = new Intent().setAction("end").putExtra("id", alarm.getId());
        pending = PendingIntent.getService(context, 0, newIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getFinishDate().getTime(), pending);

        if(alarm.isTomorrowOnly()) {
            Calendar nextDay = Calendar.getInstance();
            nextDay.setTime(alarm.getDataCreate());
            nextDay.add(Calendar.DAY_OF_YEAR, 1);
            newIntent = new Intent().setAction("tomorrow").putExtra("id", alarm.getId());
            pending = PendingIntent.getService(context, 0, newIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, nextDay.getTimeInMillis(), pending);
        }
    }

    public BootReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Realm realm = Realm.getInstance(context);
        List<DateAlarm> alarms = realm.where(DateAlarm.class).findAll();

        for (DateAlarm alarm : alarms) {
            registerAlarm(context,alarm);
        }

        realm.close();
    }
}
