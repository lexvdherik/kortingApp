package hva.flashdiscount.model;

/**
 * Created by Maiko on 22-11-2016.
 */

public class Token {

    private String expireDate;

    public Token(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }
}
