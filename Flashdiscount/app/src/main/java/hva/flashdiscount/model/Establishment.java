package hva.flashdiscount.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class Establishment {
    private int establishmentId;
    private String street;
    private String postalCode;
    private String streetNumber;
    private String city;
    private String picture;
    private String phoneNumber;
    private String website;
    private double latitude;
    private double longitude;
    private Company company;
    private ArrayList<Discount> discounts;

    public Establishment(int establishmentId, String street, String postalCode, String streetNumber, String city, String picture, String phoneNumber, String website, Company company) {
        this.establishmentId = establishmentId;
        this.street = street;
        this.postalCode = postalCode;
        this.streetNumber = streetNumber;
        this.city = city;
        this.picture = picture;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.company = company;
    }

    public int getEstablishmentId() {
        return establishmentId;
    }

    public String getStreet() {
        return street;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public String getCity() {
        return city;
    }

    public String getPicture() {
        return picture;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public Company getCompany() {
        return company;
    }

    public ArrayList<Discount> getDiscounts() {
        return discounts;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public LatLng getLocation() {
        return new LatLng(latitude, longitude);
    }
}