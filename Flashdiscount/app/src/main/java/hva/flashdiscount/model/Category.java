package hva.flashdiscount.model;

/**
 * Created by chrisvanderheijden on 13/12/2016.
 */

public class Category {

    private int categoryId;
    private String categoryName;
    private String logoUrl;
    private String description;
    private boolean selected;

    public Category() {
    }

    public Category(int categoryId, String categoryName, String logoUrl, String description) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.logoUrl = logoUrl;
        this.description = description;
    }

    public Category(int categoryId, String categoryName, String logoUrl, String description, boolean selected) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.logoUrl = logoUrl;
        this.description = description;
        this.selected = selected;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
