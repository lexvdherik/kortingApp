package hva.flashdiscount;

import android.util.Log;

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

    public Discount(int discountId, int active, String description, String startTime, String endTime, int amount, int userLimit, String picture, Establishment establishment) {
        this.discountId = discountId;
        this.active = active;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.userLimit = userLimit;
        this.picture = picture;
        this.establishment = establishment;
    }

    public Discount(String description, Establishment establishment,String endTime) {
        this.description = description;
        this.endTime = endTime;
        this.establishment = establishment;
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

    public String getCompanyName(){
        return this.establishment.getCompany().getName();
    }

    public int getCategoryId(){
        return this.establishment.getCompany().getCategoryId();
    }

    public int getCategoryImage()
    {
        int categoryId = this.establishment.getCompany().getCategoryId();

        switch (categoryId)
        {
            case 1:
                return R.drawable.beer;
            case 2:
                return R.drawable.restaurant;
        }
        return R.drawable.ic_account_settings;
    }

    public String getTimeRemaining()
    {

        DateTime d = new DateTime(this.endTime);


        return d.minutesBetween();
    }
}
