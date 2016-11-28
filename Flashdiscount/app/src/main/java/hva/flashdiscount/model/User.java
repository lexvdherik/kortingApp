package hva.flashdiscount.model;

import android.net.Uri;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class User {
    private String name;
    private String email;
    private String age;
    private Uri picture;
    private String googleId;

    public User(String name, String email, String age, Uri picture, String googleId) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.picture = picture;
        this.googleId = googleId;
    }

    public User(String googleId, String name, String email) {
        this.googleId = googleId;
        this.name = name;
        this.email = email;
    }

    public User(GoogleSignInAccount acct) {
        String firstName = acct.getGivenName().substring(0, 1).toUpperCase() + acct.getGivenName().substring(1);
        String lastName = acct.getFamilyName().substring(0, 1).toUpperCase() + acct.getFamilyName().substring(1);

        this.name = firstName + " " + lastName;
        this.email = acct.getEmail();
        this.picture = acct.getPhotoUrl();
        this.googleId = acct.getIdToken();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Uri getPicture() {
        return picture;
    }

    public void setPicture(Uri picture) {
        this.picture = picture;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }
}
