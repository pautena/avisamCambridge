package cambridge.hack.alarmbike.services;

import com.google.gson.JsonObject;

import retrofit.Call;
import retrofit.http.DELETE;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by Duffman on 30/1/16.
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("/signup/")
    Call<JsonObject> signIn(@Field("email") String email, @Field("google_token")String registrationId);

    @FormUrlEncoded
    @POST("/alarm/")
    Call<Integer> createAlarmDestination(@Field("email")String email, @Field("station_id") String stationId);

    @FormUrlEncoded
    @POST("/alarmOrigin/")
    Call<Integer> createAlarmOrigin(@Field("email")String email, @Field("station_id") String stationId);

    @DELETE("/alarm/{id}/")
    Call<JsonObject> finishAlarmDestination(@Path("id") String id);

    @DELETE("/alarmOrigin/{id}/")
    Call<JsonObject> finishAlarmOrigin(@Path("id") String id);
}
