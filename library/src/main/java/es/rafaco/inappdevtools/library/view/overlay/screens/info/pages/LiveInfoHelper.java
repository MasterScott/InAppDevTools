package es.rafaco.inappdevtools.library.view.overlay.screens.info.pages;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import es.rafaco.inappdevtools.library.logic.utils.RunningProcessesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningProvidersUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningServicesUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningTasksUtils;
import es.rafaco.inappdevtools.library.logic.utils.RunningThreadsUtils;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoGroup;
import es.rafaco.inappdevtools.library.view.overlay.screens.info.entries.InfoReport;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class LiveInfoHelper extends AbstractInfoHelper {

    public LiveInfoHelper(Context context) {
        super(context);
    }

    @Override
    public String getOverview() {
        String result = RunningTasksUtils.getTopActivity() + " on " + RunningTasksUtils.getTopActivityStatus() + Humanizer.newLine()
                + RunningTasksUtils.getCount() + " tasks with " + RunningTasksUtils.getActivitiesCount() + " activities" + Humanizer.newLine()
                + RunningServicesUtils.getCount() + " services and " + RunningProvidersUtils.getCount() + " providers" + Humanizer.newLine()
                + RunningProcessesUtils.getCount() + " processes with " + RunningThreadsUtils.getCount() + " threads";
        return result;
    }

    @Override
    public InfoReport getInfoReport() {
        return new InfoReport.Builder("")
                .add(getActivityInfo())
                .add()
                .add(getRunningInfo())
                .build();
    }

    public InfoGroup getActivityInfo() {
        return new InfoGroup.Builder("")
                .add("App on " + RunningTasksUtils.getTopActivityStatus())
                .add("Top activity is " + RunningTasksUtils.getTopActivity())
                .build();
    }

    public InfoGroup getRunningInfo() {
        return new InfoGroup.Builder("")
                .add("Tasks", RunningTasksUtils.getString())
                .add("Services", RunningServicesUtils.getString())
                .add("Providers", RunningProvidersUtils.getString())
                .add("Memory", getRunningMemory())
                .add("Processes", RunningProcessesUtils.getString())
                .add("Threads", RunningThreadsUtils.getString())
                .build();
    }

    private String getRunningMemory() {
        String output = "\n";
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        //output += "--> ActivityManager.memoryClass: max ram allowed per app" + "\n";
        int memoryClass = manager.getMemoryClass();
        output += String.format("  %s Mb allowed per app",
                memoryClass) + "\n";

        //output += "--> ActivityManager.memoryInfo: Total device memory" + "\n";
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(memoryInfo);
        output += String.format("  Device: %s / %s total (%s threshold)%s",
                Humanizer.parseByte(memoryInfo.availMem),
                Humanizer.parseByte(memoryInfo.totalMem),
                Humanizer.parseByte(memoryInfo.threshold),
                memoryInfo.lowMemory? " LOW!" : ""
        ) + "\n";

        //output += "--> Runtime data: dalvik process" + "\n";
        Runtime runtime = Runtime.getRuntime();
        int processors = runtime.availableProcessors();
        String totalMemory = Humanizer.humanReadableByteCount(runtime.totalMemory(), true);
        String freeMemory = Humanizer.humanReadableByteCount(runtime.freeMemory(), true);
        output += String.format("  Runtime: %s / %s (%s processors) ",
                freeMemory,
                totalMemory,
                processors) + "\n";

        //output += "--> Debug data: system wide" + "\n";
        String nativeHeapSize = Humanizer.humanReadableByteCount(Debug.getNativeHeapSize(), true);
        //String nativeHeapAllocatedSize = OSInfoHelper.humanReadableByteCount(Debug.getNativeHeapAllocatedSize(), true);
        String nativeHeapFreeSize = Humanizer.humanReadableByteCount(Debug.getNativeHeapFreeSize(), true);
        output += String.format("  NativeHeap: %s / %s", nativeHeapFreeSize, nativeHeapSize) + "\n";


        /*
        //output += " - Debug.MemoryInfo" + "\n";
        Debug.MemoryInfo debugMemoryInfo = new Debug.MemoryInfo();
        Debug.getMemoryInfo(debugMemoryInfo);
        output += getMemoryInfoFormatted(debugMemoryInfo);
        */

        return output;
    }

}
