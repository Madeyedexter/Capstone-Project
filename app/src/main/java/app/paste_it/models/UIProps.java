package app.paste_it.models;

import android.os.Parcel;
import android.os.Parcelable;

public class UIProps implements Parcelable{
        private int statusBarBackgroundColor;
        private int keySelectedDrawerTag;
    private String sectionText;

    public String getSectionText() {
        return sectionText;
    }

    public void setSectionText(String sectionText) {
        this.sectionText = sectionText;
    }

    protected UIProps(Parcel in) {
        statusBarBackgroundColor = in.readInt();
        keySelectedDrawerTag = in.readInt();
        sectionText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(statusBarBackgroundColor);
        dest.writeInt(keySelectedDrawerTag);
        dest.writeString(sectionText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UIProps> CREATOR = new Creator<UIProps>() {
        @Override
        public UIProps createFromParcel(Parcel in) {
            return new UIProps(in);
        }

        @Override
        public UIProps[] newArray(int size) {
            return new UIProps[size];
        }
    };

    public int getStatusBarBackgroundColor() {
        return statusBarBackgroundColor;
    }

    public void setStatusBarBackgroundColor(int statusBarBackgroundColor) {
        this.statusBarBackgroundColor = statusBarBackgroundColor;
    }

    public Integer getKeySelectedDrawerTag() {
        return keySelectedDrawerTag;
    }

    public void setKeySelectedDrawerTag(Integer keySelectedDrawerTag) {
        this.keySelectedDrawerTag = keySelectedDrawerTag;
    }

    public UIProps(){}


}