package bg.mobileapps.fn2401321025.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import androidx.appcompat.app.AppCompatDelegate;

/** Избраната от потребителя тема (светла/тъмна), запазена между стартиранията. */
public final class ThemeSettings {

    private static final String PREFS_NAME = "settings";
    private static final String KEY_NIGHT_MODE = "night_mode";

    private ThemeSettings() {
    }

    /** Вика се при старта на приложението. Преди първия избор следва системната тема. */
    public static void apply(Context context) {
        AppCompatDelegate.setDefaultNightMode(prefs(context).getInt(
                KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM));
    }

    /** Превключва между светла и тъмна; AppCompat сам пресъздава екрана. */
    public static void toggle(Context context) {
        int next = nextMode(isNight(context));
        prefs(context).edit().putInt(KEY_NIGHT_MODE, next).apply();
        AppCompatDelegate.setDefaultNightMode(next);
    }

    /** Дали в момента реално се показва тъмна тема (включително ако идва от системата). */
    public static boolean isNight(Context context) {
        int mask = context.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return mask == Configuration.UI_MODE_NIGHT_YES;
    }

    /** От тъмна към светла и обратно. */
    public static int nextMode(boolean currentlyNight) {
        return currentlyNight
                ? AppCompatDelegate.MODE_NIGHT_NO
                : AppCompatDelegate.MODE_NIGHT_YES;
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
