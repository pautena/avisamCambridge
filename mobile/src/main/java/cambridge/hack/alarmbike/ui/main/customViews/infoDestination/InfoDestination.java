package cambridge.hack.alarmbike.ui.main.customViews.infoDestination;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cambridge.hack.alarmbike.R;

/**
 * Created by Duffman on 30/1/16.
 */
public class InfoDestination extends LinearLayout {

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
    }
}
