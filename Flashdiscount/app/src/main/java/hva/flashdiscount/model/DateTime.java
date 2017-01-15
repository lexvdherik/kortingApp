package hva.flashdiscount.model;

import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import hva.flashdiscount.R;

class DateTime {
    private static String TAG = DateTime.class.getSimpleName();
    private Calendar targetTime;

    DateTime(String endTimeString) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.targetTime = Calendar.getInstance();

        Date endDate;

        try {
            endDate = df.parse(endTimeString);

            this.targetTime.setTime(endDate);

        } catch (ParseException e) {
            Log.e(TAG, "DateTime ParseError:" + e.toString());
        }
    }

    public int getTargetTime() {
        return targetTime.get(Calendar.DATE);
    }

    public int getCurrentTime() {
        return Calendar.getInstance().get(Calendar.DATE);
    }

    String minutesBetween(Context context) {
        Long end = Calendar.getInstance().getTimeInMillis();
        Long start = targetTime.getTimeInMillis();
        Long minutesBetween = (start - end) / 60000;
        Long hours, minutes;

        if (minutesBetween < 1) {
            return context.getString(R.string.expired_time);
        }

        if (minutesBetween > 1440) {
            return "24+ " + context.getString(R.string.hour_plural);
        }

        if (minutesBetween >= 60) {
            hours = minutesBetween / 60;
            minutes = minutesBetween % 60;
            return hours.toString()
                    + " " + ((hours > 1) ? context.getString(R.string.hour_plural) : context.getString(R.string.hour))
                    + " " + minutes.toString()
                    + " " + ((minutes > 1) ? context.getString(R.string.minutes_plural) : context.getString(R.string.minutes));
        }

        return minutesBetween.toString()
                + " " + ((minutesBetween > 1) ? context.getString(R.string.minutes_plural) : context.getString(R.string.minutes));

    }

}
