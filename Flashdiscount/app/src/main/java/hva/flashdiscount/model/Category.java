package hva.flashdiscount.model;

/**
 * Created by chrisvanderheijden on 13/12/2016.
 */

public class Category {

    private int categoryId;
    private String name;
    private String logo;
    private String description;
    private boolean selected;

    public Category() {
    }

    public Category(int categoryId, String name, String logo, String description) {
        this.categoryId = categoryId;
        this.name = name;
        this.logo = logo;
        this.description = description;
    }

    public Category(int categoryId, String name, String logo, String description, boolean selected) {
        this.categoryId = categoryId;
        this.name = name;
        this.logo = logo;
        this.description = description;
        this.selected = selected;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getName() {
        return name;
    }

    public void setCategoryName(String categoryName) {
        this.name = categoryName;
    }

    public String getLogoUrl() {
        return logo;
    }

    public void setLogoUrl(String logoUrl) {
        this.logo = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Category{"
                + "categoryId=" + categoryId
                + ", categoryName='" + name + '\''
                + ", logoUrl='" + logo + '\''
                + ", description='" + description + '\''
                + ", selected=" + selected
                + '}';
    }
}
