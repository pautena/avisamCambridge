package cambridge.hack.alarmbike.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

/**
 * Created by duffman on 24/11/15.
 */
public final class PermissionUtils {
    public static boolean checkPermission(Context context,String permission){
        int permissionCheck = ContextCompat.checkSelfPermission(context, permission);
        return permissionCheck== PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermissions(List<String> permissions,AppCompatActivity activity,int permissionRequestCode){
        String[] arrayPermissions = permissions.toArray(new String[permissions.size()]);

        Log.d("PermissionUtils", "permissions: " + Arrays.toString(arrayPermissions));



        if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.READ_CONTACTS)) {
            Log.d("PermissionUtils","shouldShowRequestPermissionRationale");

        } else {
            ActivityCompat.requestPermissions(activity,
                    arrayPermissions,
                    permissionRequestCode);
        }
    }

    public static boolean checkVersionMarshmallowOrMore(){
        return checkVersionOrMore(Build.VERSION_CODES.M);
    }

    public static boolean checkVersionOrMore(int version){
        return Build.VERSION.SDK_INT >= version;
    }
}
