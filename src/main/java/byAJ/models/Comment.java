package byAJ.models;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long commentid;
    private String username;
    private String commented;
    private Date date;
    private String photoid;

    public String getPhotoid() {
        return photoid;
    }

    public void setPhotoid(String photoid) {
        this.photoid = photoid;
    }

    public long getCommentid() {
        return commentid;
    }

    public void setCommentid(long commentid) {
        this.commentid = commentid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCommented() {
        return commented;
    }

    public void setCommented(String commented) {
        this.commented = commented;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
