package es.rafaco.inappdevtools.library.tools;

import android.content.Context;

import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;

public abstract class ToolHelper {

    public final Context context;

    public ToolHelper() {
        this.context = DevTools.getAppContext();
    }

    public abstract String getReportPath();
    public List<String> getReportPaths() { return null; }
    public abstract String getReportContent();

}