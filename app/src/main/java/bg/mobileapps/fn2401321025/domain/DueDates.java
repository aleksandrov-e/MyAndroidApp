package bg.mobileapps.fn2401321025.domain;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Помощни функции за крайния срок. Срокът се пази като UTC полунощ
 * (така го връща MaterialDatePicker), затова и сравненията са в UTC.
 */
public final class DueDates {

    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private DueDates() {
    }

    /** UTC полунощ на календарната дата, която е "днес" в подадената часова зона. */
    public static long todayUtcMidnight(long nowMillis, TimeZone zone) {
        Calendar local = Calendar.getInstance(zone);
        local.setTimeInMillis(nowMillis);

        Calendar utc = Calendar.getInstance(UTC);
        utc.clear();
        utc.set(local.get(Calendar.YEAR), local.get(Calendar.MONTH),
                local.get(Calendar.DAY_OF_MONTH));
        return utc.getTimeInMillis();
    }

    /** Срокът е просрочен, ако е преди днешната дата. Задача без срок не е просрочена. */
    public static boolean isOverdue(Long dueDate, long todayUtcMidnight) {
        return dueDate != null && dueDate < todayUtcMidnight;
    }

    public static String format(long dueDate, Locale locale) {
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);
        format.setTimeZone(UTC);
        return format.format(new Date(dueDate));
    }
}
