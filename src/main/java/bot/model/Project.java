package bot.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "projects", schema = "public")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name")
    @Pattern(regexp = "[а-яА-ЯЁё .-]*")
    private String name;

    @Column(name = "description")
    @Pattern(regexp = "[а-яА-ЯЁё .,-]*")
    private String description;

    @Column(name = "deadline")
    @Pattern(regexp = "[0-9а-яА-ЯЁё .-]*")
    private String deadLine;

    @Column(name = "quantity")
    @Pattern(regexp = "[0-9а-яА-ЯЁё .]*")
    private String quantity;

    @Column(name = "file_name")
    @NotNull
    private String fileName;

    @Column(name = "price")
    @Pattern(regexp = "[0-9а-яА-ЯЁё .]*")
    private String price;

    @Column(name = "category")
    @Pattern(regexp = "[0-9а-яА-ЯЁё .]*")
    private String category;

    public Project() {
    }

    public Integer getId() {
        return id;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Название: " + this.getName() + "\n" +
                "Описание: " + this.getDescription() + "\n" +
                "Цена: " + this.price  +"\n"+
                "В наличии: " + this.getQuantity() + "\n" +
                "Срок изготовления: " + this.getDeadLine() + "\n";
    }
}
