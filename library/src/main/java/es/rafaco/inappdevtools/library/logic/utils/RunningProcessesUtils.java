/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.logic.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class RunningProcessesUtils {

    private RunningProcessesUtils() { throw new IllegalStateException("Utility class"); }

    private static Context getContext(){
        return IadtController.get().getContext();
    }

    public static int getCount() {
        if (getList() == null){
            return 0;
        }
        return getList().size();
    }

    public static String getString() {
        StringBuilder result = new StringBuilder("\n");
        List<ActivityManager.RunningAppProcessInfo> processes = getList();
        if (processes == null){
            return "";
        }
        
        for (ActivityManager.RunningAppProcessInfo info : processes) {

            String importance = info.importance + " ";
            if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                importance += "Foreground";
            }else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
                importance += "Service";
            }
            else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                importance += "Cached";
            }
            else if(info.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_GONE) {
                importance += "Gone";
            }
            result
                    .append(info.pid)
                    .append(" - ")
                    .append(info.processName)
                    .append("\n")
                    .append("  importance: ")
                    .append(importance)
                    //.append(" uid: ")
                    //.append(info.uid)
                    .append("\n");
            for (String pkg : info.pkgList) {
                result.append("  pkg: ").append(pkg).append("\n");
            }

            result.append(getMemoryInfoFormatted(info));

            //result.append(getRunningServices(info.pid)).append("\n");
            //result.append(getRunningProviders(info.processName, info.uid)).append("\n");
        }
        return result.toString();
    }


    private static List<ActivityManager.RunningAppProcessInfo> getList() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        return manager.getRunningAppProcesses();
    }

    private static String getMemoryInfoFormatted(ActivityManager.RunningAppProcessInfo processInfo) {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
        Debug.MemoryInfo[] processMemoryInfo;
        try {
            processMemoryInfo = manager.getProcessMemoryInfo(new int[]{processInfo.pid});
            if (processMemoryInfo!=null && processMemoryInfo.length>0){
                return getMemoryInfoFormatted(processMemoryInfo[0]);
            }
        }
        catch (Exception e) {
            //TODO: research why is always null
            return "No Info " + e.getMessage();
        }
        return "No Info ";
    }

    private static String getMemoryInfoFormatted(Debug.MemoryInfo debugMemoryInfo) {
        String result;
        result = String.format("  Dalvik: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.dalvikPss),
                Humanizer.parseKb(debugMemoryInfo.dalvikSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.dalvikPrivateDirty));
        result += Humanizer.newLine();
        result += String.format("  Native: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.nativePss),
                Humanizer.parseKb(debugMemoryInfo.nativeSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.nativePrivateDirty));
        result += Humanizer.newLine();
        result += String.format("  Other: %s pss, %s shared, %s private",
                Humanizer.parseKb(debugMemoryInfo.otherPss),
                Humanizer.parseKb(debugMemoryInfo.otherSharedDirty),
                Humanizer.parseKb(debugMemoryInfo.otherPrivateDirty));
        result += Humanizer.newLine();

        return result;
    }
}
