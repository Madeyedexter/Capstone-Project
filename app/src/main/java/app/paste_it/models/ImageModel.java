package app.paste_it.models;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Created by Madeyedexter on 13-05-2017.
 */
@Entity
public class ImageModel implements Parcelable {

    public static final Creator<ImageModel> CREATOR = new Creator<ImageModel>() {
        @Override
        public ImageModel createFromParcel(Parcel in) {
            return new ImageModel(in);
        }

        @Override
        public ImageModel[] newArray(int size) {
            return new ImageModel[size];
        }
    };
    @Id
    private String id;
    private String downloadURL;
    private String pasteId;
    private String storageLocation;

    @Exclude
    private transient Uri contentUri;

    //The file name of the model saved in local cache
    //this should always be set before we upload the file
    private String fileName;
    @Exclude
    private int width;
    @Exclude
    private int height;


    protected ImageModel(Parcel in) {
        id = in.readString();
        downloadURL = in.readString();
        pasteId = in.readString();
        storageLocation = in.readString();
        fileName = in.readString();
        width = in.readInt();
        height = in.readInt();
    }

    @Generated(hash = 401291980)
    public ImageModel(String id, String downloadURL, String pasteId,
                      String storageLocation, String fileName, int width, int height) {
        this.id = id;
        this.downloadURL = downloadURL;
        this.pasteId = pasteId;
        this.storageLocation = storageLocation;
        this.fileName = fileName;
        this.width = width;
        this.height = height;
    }

    @Generated(hash = 799163379)
    public ImageModel() {
    }

    @Override
    public String toString() {
        return "ImageModel{" +
                "id=" + id +
                ", downloadURL='" + downloadURL + '\'' +
                ", pasteId='" + pasteId + '\'' +
                ", storageLocation='" + storageLocation + '\'' +
                ", contentUri=" + contentUri +
                ", fileName='" + fileName + '\'' +
                ", width=" + width +
                ", height=" + height +
                '}';
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(downloadURL);
        dest.writeString(pasteId);
        dest.writeString(storageLocation);
        dest.writeString(fileName);
        dest.writeInt(width);
        dest.writeInt(height);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownloadURL() {
        return this.downloadURL;
    }

    public void setDownloadURL(String downloadURL) {
        this.downloadURL = downloadURL;
    }

    public String getPasteId() {
        return this.pasteId;
    }

    public void setPasteId(String pasteId) {
        this.pasteId = pasteId;
    }

    public String getStorageLocation() {
        return this.storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
