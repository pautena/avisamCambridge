package cambridge.hack.alarmbike.ui.alarms;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.DateAlarm;
import cambridge.hack.alarmbike.entities.Station;
import cambridge.hack.alarmbike.enums.OriginOrDestination;
import cambridge.hack.alarmbike.services.AlarmService;
import cambridge.hack.alarmbike.services.BootReceiver;
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

    @Bind(R.id.tvNameStation)
    TextView tvNameStation;

    @Bind(R.id.radio_origin)
    RadioButton radioOrigin;

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

        int uid=getIntent().getIntExtra(ARG_STATION,-1);
        station = realm.where(Station.class).equalTo("uid",uid).findFirst();
        tvNameStation.setText(station.getName());

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
        OriginOrDestination originOrDestination;

        if(radioOrigin.isChecked())
            originOrDestination= OriginOrDestination.ORIGIN;
        else
            originOrDestination=OriginOrDestination.DESTINATION;

        DateAlarm alarm = new DateAlarm(this,
                initTime.getTime(),
                finalTime.getTime(),
                cbSelectOnlyTomorrow.isChecked(),
                station,
                originOrDestination);

        realm.beginTransaction();
        realm.copyToRealmOrUpdate(alarm);
        realm.commitTransaction();
        BootReceiver.registerAlarm(this, alarm);

        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.init_picker)
    public void selectInitTime(View view){
        TimePickerDialog dialog = new TimePickerDialog(this, R.style.Theme_Dialog,
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar aux = Calendar.getInstance();
                aux.set(Calendar.HOUR_OF_DAY,hourOfDay);
                aux.set(Calendar.MINUTE,minute);

                if(checkPosterior(aux,finalTime)){
                    initTime=aux;
                    tvSelectInitTime.setText(getFormatedDate(initTime));
                }else{
                    Toast.makeText(AddAlarmActivity.this,R.string.bad_time_input,Toast.LENGTH_SHORT).show();
                }
            }
        },initTime.get(Calendar.HOUR_OF_DAY),initTime.get(Calendar.MINUTE),true);
        dialog.show();
    }

    @OnClick(R.id.final_picker)
    public void selectFinalTime(View view){
        TimePickerDialog dialog = new TimePickerDialog(this, R.style.Theme_Dialog,
        new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar aux = Calendar.getInstance();
                aux.set(Calendar.HOUR_OF_DAY,hourOfDay);
                aux.set(Calendar.MINUTE,minute);

                if(checkPosterior(initTime,aux)){
                    finalTime=aux;
                    tvSelectFinalTime.setText(getFormatedDate(finalTime));
                }else{
                    Toast.makeText(AddAlarmActivity.this,R.string.bad_time_input,Toast.LENGTH_SHORT).show();
                }
            }
        },finalTime.get(Calendar.HOUR_OF_DAY),finalTime.get(Calendar.MINUTE),true);
        dialog.show();
    }

    private String getFormatedDate(Calendar calendar){
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(calendar.getTime());
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
