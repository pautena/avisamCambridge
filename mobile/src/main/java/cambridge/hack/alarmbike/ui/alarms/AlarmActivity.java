package cambridge.hack.alarmbike.ui.alarms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.DateAlarm;
import cambridge.hack.alarmbike.enums.OriginOrDestination;
import cambridge.hack.alarmbike.utils.ListAdapter;
import io.realm.Realm;

public class AlarmActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;

    @Bind(R.id.listView)
    ListView listView;


    private Realm realm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        realm= Realm.getInstance(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setAdapter();
    }

    @Override
    protected void onDestroy() {
        realm.close();
        super.onDestroy();
    }

    public void setAdapter(){
        List<DateAlarm> alarms = realm.where(DateAlarm.class).findAll();

        listView.setAdapter(new ListAdapter<DateAlarm>(this,R.layout.date_alarm_list_item,alarms) {
            @Override
            public void onEntrada(final DateAlarm entrada, View view) {
                TextView tvStationName=(TextView) view.findViewById(R.id.tv_station_name);
                TextView timeTv = (TextView) view.findViewById(R.id.textView);
                TextView tvOriginOrDestination = (TextView) view.findViewById(R.id.tv_origin_destination);
                CheckBox tomorrowCb= (CheckBox) view.findViewById(R.id.checkBox);
                ImageView closeIv = (ImageView) view.findViewById(R.id.imageView);


                tvStationName.setText(entrada.getStation().getName());

                String orOrDest;

                if(DateAlarm.getOriginOrDestination(entrada).equals(OriginOrDestination.DESTINATION))
                    orOrDest = getResources().getString(R.string.destination);
                else
                    orOrDest = getResources().getString(R.string.origin);

                tvOriginOrDestination.setText(orOrDest);
                String timeString = DateAlarm.getFormatTime(entrada);
                timeTv.setText(timeString);
                tomorrowCb.setChecked(entrada.isTomorrowOnly());
                closeIv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        realm.beginTransaction();
                        entrada.removeFromRealm();
                        realm.commitTransaction();
                        setAdapter();
                    }
                });

            }
        });


    }

}
