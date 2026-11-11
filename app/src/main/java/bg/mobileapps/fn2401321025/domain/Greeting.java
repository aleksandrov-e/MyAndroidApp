package bg.mobileapps.fn2401321025.domain;

/** Поздравът в началото на списъка според часа от денонощието. */
public enum Greeting {
    MORNING,
    DAY,
    EVENING;

    public static Greeting forHour(int hourOfDay) {
        if (hourOfDay >= 5 && hourOfDay < 12) {
            return MORNING;
        }
        if (hourOfDay >= 12 && hourOfDay < 18) {
            return DAY;
        }
        return EVENING;
    }
}
