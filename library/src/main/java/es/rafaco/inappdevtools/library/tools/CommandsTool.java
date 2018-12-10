package es.rafaco.inappdevtools.library.tools;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.DecoratedToolInfo;
import es.rafaco.inappdevtools.library.view.overlay.layers.NavigationStep;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.commands.CommandsScreen;

public class CommandsTool extends Tool {
    @Override
    protected void onRegister() {

    }

    @Override
    public Class<? extends ToolHelper> getHelperClass() {
        return null;
    }

    @Override
    public Class<? extends OverlayScreen> getMainScreen() {
        return CommandsScreen.class;
    }

    @Override
    public DecoratedToolInfo getHomeInfo() {
        NavigationStep step = new NavigationStep(CommandsScreen.class, null);
        DecoratedToolInfo info = new DecoratedToolInfo(
                "Run Commands",
                "Type shell commands, run a predefined ones or configure your owns.",
                R.color.rally_white,
                4,
                step);
        return  info;
    }

    @Override
    public DecoratedToolInfo getReportInfo() {
        return null;
    }
}