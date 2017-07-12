package byAJ.models;

import com.cloudinary.StoredFile;

import javax.persistence.*;
import java.util.Date;

@Entity(name = "photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Basic
    private String title;

    @Basic
    private String image;

    @Basic
    private Date createdAt = new Date();

    private String topmessage;

    private String botmessage;

    private String type;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public StoredFile getUpload() {
        StoredFile file = new StoredFile();
        file.setPreloadedFile(image);
        return file;
    }

    public void setUpload(StoredFile file) {
        this.image = file.getPreloadedFile();
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTopmessage() {
        return topmessage;
    }

    public void setTopmessage(String topmessage) {
        this.topmessage = topmessage;
    }

    public String getBotmessage() {
        return botmessage;
    }

    public void setBotmessage(String botmessage) {
        this.botmessage = botmessage;
    }
}
