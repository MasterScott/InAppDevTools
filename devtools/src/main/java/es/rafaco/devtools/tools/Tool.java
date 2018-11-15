package es.rafaco.devtools.tools;

import java.util.List;

import es.rafaco.devtools.view.components.DecoratedToolInfo;
import es.rafaco.devtools.view.components.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;

public abstract class Tool {

    public Tool() {
        onRegister();
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    protected abstract void onRegister();

    public abstract Class<? extends ToolHelper> getHelperClass();

    public abstract Class<? extends OverlayScreen> getMainScreen();
    public List<Class<? extends OverlayScreen>> getOtherScreens() {
        return null;
    }

    public abstract DecoratedToolInfo getHomeInfo();
    public abstract DecoratedToolInfo getReportInfo();

    public void updateHomeInfo(DecoratedToolInfoAdapter adapter) { }
}
