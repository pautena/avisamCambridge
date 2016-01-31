package cambridge.hack.alarmbike.services;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.gson.JsonObject;

import java.io.IOException;

import cambridge.hack.alarmbike.callback.CreateAlarmCallback;
import cambridge.hack.alarmbike.callback.RegisterUserCallback;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.Station;
import cambridge.hack.alarmbike.enums.OriginOrDestination;
import cambridge.hack.alarmbike.utils.UserUtils;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Duffman on 30/1/16.
 */
public class ApiAdapter {
    private static ApiAdapter instance;
    private static final String API_URL ="178.62.24.71";
    private static final int API_PORT=8000;

    public static ApiAdapter getInstance(Context context){
        if(instance==null) instance= new ApiAdapter(context.getApplicationContext());
        return instance;
    }

    private ApiService service;
    private Context context;

    private ApiAdapter(Context context){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + API_URL + ":"+ API_PORT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.context=context;

        service = retrofit.create(ApiService.class);
    }

    public void registerUser(String email, String registrationId, final RegisterUserCallback callback){
        Call<JsonObject> call = service.signIn(email, registrationId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                Log.d("ApiAdapter", "onResponse body("+response.code()+"): " + response.body());
                if(response.isSuccess()){

                }else{
                    try {
                        callback.onError(response.code(), response.errorBody().string());
                    }catch (IOException e){
                        e.printStackTrace();
                        callback.onError(e);
                    }
                }
            }

            @Override
            public void onFailure(Throwable t) {
                callback.onError(t);
            }
        });
    }

    public void createAlarm(Station station,OriginOrDestination state,CreateAlarmCallback callback){
        Log.d("ApiAdapter","createAlarm");
        if(state.equals(OriginOrDestination.DESTINATION)){
            createAlarmDestination(station,state,callback);
        }else{
            createAlarmOrigin(station,state,callback);
        }
    }


    public void createAlarmDestination(final Station station, final OriginOrDestination state, final CreateAlarmCallback callback){
        String email = UserUtils.getUserEmail(context);

        Call<Integer> call = service.createAlarmDestination(email,String.valueOf(station.getUid()));
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                Log.d("ApiAdapter","onResponse("+response.code()+"): "+response.body());
                if(response.isSuccess()) {
                    callback.onCreateAlarm(new Alarm(response.body(),station, state));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });

    }

    public void createAlarmOrigin(final Station station, final OriginOrDestination state, final CreateAlarmCallback callback){
        String email = UserUtils.getUserEmail(context);

        Call<Integer> call = service.createAlarmOrigin(email,String.valueOf(station.getUid()));
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Response<Integer> response, Retrofit retrofit) {
                Log.d("ApiAdapter", "onResponse(" + response.code() + "): " + response.body());
                if(response.isSuccess()) {
                    callback.onCreateAlarm(new Alarm(response.body(),station, state));
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void finishAlarm(Alarm alarm) {
        if(alarm.getState().equals(OriginOrDestination.DESTINATION))
            finishAlarmDestination(alarm);
        else
            finishAlarmOrigin(alarm);
    }

    public void finishAlarmDestination(Alarm alarm){
        Call<JsonObject> call = service.finishAlarmDestination(String.valueOf(alarm.getId()));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    Log.d("ApiAdapter","finishAlarmDestination success. body: "+response.body());
                }else{
                    Log.e("ApiAdapter","finishAlarmDestination error");

                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    public void finishAlarmOrigin(Alarm alarm){
        Call<JsonObject> call = service.finishAlarmOrigin(String.valueOf(alarm.getId()));

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                if(response.isSuccess()){
                    Log.d("ApiAdapter","finishAlarmDestination success. body: "+response.body());
                }else{
                    Log.e("ApiAdapter","finishAlarmDestination error");
                }
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }


}
