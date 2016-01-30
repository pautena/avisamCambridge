package cambridge.hack.alarmbike.ui.main.customViews.infoDestination;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cambridge.hack.alarmbike.R;
import cambridge.hack.alarmbike.entities.Station;

/**
 * Created by Duffman on 30/1/16.
 */
public class InfoDestination extends LinearLayout {

    TextView tvTop,tvBottom;
    LinearLayout rootLayout;
    private boolean isShown;
    private Station station;

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
        isShown =false;
    }

    public void setTop(String text){
        tvTop.setText(text);
    }

    public void setBottom(String text){
        tvBottom.setText(text);
    }

    public void showStation(Station station){
        this.station=station;
        String topText= getResources().getString(R.string.info_destination_title,station.getName());
        setTop(topText);
        String bottomText= getResources().getString(R.string.info_bikes_slots,station.getBikes(),station.getSlots());
        setBottom(bottomText);

        show();
    }

    public void show(){
        if(!isShown && station!=null) {
            rootLayout.setVisibility(View.VISIBLE);
            ObjectAnimator anim = ObjectAnimator.ofFloat(rootLayout, "translationY", -getHeight(), 0);
            anim.setDuration(400);
            anim.start();
            isShown =true;
        }else if(station!=null){
            Toast.makeText(getContext(),R.string.no_destination_selected,Toast.LENGTH_SHORT).show();
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
