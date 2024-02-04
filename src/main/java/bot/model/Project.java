package bot.model;

import jakarta.persistence.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;

@Entity
@Table(name = "projects", schema = "public")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "deadline")
    private String deadLine;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "photo_id")
    private String photoId;

    public Project() {
    }

    public Project(String name, String description, String deadLine, String quantity, String photoId) {
        this.name = name;
        this.description = description;
        this.deadLine = deadLine;
        this.quantity = quantity;
        this.photoId = photoId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDeadLine() {
        return deadLine;
    }

    public void setDeadLine(String deadLine) {
        this.deadLine = deadLine;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
    }
}
