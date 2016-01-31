package cambridge.hack.alarmbike.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import cambridge.hack.alarmbike.callback.CreateAlarmCallback;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.DateAlarm;
import cambridge.hack.alarmbike.enums.OriginOrDestination;
import io.realm.Realm;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class AlarmService extends IntentService {

    public AlarmService() {
        super("AlarmService");
    }

    public static void registerAlarm(Context context, DateAlarm alarm) {
        ApiAdapter.getInstance(context).createAlarm(alarm.getStation(),
                (alarm.getOriginOrDestination() == OriginOrDestination.ORIGIN.ordinal()) ?
                        OriginOrDestination.ORIGIN : OriginOrDestination.DESTINATION, new CreateAlarmCallback() {
                    @Override
                    public void onCreateAlarm(Alarm alarm) {

                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
        ApiAdapter.getInstance(context).finishAlarm(alarm);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            int id = intent.getIntExtra("id", -1);
            if (id > 0) {
                DateAlarm alarm = Realm.getInstance(this).where(DateAlarm.class).equalTo("id", id).findFirst();
                registerAlarm(this, alarm);
            }
        }
    }
}
