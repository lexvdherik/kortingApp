package hva.flashdiscount.model;

public class Favorite {

    private int establishmentId;
    private int companyId;
    private String street;
    private String postalcode;
    private String streetnumber;
    private String city;
    private String longitude;
    private String latitude;
    private String picture;
    private String phoneNumber;
    private String website;
    private Company company;
    private int notification;

    public Favorite(int establishmentId, int companyId, String street, String postalcode, String streetnumber,
                    String city, String longitude, String latitude, String picture, String phoneNumber,
                    String website, int notification, Company company) {
        this.establishmentId = establishmentId;
        this.companyId = companyId;
        this.street = street;
        this.postalcode = postalcode;
        this.streetnumber = streetnumber;
        this.city = city;
        this.longitude = longitude;
        this.latitude = latitude;
        this.picture = picture;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.notification = notification;
        this.company = company;
    }

    public int getEstablishmentId() {
        return establishmentId;
    }

    public void setEstablishmentId(int establishmentId) {
        this.establishmentId = establishmentId;
    }

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getStreetnumber() {
        return streetnumber;
    }

    public void setStreetnumber(String streetnumber) {
        this.streetnumber = streetnumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public int getNotification() {
        return notification;
    }

    public void setNotification(int notification) {
        this.notification = notification;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
