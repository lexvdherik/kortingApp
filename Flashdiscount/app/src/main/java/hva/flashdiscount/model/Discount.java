package hva.flashdiscount.model;

import android.content.Context;

import java.util.Objects;

import hva.flashdiscount.R;

public class Discount {

    private int discountId;
    private int active;
    private String description;
    private String startTime;
    private String endTime;
    private int amount;
    private int amountLimit;
    private int userLimit;
    private String picture;

    public Discount(int discountId, int active, String description, String startTime, String endTime, int amount, int amountLimit, int userLimit, String picture) {
        this.discountId = discountId;
        this.active = active;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.amount = amount;
        this.amountLimit = amountLimit;
        this.userLimit = userLimit;
        this.picture = picture;
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

    public int getAmountLimit() {
        return amountLimit;
    }

    public void setAmountLimit(int amountLimit) {
        this.amountLimit = amountLimit;
    }

    public String getTimeRemaining(Context context) {

        DateTime d = new DateTime(this.endTime);
        return d.minutesBetween(context);
    }

    public int getCategoryImage(int categoryId) {
        switch (categoryId) {
            case 1:
                return R.drawable.ic_coffee;
            case 2:
                return R.drawable.ic_restaurant;
            default:
                return R.drawable.ic_settings;
        }
    }

    @Override
    public String toString() {
        return "Discount{"
                + "discountId=" + discountId
                + ", active=" + active
                + ", description='" + description + '\''
                + ", startTime='" + startTime + '\''
                + ", endTime='" + endTime + '\''
                + ", amount=" + amount
                + ", userLimit=" + userLimit
                + ", picture='" + picture + '\''
                + '}';
    }

    public String getAmountRemaining() {
        return String.valueOf((amountLimit - amount) > 0 ? (amountLimit - amount) : 0);
    }

    public boolean isValid(Context context) {
        return !(Objects.equals(getAmountRemaining(), "0") || Objects.equals(getTimeRemaining(context), context.getResources().getString(R.string.expired_time)));
    }
}
