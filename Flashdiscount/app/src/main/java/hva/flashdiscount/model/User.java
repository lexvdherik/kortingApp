package hva.flashdiscount.model;

public class User {
    private int userId;
    private String role;
    private String name;
    private String password;
    private String emailAddress;
    private String age;
    private String picture;
    private String googleId;

    public User(int userId, String role, String name, String password, String emailAddress, String age, String picture, String googleId) {
        this.userId = userId;
        this.role = role;
        this.name = name;
        this.password = password;
        this.emailAddress = emailAddress;
        this.age = age;
        this.picture = picture;
        this.googleId = googleId;
    }

    public User(String googleId, String name, String emailAddress) {
        this.googleId = googleId;
        this.name = name;
        this.emailAddress = emailAddress;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
