package org.ctor.dev.llrps2.model;

import java.text.ParseException;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang.time.FastDateFormat;

public final class DateTimeMapper {
    private static final FastDateFormat formatter = FastDateFormat
            .getInstance("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private static final String[] parsePatterns = { "yyyy-MM-dd'T'HH:mm:ss.SSSZ" };

    private DateTimeMapper() {
        // prohibited
    }

    public static GregorianCalendar messageToModel(String dateTime) {
        try {
            final Date date = DateUtils.parseDate(dateTime, parsePatterns);
            final GregorianCalendar calendar = new GregorianCalendar();
            calendar.setTime(date);
            return calendar;
        } catch (ParseException pe) {
            throw new IllegalStateException(pe.getMessage(), pe);
        }
    }

    public static String modelToMessage(GregorianCalendar dateTime) {
        return formatter.format(dateTime);
    }
}
