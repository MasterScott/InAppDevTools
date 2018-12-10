package es.rafaco.inappdevtools.library.storage.files;

import android.os.Environment;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import es.rafaco.inappdevtools.library.storage.db.entities.Crash;

public class DevToolsFiles {

    public static String storeCrashDetail(Crash crash, String report) {
        return FileCreator.withContent(
                "crash",
                "crash_detail_" + crash.getDate() + ".txt",
                report);
    }

    public static String storeCrashLog(Crash crash) {
        return FileCreator.withContent(
                "crash",
                "crash_logcat_" + crash.getDate() + ".txt",
                crash.getRawLogcat());

        /*TODO: validate rawLog.getBytes() is not needed
        FileOutputStream outputStream = new FileOutputStream(file);
        outputStream.write(rawLog.getBytes());
        outputStream.close();*/
    }

    public static String storeInfo(String report, long timeMillis) {
        return FileCreator.withContent(
                "info",
                "system_info_" + timeMillis + ".txt",
                report);
    }

    public static File prepareSource(String target, String extension) {
        return FileCreator.prepare(
                "sources",
                target + "." + extension);
    }

    public static File prepareLogcat(long timeMillis) {
        return FileCreator.prepare(
                "logcat",
                "logcat_" + timeMillis + ".txt");
    }

    public static File prepareDatabase(String dbName, long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmm");
        String formattedTime = sdf.format(new Date(time));
        return FileCreator.prepare(
                "db",
                "db_" + dbName + "_" + formattedTime + ".csv");
    }

    public static File prepareScreen(long mImageTime, boolean fromCrash) {

        File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "DevTools");
        if (!folder.exists())
            folder.mkdir();

        String first = fromCrash ? "crash" : "DevTools";
        String second = "screen" ;
        String fileName = String.format(first + "_" + second + "_" + mImageTime + "_" + ".jpg");
        return new File(folder, fileName);
    }
}