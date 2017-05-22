package app.paste_it.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Madeyedexter on 13-05-2017.
 */
@Entity
public class Tag implements Parcelable {
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
    @Id(autoincrement = true)
    private Long id;
    private String label;
    private String pasteId;

    protected Tag(Parcel in) {
        label = in.readString();
        pasteId = in.readString();
    }

    @Generated(hash = 1997334013)
    public Tag(Long id, String label, String pasteId) {
        this.id = id;
        this.label = label;
        this.pasteId = pasteId;
    }

    @Generated(hash = 1605720318)
    public Tag() {
    }

    @Override
    public String toString() {
        return "Tag{" +
                "id=" + id +
                ", label='" + label + '\'' +
                ", pasteId='" + pasteId + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(label);
        dest.writeString(pasteId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
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
