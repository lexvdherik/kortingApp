package hva.flashdiscount.model;

import hva.flashdiscount.R;

/**
 * Created by Maiko on 6-10-2016.
 */

public class Discount {

    private int discountId;
    private int active;
    private String description;
    private String startTime;
    private String endTime;
    private int amount;
    private int userLimit;
    private String picture;
    private Establishment establishment;
    private Company company;

    public Discount(int discountId, int active, String description, String startTime, String endTime, int amount, int userLimit, String picture, Company company) {
        this.discountId = discountId;
        this.active = active;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.userLimit = userLimit;
        this.picture = picture;
        this.company = company;
    }

    public Discount(String description, Establishment establishment, String endTime) {
        this.description = description;
        this.endTime = endTime;
        this.establishment = establishment;
    }

    public Discount(String description, String endTime, Company company) {
        this.description = description;
        this.endTime = endTime;
        this.company = company;
    }

    public Company getCompany() {
        return company;
    }

    public int getDiscountId() {
        return discountId;
    }

    public int getActive() {
        return active;
    }

    public String getDescription() {
        return description;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public int getAmount() {
        return amount;
    }

    public int getUserLimit() {
        return userLimit;
    }

    public String getPicture() {
        return picture;
    }

    public String getCompanyName() {
        return this.establishment.getCompany().getName();
    }

    public int getCategoryId() {
        return this.establishment.getCompany().getCategoryId();
    }

    public String getTimeRemaining() {

        DateTime d = new DateTime(this.endTime);


        return d.minutesBetween();
    }

    public int getCategoryImage() {
        int categoryId = this.company.getCategoryId();

        switch (categoryId) {
            case 1:
                return R.drawable.ic_breakfast;
            case 2:
                return R.drawable.ic_restaurant;
            default:
                return R.drawable.ic_account_settings;
        }
    }
}
