package cambridge.hack.alarmbike.services;

import com.google.android.gms.common.api.Api;

import cambridge.hack.alarmbike.callback.RegisterUserCallback;
import retrofit.GsonConverterFactory;
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

    public void registerUser(String email, String registrationId, RegisterUserCallback callback){
        //TODO: Fer la funció
    }
}
