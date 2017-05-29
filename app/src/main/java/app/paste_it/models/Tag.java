package app.paste_it.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

/**
 * Created by Madeyedexter on 13-05-2017.
 */
public class Tag implements Parcelable, Identity {
    public static final Creator<Tag> CREATOR = new Creator<Tag>() {
        @Override
        public Tag createFromParcel(Parcel in) {
            return new Tag(in);
        }

        @Override
        public Tag[] newArray(int size) {
            return new Tag[size];
        }
    };
    private String id;
    private String label;
    private String pasteId;
    @Exclude
    private boolean selected;

    public Tag() {
    }

    protected Tag(Parcel in) {
        id = in.readString();

        label = in.readString();
        pasteId = in.readString();
        selected = in.readByte() != 0;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(label);
        dest.writeString(pasteId);
        dest.writeByte((byte) (selected ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", pasteId='" + pasteId + '\'' +
                ", selected='" + selected + '\'' +
                '}';
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPasteId() {
        return this.pasteId;
    }

    public void setPasteId(String pasteId) {
        this.pasteId = pasteId;
    }
}
