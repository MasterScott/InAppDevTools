package es.rafaco.devtools.tools;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.components.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.log.LogHelper;
import es.rafaco.devtools.view.overlay.screens.log.LogScreen;

public class LogTool extends Tool {

    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return LogHelper.class;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return LogScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        NavigationStep step = new NavigationStep(LogScreen.class, null);
        return new DecoratedToolInfo(
                "Logcat",
                "Live log is available.",
                R.color.rally_yellow,
                2,
                step);
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        NavigationStep step = new NavigationStep(LogScreen.class, null);
        return new DecoratedToolInfo(
                getName(),
                "Include full log.",
                R.color.rally_yellow,
                2,
                step);
    }
}