package app.paste_it.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Madeyedexter on 30-05-2017.
 */

public class ConfirmDialogMessage implements Parcelable {

    private String title;
    private String message;

    public ConfirmDialogMessage(String title, String message) {
        this.title = title;
        this.message = message;
    }

    @Override
    public String toString() {

        return "ConfirmDialogMessage{" +
                "title='" + title + '\'' +
                ", message='" + message + '\'' +
                '}';
    }

    protected ConfirmDialogMessage(Parcel in) {
        title = in.readString();
        message = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(message);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ConfirmDialogMessage> CREATOR = new Creator<ConfirmDialogMessage>() {
        @Override
        public ConfirmDialogMessage createFromParcel(Parcel in) {
            return new ConfirmDialogMessage(in);
        }

        @Override
        public ConfirmDialogMessage[] newArray(int size) {
            return new ConfirmDialogMessage[size];
        }
    };
}
