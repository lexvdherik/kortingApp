package hva.flashdiscount.model;

public class Company {
    private int companyId;
    private String name;
    private String description;
    private String shortDescription;
    private String logo;
    private int categoryId;

    public Company(int companyId, String name, String description, String shortDescription, String logo, int categoryId) {
        this.companyId = companyId;
        this.name = name;
        this.description = description;
        this.shortDescription = shortDescription;
        this.logo = logo;
        this.categoryId = categoryId;
    }

    public Company(int categoryId, String name) {
        this.categoryId = categoryId;
        this.name = name;
    }

    public int getCompanyId() {
        return companyId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getLogo() {
        return logo;
    }

    public int getCategoryId() {
        return categoryId;
    }
}