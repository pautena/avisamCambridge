package cambridge.hack.alarmbike.services;

import com.google.gson.JsonObject;

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Duffman on 30/1/16.
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("")
    Call<JsonObject> signIn(@Field("email") String email, @Field("registrationid")String registrationId);

    @FormUrlEncoded
    @POST("")
    Call<JsonObject> createAlarmDestination();

    @FormUrlEncoded
    @POST("")
    Call<JsonObject> createAlarmOrigin();

    @FormUrlEncoded
    @DELETE("")
    Call<JsonObject> finishAlarm();
}
