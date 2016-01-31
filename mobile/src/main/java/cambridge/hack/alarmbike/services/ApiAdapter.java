package cambridge.hack.alarmbike.services;

import android.util.Log;

import com.google.android.gms.common.api.Api;
import com.google.gson.JsonObject;

import java.io.IOException;

import cambridge.hack.alarmbike.callback.RegisterUserCallback;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.Station;
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

    public static ApiAdapter getInstance(){
        if(instance==null) instance= new ApiAdapter();
        return instance;
    }

    ApiService service;

    private ApiAdapter(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://" + API_URL + ":"+ API_PORT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        service = retrofit.create(ApiService.class);
    }

    public void registerUser(String email, String registrationId, final RegisterUserCallback callback){
        Call<JsonObject> call = service.signIn(email, registrationId);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Response<JsonObject> response, Retrofit retrofit) {
                Log.d("ApiAdapter", "onRresponse body: " + response.body());
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


    public void createAlarmDestination(Station station){
        //TODO
    }

    public void createAlarmOrigin(Station station){
        //TODO
    }

    public void finishAlarm(){
        //TODO
    }
}
