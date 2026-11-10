package bg.mobileapps.fn2401321025;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

import bg.mobileapps.fn2401321025.di.AppContainer;

/** Application класът държи единствения DI контейнер на приложението. */
public class TasksApp extends Application {

    private AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        DynamicColors.applyToActivitiesIfAvailable(this);
        container = new AppContainer(this);
    }

    public AppContainer getContainer() {
        return container;
    }
}
