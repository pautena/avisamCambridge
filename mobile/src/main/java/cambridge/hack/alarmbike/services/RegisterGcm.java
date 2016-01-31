package cambridge.hack.alarmbike.services;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.callback.RegisterUserCallback;
import cambridge.hack.alarmbike.utils.UserUtils;


/**
 * Created by Duffman on 30/1/16.
 */
public class RegisterGcm extends AsyncTask<Void,Void,String> implements RegisterUserCallback {

    private Context context;
    private String senderId,email;

    public RegisterGcm(Context context){
        this.context=context.getApplicationContext();
        senderId=context.getResources().getString(R.string.google_dev_app_id);
    }

    @Override
    protected String doInBackground(Void... params)
    {
        email = UserUtils.getUserEmail(context);

        try
        {
            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(context);

            //Nos registramos en los servidores de GCM
            String regid = gcm.register(senderId);

            Log.d("TaskRegisterGCM", "Registrado en GCM: registration_id=" + regid);

            return regid;

        }catch (IOException ex){
            Log.e("TaskRegisterGCM", "Error registro en GCM:" + ex.getMessage());
        }

        return null;
    }



    @Override
    protected void onPostExecute(String regId) {
        super.onPostExecute(regId);
        if(regId!=null && email!=null){
            ApiAdapter.getInstance(context).registerUser(email, regId, this);
        }
    }

    @Override
    public void onRegisterFinish() {
        Toast.makeText(context,R.string.registration_user_ok,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        Log.e("RegisterGcm", "onError: " + throwable.getMessage());
        Toast.makeText(context,R.string.error_register_user,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onError(int code, String message) {
        Log.e("RegisterGcm", "onError. code: "+code+", message: "+message);
        Toast.makeText(context,R.string.error_register_user,Toast.LENGTH_SHORT).show();

    }
}
