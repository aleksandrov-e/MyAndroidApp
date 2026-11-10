package bg.mobileapps.fn2401321025.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DueDatesTest {

    private static final long DAY = 24L * 60 * 60 * 1000;

    @Test
    public void todayUtcMidnight_usesLocalCalendarDate() {
        // 23:30 UTC на 10 ноември е вече 11 ноември в София (UTC+2).
        long now = utc(2026, Calendar.NOVEMBER, 10, 23, 30);

        long today = DueDates.todayUtcMidnight(now, TimeZone.getTimeZone("Europe/Sofia"));

        assertEquals(utc(2026, Calendar.NOVEMBER, 11, 0, 0), today);
    }

    @Test
    public void todayUtcMidnight_inUtc_dropsTimeOfDay() {
        long now = utc(2026, Calendar.NOVEMBER, 10, 15, 45);

        long today = DueDates.todayUtcMidnight(now, TimeZone.getTimeZone("UTC"));

        assertEquals(utc(2026, Calendar.NOVEMBER, 10, 0, 0), today);
    }

    @Test
    public void noDueDate_isNeverOverdue() {
        assertFalse(DueDates.isOverdue(null, 0));
    }

    @Test
    public void dueToday_isNotOverdue() {
        long today = utc(2026, Calendar.NOVEMBER, 10, 0, 0);
        assertFalse(DueDates.isOverdue(today, today));
    }

    @Test
    public void dueYesterday_isOverdue() {
        long today = utc(2026, Calendar.NOVEMBER, 10, 0, 0);
        assertTrue(DueDates.isOverdue(today - DAY, today));
    }

    @Test
    public void format_printsUtcDate() {
        String text = DueDates.format(utc(2026, Calendar.NOVEMBER, 10, 0, 0), Locale.US);
        assertEquals("Nov 10, 2026", text);
    }

    private static long utc(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(year, month, day, hour, minute);
        return calendar.getTimeInMillis();
    }
}
