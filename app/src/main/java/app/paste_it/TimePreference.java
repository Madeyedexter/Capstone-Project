package app.paste_it;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class TimePreference extends android.support.v7.preference.DialogPreference{

    public static int getHour(String time) {
        String[] pieces=time.split(":");

        return(Integer.parseInt(pieces[0]));
    }

    public static int getMinute(String time) {
        String[] pieces=time.split(":");
        return(Integer.parseInt(pieces[1]));
    }

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        setTitle(getContext().getString(R.string.set_time));
        setPositiveButtonText(getContext().getString(R.string.set));
        setNegativeButtonText(getContext().getString(R.string.cancel));
    }




    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return(a.getString(index));
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String time;
        if (restoreValue) {
            if (defaultValue==null) {
                time=getPersistedString("00:00");
            }
            else {
                time=getPersistedString(defaultValue.toString());
            }
        }
        else {
            time=defaultValue.toString();
        }
        int lastHour = getHour(time);
        int lastMinute = getMinute(time);
    }
}
