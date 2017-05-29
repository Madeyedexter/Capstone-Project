package app.paste_it.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * Created by Madeyedexter on 14-05-2017.
 */
public class Paste implements Parcelable, Identity {
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
    private long modified;
    private long created;
    private String title;
    private String text;
    private Map<String, ImageModel> urls = new LinkedHashMap<>();
    private boolean archived;
    private HashMap<String, Tag> tags = new HashMap<>();

    public Paste() {
    }

    protected Paste(Parcel in) {
        id = in.readString();
        modified = in.readLong();
        created = in.readLong();
        title = in.readString();
        text = in.readString();
        archived = in.readByte() != 0;
        tags = new LinkedHashMap<>();
        ArrayList<Tag> tagArrayList = in.createTypedArrayList(Tag.CREATOR);
        for (Tag tag : tagArrayList) {
            tags.put(tag.getId(), tag);
        }
        //LongSparseArray may be an alternative here
        urls = new HashMap<>();
        ArrayList<ImageModel> imageModelArrayList = in.createTypedArrayList(ImageModel.CREATOR);
        for (ImageModel imageModel : imageModelArrayList) {
            urls.put(imageModel.getId(), imageModel);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeLong(modified);
        dest.writeLong(created);
        dest.writeString(title);
        dest.writeString(text);
        dest.writeByte((byte) (archived ? 1 : 0));
        dest.writeTypedList(new ArrayList<Parcelable>(tags.values()));
        dest.writeTypedList(new ArrayList<Parcelable>(urls.values()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
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

    public Map<String, ImageModel> getUrls() {
        return urls;
    }

    public void setUrls(HashMap<String, ImageModel> urls) {
        this.urls = urls;
    }

    public HashMap<String, Tag> getTags() {
        return tags;
    }

    public void setTags(HashMap<String, Tag> tags) {
        this.tags = tags;
    }

}
