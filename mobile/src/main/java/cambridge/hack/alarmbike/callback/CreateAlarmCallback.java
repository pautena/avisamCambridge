package cambridge.hack.alarmbike.callback;

import cambridge.hack.alarmbike.entities.Alarm;

/**
 * Created by Duffman on 31/1/16.
 */
public interface CreateAlarmCallback {
    void onCreateAlarm(Alarm alarm);
    void onError(Throwable t);
}
