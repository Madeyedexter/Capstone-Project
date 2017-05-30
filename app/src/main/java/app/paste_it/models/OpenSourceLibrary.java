package app.paste_it.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by n188851 on 30-05-2017.
 */

public class OpenSourceLibrary implements Parcelable {

    private String libraryName;
    private String copyright;
    private String license;

    public OpenSourceLibrary(String libraryName, String copyright, String license, String website) {
        this.libraryName = libraryName;
        this.copyright = copyright;
        this.license = license;
        this.website = website;
    }

    public String getLibraryName() {

        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    private String website;

    protected OpenSourceLibrary(Parcel in) {
        libraryName = in.readString();
        copyright = in.readString();
        license = in.readString();
        website = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(libraryName);
        dest.writeString(copyright);
        dest.writeString(license);
        dest.writeString(website);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OpenSourceLibrary> CREATOR = new Creator<OpenSourceLibrary>() {
        @Override
        public OpenSourceLibrary createFromParcel(Parcel in) {
            return new OpenSourceLibrary(in);
        }

        @Override
        public OpenSourceLibrary[] newArray(int size) {
            return new OpenSourceLibrary[size];
        }
    };
}
