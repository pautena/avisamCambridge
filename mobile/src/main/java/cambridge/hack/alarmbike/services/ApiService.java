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
    @POST("/signup")
    Call<JsonObject> signIn(@Field("email") String email, @Field("googletoken")String registrationId);

    @FormUrlEncoded
    @POST("/alarm")
    Call<JsonObject> createAlarmDestination(@Field("appuser")String email, @Field("station") int stationId);

    @FormUrlEncoded
    @POST("/alarm")
    Call<JsonObject> createAlarmOrigin(@Field("appuser")String email, @Field("station") int stationId);

    @FormUrlEncoded
    @DELETE("/alarm/{id}")
    Call<JsonObject> finishAlarmDestination(@Path("id") int id);

    @FormUrlEncoded
    @DELETE("/alarmOrigin/{id}")
    Call<JsonObject> finishAlarmOrigin(@Path("id") int id);
}
