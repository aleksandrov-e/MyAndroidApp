package bg.mobileapps.fn2401321025;

import android.app.Application;

import bg.mobileapps.fn2401321025.di.AppContainer;

/** Application класът държи единствения DI контейнер на приложението. */
public class TasksApp extends Application {

    private AppContainer container;

    @Override
    public void onCreate() {
        super.onCreate();
        container = new AppContainer(this);
    }

    public AppContainer getContainer() {
        return container;
    }
}
