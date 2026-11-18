package bg.mobileapps.fn2401321025;

import android.app.Application;

import bg.mobileapps.fn2401321025.di.AppContainer;
import bg.mobileapps.fn2401321025.ui.ThemeSettings;

/** Application класът държи единствения DI контейнер на приложението. */
public class TasksApp extends Application {

    private AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        ThemeSettings.apply(this);
        container = new AppContainer(this);
    }

    public AppContainer getContainer() {
        return container;
    }
}
