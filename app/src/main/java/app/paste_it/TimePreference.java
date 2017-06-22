package app.paste_it;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

public class TimePreference extends android.support.v7.preference.DialogPreference{

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        setTitle(getContext().getString(R.string.set_time));
        setPositiveButtonText(getContext().getString(R.string.set));
        setNegativeButtonText(getContext().getString(R.string.cancel));
        setDialogLayoutResource(R.layout.timepicker);
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
        setSummary(time);
    }

    public boolean persistTime(String value){
        return persistString(value);
    }
}
