package app.paste_it.models.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;


/**
 * Created by Madeyedexter on 14-05-2017.
 */

public class Paste implements Parcelable{
    public static final Creator<Paste> CREATOR = new Creator<Paste>() {
        @Override
        public Paste createFromParcel(Parcel in) {
            return new Paste(in);
        }

        @Override
        public Paste[] newArray(int size) {
            return new Paste[size];
        }
    };
    private String id;
    private Long modified;
    private Long created;
    private String title;
    private String text;
    private List<String> urls;
    private List<String> tags;

    protected Paste(Parcel in) {
        id = in.readString();
        title = in.readString();
        text = in.readString();
        urls = in.createStringArrayList();
        tags = in.createStringArrayList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getModified() {
        return modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        return "Paste{" +
                "id='" + id + '\'' +
                ", modified=" + modified +
                ", created=" + created +
                ", title='" + title + '\'' +
                ", text='" + text + '\'' +
                ", urls=" + urls +
                ", tags=" + tags +
                '}';

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeStringList(urls);
        dest.writeStringList(tags);
    }

    public Paste(){}

    @Override
    public int describeContents() {
        return 0;
    }
}
