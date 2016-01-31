package cambridge.hack.alarmbike.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import cambridge.hack.alarmbike.entities.DateAlarm;
import io.realm.Realm;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    public BootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Realm realm = Realm.getInstance(context);
        List<DateAlarm> alarms = realm.where(DateAlarm.class).findAll();

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        for (DateAlarm alarm : alarms) {
            Intent initIntent = new Intent().setAction("init").putExtra("id", alarm.getId());
            PendingIntent pending = PendingIntent.getService(context, 0, initIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getInitDate().getTime(), pending);

            Intent finishIntent = new Intent().setAction("finish").putExtra("id", alarm.getId());
            pending = PendingIntent.getService(context, 0, finishIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, alarm.getFinishDate().getTime(), pending);
        }

        realm.close();
    }
}
