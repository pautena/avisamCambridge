package cambridge.hack.alarmbike.utils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.util.Log;

/**
 * Created by Duffman on 31/1/16.
 */
public class UserUtils {
    public static String getUserEmail(Context context){
        AccountManager manager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] accounts = manager.getAccounts();
        String email=null;

        boolean found= false;
        for(int i=0; i<accounts.length && !found;++i){
            if(checkIsEmail(accounts[i].name)){
                email = accounts[i].name;
                Log.d("RegisterGcm", "registration email: " + email);
            }
        }
        return email;
    }

    public static boolean checkIsEmail(String email){
        String[] parts = email.split("@");
        if (parts.length > 0 && parts[0] != null)
            return true;
        else
            return false;
    }
}
