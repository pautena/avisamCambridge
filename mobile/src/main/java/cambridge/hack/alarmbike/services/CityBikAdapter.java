package cambridge.hack.alarmbike.services;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cambridge.hack.alarmbike.callback.GetStationsCallback;
import cambridge.hack.alarmbike.entities.Station;
import io.realm.Realm;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Duffman on 30/1/16.
 */
public class CityBikAdapter {
    private static CityBikAdapter instance;

    public static CityBikAdapter getInstance(Context context){
        if(instance ==null) instance = new CityBikAdapter(context.getApplicationContext());
        return instance;
    }



    private static final String API_URL ="api.citybik.es";

    private final CityBikService service;
    private final Context context;

    private CityBikAdapter(Context context){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.context = context;

        service = retrofit.create(CityBikService.class);
    }

    public void getLondonStations(GetStationsCallback callback){
        getStations("barclays-cycle-hire",callback);
    }


    public void getStations(String stationsService,final GetStationsCallback callback){
        Log.d("CityBikAdapter","getStations");

        Call<JsonObject> call = service.getStations(stationsService);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                Log.d("CityBikAdapter","onresponse getStations: "+response.body());


                if(response.isSuccess()){
                    List<Station> stations = new ArrayList<>();

                    JsonArray stationsArray = response.body().get("network").getAsJsonObject().
                            get("stations").getAsJsonArray();

                    for(int i=0; i<stationsArray.size();++i){
                        JsonObject object = stationsArray.get(i).getAsJsonObject();
                        Station station = new Station(
                            object.get("id").getAsString(),
                            object.get("extra").getAsJsonObject().get("uid").getAsInt(),
                            object.get("name").getAsString(),
                            object.get("free_bikes").getAsInt(),
                            object.get("empty_slots").getAsInt(),
                            object.get("latitude").getAsDouble(),
                            object.get("longitude").getAsDouble()
                        );
                        stations.add(station);
                    }

                    Realm realm = Realm.getInstance(context);
                    realm.beginTransaction();
                    realm.copyToRealmOrUpdate(stations);
                    realm.commitTransaction();

                    callback.onGetStationsFinish(stations);
                }else{
                    try {
                        Log.e("CityBikAdapter","code: "+response.code()+", message: "+response.errorBody().string());
                        callback.onError(response.code(), response.errorBody().string());
                    }catch (IOException exception){
                        exception.printStackTrace();
                        callback.onFailure(exception);
                    }
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.e("CityBikAdapter","onFailure: "+t.getMessage());
                callback.onFailure(t);
            }
        });

    }
}
