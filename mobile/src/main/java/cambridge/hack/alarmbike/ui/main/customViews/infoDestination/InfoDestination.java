package cambridge.hack.alarmbike.ui.main.customViews.infoDestination;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.WebStorage;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.entities.Station;
import cambridge.hack.alarmbike.enums.OriginOrDestination;

/**
 * Created by Duffman on 30/1/16.
 */
public class InfoDestination extends LinearLayout {

    TextView tvTop,tvBottom;
    LinearLayout rootLayout;
    Button buttonOrigin,buttonDestination,buttonAddAlarm;
    private boolean isShown;
    private Station station;
    private OriginOrDestination state=OriginOrDestination.NONE;
    private View.OnClickListener onClickDestination= new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };
    private View.OnClickListener onClickOrigin= new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    private OnClickListener onClickAddAlarm = new OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    public InfoDestination(Context context) {
        super(context);
        initialize();
    }

    public InfoDestination(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    private void initialize(){
        inflate(getContext(), R.layout.info_destination, this);
        tvTop= (TextView) findViewById(R.id.tv_top);
        tvBottom= (TextView) findViewById(R.id.tv_bottom);
        rootLayout = (LinearLayout) findViewById(R.id.root_layout);
        buttonOrigin= (Button) findViewById(R.id.button_origin);
        buttonDestination=(Button) findViewById(R.id.button_destination);
        buttonAddAlarm=(Button) findViewById(R.id.button_add_alarm);
        isShown =false;

        buttonOrigin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideButtons();
                String part = getResources().getString(R.string.info_destination_title_part_origin);
                setTop(getResources().getString(R.string.info_destination_title, part, station.getName()));
                onClickOrigin.onClick(v);
            }
        });

        buttonDestination.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                hideButtons();
                String part = getResources().getString(R.string.info_destination_title_part_destination);
                setTop(getResources().getString(R.string.info_destination_title, part, station.getName()));
                onClickDestination.onClick(v);
            }
        });

        buttonAddAlarm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickAddAlarm.onClick(v);
            }
        });
    }

    public void setTop(String text){
        tvTop.setText(text);
    }

    public void setBottom(String text){
        tvBottom.setText(text);
    }

    public void showStation(Station station){
        this.station=station;
        state=OriginOrDestination.NONE;
        showButtons();

        String topText= getResources().getString(R.string.info_destination_title,"",station.getName());
        setTop(topText);
        String bottomText= getResources().getString(R.string.info_bikes_slots,station.getBikes(),station.getSlots());
        setBottom(bottomText);

        show();
    }

    public void setState(OriginOrDestination state){
        this.state=state;
    }

    public OriginOrDestination getState(){
        return state;
    }

    public void setOnClickOriginListener(OnClickListener listener){
        onClickOrigin=listener;
    }

    public void setOnClickDestinationListener(OnClickListener listener){
        onClickDestination=listener;
    }

    public void setOnClickAddAlarmListener(OnClickListener listener){
        onClickAddAlarm = listener;
    }

    public void showButtons(){
        buttonOrigin.setVisibility(View.VISIBLE);
        buttonDestination.setVisibility(View.VISIBLE);
        buttonAddAlarm.setVisibility(View.VISIBLE);
    }

    public void hideButtons(){
        buttonOrigin.setVisibility(View.GONE);
        buttonDestination.setVisibility(View.GONE);
        buttonAddAlarm.setVisibility(View.GONE);
    }

    public void show(){
        if(!isShown && station!=null) {
            rootLayout.setVisibility(View.VISIBLE);
            ObjectAnimator anim = ObjectAnimator.ofFloat(rootLayout, "translationY", -getHeight(), 0);
            anim.setDuration(400);
            anim.start();
            isShown =true;
        }
    }

    public void hide(){
        if(isShown) {
            ObjectAnimator anim = ObjectAnimator.ofFloat(rootLayout, "translationY", 0, -getHeight());
            anim.setDuration(400);
            anim.start();
            isShown = false;
        }
    }

    @Override
    public boolean isShown() {
        return isShown;
    }

    public void swipeVisibility() {
        if(isShown()) hide();
        else show();
    }
}
