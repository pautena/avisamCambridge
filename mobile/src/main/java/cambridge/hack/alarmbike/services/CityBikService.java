package cambridge.hack.alarmbike.services;

import com.google.gson.JsonObject;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by Duffman on 30/1/16.
 */
public interface CityBikService {
    @GET("/v2/networks/{service}")
    Call<JsonObject> getStations(@Path("service")String service);
}
