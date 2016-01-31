package cambridge.hack.alarmbike.ui.alarms;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.entities.DateAlarm;
import cambridge.hack.alarmbike.entities.Station;
import io.realm.Realm;

public class AddAlarmActivity extends AppCompatActivity {

    public static final String ARG_STATION = "argStationUid";
    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.tv_select_init_time)
    TextView tvSelectInitTime;

    @Bind(R.id.tv_select_final_time)
    TextView tvSelectFinalTime;

    @Bind(R.id.cb_select_only_tomorrow)
    CheckBox cbSelectOnlyTomorrow;

    private Calendar initTime,finalTime;
    private Realm realm;
    private Station station;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        realm = Realm.getInstance(this);

        if(getIntent().getExtras().containsKey(ARG_STATION)){
            int uid=getIntent().getExtras().getInt(ARG_STATION);
            station = realm.where(Station.class).equalTo("uid",uid).findFirst();
        }

        initTime = Calendar.getInstance();
        initTime.set(Calendar.HOUR_OF_DAY, 8);
        initTime.set(Calendar.MINUTE,0);

        finalTime = Calendar.getInstance();
        finalTime.set(Calendar.HOUR_OF_DAY,9);
        finalTime.set(Calendar.MINUTE,0);
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    @OnClick(R.id.fab)
    public void onClickCreateAlarm(View view){
        Log.d("AddAlarmActivity", "onClickCreateAlarm");
        DateAlarm alarm = new DateAlarm();
    }

    @OnClick(R.id.init_picker)
    public void selectInitTime(View view){
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar aux = Calendar.getInstance();
                aux.set(Calendar.HOUR_OF_DAY,hourOfDay);
                aux.set(Calendar.MINUTE,minute);

                if(checkPosterior(finalTime,aux)){
                    initTime=aux;
                }else{
                    Toast.makeText(AddAlarmActivity.this,R.string.bad_time_input,Toast.LENGTH_SHORT).show();
                }
            }
        },initTime.get(Calendar.HOUR_OF_DAY),initTime.get(Calendar.MINUTE),true);
    }

    @OnClick(R.id.final_picker)
    public void selectFinalTime(View view){
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar aux = Calendar.getInstance();
                aux.set(Calendar.HOUR_OF_DAY,hourOfDay);
                aux.set(Calendar.MINUTE,minute);

                if(checkPosterior(aux,initTime)){
                    finalTime=aux;
                }else{
                    Toast.makeText(AddAlarmActivity.this,R.string.bad_time_input,Toast.LENGTH_SHORT).show();
                }
            }
        },finalTime.get(Calendar.HOUR_OF_DAY),finalTime.get(Calendar.MINUTE),true);
    }

    private boolean checkPosterior(Calendar end,Calendar ini){
        if(end.get(Calendar.HOUR_OF_DAY)>ini.get(Calendar.HOUR_OF_DAY))
            return true;
        else
            return end.get(Calendar.HOUR_OF_DAY)>ini.get(Calendar.MINUTE);
    }

    @OnClick(R.id.layout_select_only_tomorrow)
    public void selectOnlyTomorrow(View view){
        cbSelectOnlyTomorrow.setChecked(!cbSelectOnlyTomorrow.isChecked());
    }

}
