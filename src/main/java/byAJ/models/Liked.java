package byAJ.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created by Meeliana on 7/13/2017.
 */
@Entity
public class Liked {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long likedid;
    private long likednum;
    private long photoid;
    private String username;

    public long getLikedid() {
        return likedid;
    }

    public void setLikedid(long likedid) {
        this.likedid = likedid;
    }

    public long getLikednum() {
        return likednum;
    }

    public void setLikednum(long likednum) {
        this.likednum = likednum;
    }

    public long getPhotoid() {
        return photoid;
    }

    public void setPhotoid(long photoid) {
        this.photoid = photoid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
