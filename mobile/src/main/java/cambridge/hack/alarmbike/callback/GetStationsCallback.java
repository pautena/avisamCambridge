package cambridge.hack.alarmbike.callback;

import java.util.List;

import cambridge.hack.alarmbike.entities.Station;

/**
 * Created by Duffman on 30/1/16.
 */
public interface GetStationsCallback {
    void onGetStationsFinish(List<Station> stations);
    void onError(int code, String message);
    void onFailure(Throwable throwable);
}
