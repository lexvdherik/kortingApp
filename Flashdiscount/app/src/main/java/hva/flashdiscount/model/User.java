package hva.flashdiscount.model;

/**
 * Created by Maiko on 27-10-2016.
 */

public class User {
        private int id;
        private int googleId;
        private String name;
        private String email;

    public User(int googleId, String email, String name, int id) {
        this.googleId = googleId;
        this.email = email;
        this.name = name;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGoogleId() {
        return googleId;
    }

    public void setGoogleId(int googleId) {
        this.googleId = googleId;
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
}
