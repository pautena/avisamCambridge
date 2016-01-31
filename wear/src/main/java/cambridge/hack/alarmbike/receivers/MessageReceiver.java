package cambridge.hack.alarmbike.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import cambridge.hack.alarmbike.MapActivity.MapHandler;
import cambridge.hack.alarmbike.model.Station;

public class MessageReceiver extends BroadcastReceiver {
    private static Gson g = new Gson();
    private MapHandler handler;

    public MessageReceiver() {
        super();
    }

    public MessageReceiver(MapHandler handler) {
        super();
        this.handler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String msg = intent.getStringExtra("message");


        // TODO filter message
        Station s = new Station(new Gson().fromJson(msg, JsonObject.class));
        System.out.println(msg);

        handler.post(s);
    }
}
