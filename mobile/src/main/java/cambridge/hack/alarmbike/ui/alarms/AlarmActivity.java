package cambridge.hack.alarmbike.ui.alarms;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.entities.Alarm;
import cambridge.hack.alarmbike.entities.DateAlarm;
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

    @OnClick(R.id.fab)
    public void onClickAddAlarm(View view){
        Intent intent = new Intent(this,AlarmActivity.class);
        startActivity(intent);
    }

    public void setAdapter(){
        List<DateAlarm> alarms = realm.where(DateAlarm.class).findAll();

        listView.setAdapter(new ListAdapter<DateAlarm>(this,R.layout.date_alarm_list_item,alarms) {
            @Override
            public void onEntrada(DateAlarm entrada, View view) {

            }
        });


    }

}
