package bg.mobileapps.fn2401321025.ui;

import static org.junit.Assert.assertEquals;

import androidx.appcompat.app.AppCompatDelegate;

import org.junit.Test;

public class ThemeSettingsTest {

    @Test
    public void fromDark_goesBackToLight() {
        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, ThemeSettings.nextMode(true));
    }

    @Test
    public void fromLight_goesToDark() {
        assertEquals(AppCompatDelegate.MODE_NIGHT_YES, ThemeSettings.nextMode(false));
    }

    @Test
    public void togglingTwice_returnsToStart() {
        int dark = ThemeSettings.nextMode(false);
        int backToLight = ThemeSettings.nextMode(dark == AppCompatDelegate.MODE_NIGHT_YES);

        assertEquals(AppCompatDelegate.MODE_NIGHT_NO, backToLight);
    }
}
