package app.paste_it.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Madeyedexter on 01-06-2017.
 */

public class WidgetPreference implements Parcelable{
    private int noOfItems = 3;

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }

    protected WidgetPreference(Parcel in) {
        noOfItems = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(noOfItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<WidgetPreference> CREATOR = new Creator<WidgetPreference>() {
        @Override
        public WidgetPreference createFromParcel(Parcel in) {
            return new WidgetPreference(in);
        }

        @Override
        public WidgetPreference[] newArray(int size) {
            return new WidgetPreference[size];
        }
    };

    public WidgetPreference(int count){
        this.noOfItems=count;
    }
}
