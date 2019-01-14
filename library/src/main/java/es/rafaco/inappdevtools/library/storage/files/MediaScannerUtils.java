package es.rafaco.inappdevtools.library.storage.files;

import android.media.MediaScannerConnection;
import android.util.Log;
import java.io.File;

import es.rafaco.inappdevtools.library.DevTools;

public class MediaScannerUtils {

    public static void scan(File file) {
        String[] paths =  new String[]{file.toString()};
        scan(paths);
    }

    public static void scan(String[] paths) {
        // Tell the media scanner about the new file so that it is immediately available to the user.
        MediaScannerConnection.scanFile(
                DevTools.getAppContext(),
                paths,
                null,
                (path, uri) -> Log.i("ExternalStorage", "Scanned " + path + " -> uri=" + uri));
    }
}
