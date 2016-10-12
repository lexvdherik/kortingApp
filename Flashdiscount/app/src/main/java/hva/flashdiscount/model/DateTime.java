package hva.flashdiscount.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Maiko on 6-10-2016.
 */

public class DateTime {
    private Calendar endTime;
    private Calendar currentTime;

    public DateTime(String endTimeString) {


        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.currentTime = Calendar.getInstance();
        this.endTime = Calendar.getInstance();

        Date endDate;

        try {
            endDate = df.parse(endTimeString);

            this.endTime.setTime(endDate);

        } catch (ParseException e) {

        }

    }

    public int getEndTime() {
        return endTime.get(Calendar.DATE);
    }

    public int getCurrentTime() {
        return currentTime.get(Calendar.DATE);
    }

    public String minutesBetween() {
        Long end = this.currentTime.getTimeInMillis();
        Long start = this.endTime.getTimeInMillis();
        Long minutesBetween = (start - end) / 60000;
        Long hours;
        Long minutes;
        String mins = "";
        if (minutesBetween >= 60) {
            hours = minutesBetween / 60;
            minutes = minutesBetween % 60;
            mins = hours.toString() + " uur " + minutes.toString() + " min";
        } else {
            mins = minutesBetween.toString() + " min";
        }
        return mins;
    }

}
