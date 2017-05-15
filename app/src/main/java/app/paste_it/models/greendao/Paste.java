package app.paste_it.models.greendao;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

/**
 * Created by Madeyedexter on 13-05-2017.
 */
@Entity
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
    @Id
    private String id;
    private Long modified;
    private Long created;
    private String title;
    private String text;
    @ToMany(referencedJoinProperty = "pasteId")
    private List<ImageURL> urls;
    @ToMany(referencedJoinProperty = "pasteId")
    private List<Tag> tags;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 230182178)
    private transient PasteDao myDao;

    protected Paste(Parcel in) {
        id = in.readString();
        title = in.readString();
        text = in.readString();
        urls = in.createTypedArrayList(ImageURL.CREATOR);
        tags = in.createTypedArrayList(Tag.CREATOR);
    }

    @Generated(hash = 1080823105)
    public Paste(String id, Long modified, Long created, String title,
            String text) {
        this.id = id;
        this.modified = modified;
        this.created = created;
        this.title = title;
        this.text = text;
    }

    @Generated(hash = 647083407)
    public Paste() {
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
        dest.writeTypedList(urls);
        dest.writeTypedList(tags);
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

    public Long getModified() {
        return this.modified;
    }

    public void setModified(Long modified) {
        this.modified = modified;
    }

    public Long getCreated() {
        return this.created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1236614839)
    public List<ImageURL> getUrls() {
        if (urls == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            ImageURLDao targetDao = daoSession.getImageURLDao();
            List<ImageURL> urlsNew = targetDao._queryPaste_Urls(id);
            synchronized (this) {
                if (urls == null) {
                    urls = urlsNew;
                }
            }
        }
        return urls;
    }

    public void setUrls(List<ImageURL> urls) {
        this.urls = urls;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1556834690)
    public synchronized void resetUrls() {
        urls = null;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1595444046)
    public List<Tag> getTags() {
        if (tags == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            TagDao targetDao = daoSession.getTagDao();
            List<Tag> tagsNew = targetDao._queryPaste_Tags(id);
            synchronized (this) {
                if (tags == null) {
                    tags = tagsNew;
                }
            }
        }
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 404234)
    public synchronized void resetTags() {
        tags = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 1374933456)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getPasteDao() : null;
    }
}
