package app.paste_it.models.greendao;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Madeyedexter on 13-05-2017.
 */
@Entity
public class ImageURL implements Parcelable {
    public static final Creator<ImageURL> CREATOR = new Creator<ImageURL>() {
        @Override
        public ImageURL createFromParcel(Parcel in) {
            return new ImageURL(in);
        }

        @Override
        public ImageURL[] newArray(int size) {
            return new ImageURL[size];
        }
    };
    @Id(autoincrement = true)
    private Long id;
    private String url;
    private String pasteId;

    protected ImageURL(Parcel in) {
        url = in.readString();

        pasteId = in.readString();
    }

    @Generated(hash = 639769370)
    public ImageURL(Long id, String url, String pasteId) {
        this.id = id;
        this.url = url;
        this.pasteId = pasteId;
    }

    @Generated(hash = 661544148)
    public ImageURL() {
    }

    @Override
    public String toString() {
        return "ImageURL{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", pasteId='" + pasteId + '\'' +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPasteId() {
        return this.pasteId;
    }

    public void setPasteId(String pasteId) {
        this.pasteId = pasteId;
    }
}
