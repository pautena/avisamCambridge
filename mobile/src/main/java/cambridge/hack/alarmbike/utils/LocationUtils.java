package cambridge.hack.alarmbike.utils;

import android.Manifest;
import android.content.Context;

/**
 * Created by Duffman on 30/1/16.
 */
public class LocationUtils {
    public static boolean checkLocationPermission(Context context){
        return PermissionUtils.checkPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                && PermissionUtils.checkPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
    }
}
