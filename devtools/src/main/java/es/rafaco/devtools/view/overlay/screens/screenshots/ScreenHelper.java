package es.rafaco.devtools.view.overlay.screens.screenshots;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.db.DevToolsDatabase;
import es.rafaco.devtools.db.entities.Crash;
import es.rafaco.devtools.db.entities.Screen;
import es.rafaco.devtools.db.entities.ScreenDao;
import es.rafaco.devtools.tools.ToolHelper;
import es.rafaco.devtools.logic.utils.FileUtils;
import es.rafaco.devtools.logic.utils.ThreadUtils;
import es.rafaco.devtools.view.utils.ViewHierarchyUtils;

public class ScreenHelper extends ToolHelper{

    private static final String SCREENSHOTS_DIR_NAME = "Screenshots";
    private static final String FILE_NAME_TEMPLATE = "DevTools_%s_%s.jpg";
    private static final String SCREENSHOT_SHARE_SUBJECT_TEMPLATE = "Screenshot (%s)";


    @Override
    public String getReportPath() {
        //TODO: ThreadUtils.runOnBackThread(new Runnable() {
        ScreenDao screenDao = DevToolsDatabase.getInstance().screenDao();
        final Screen lastScreen = screenDao.getLast();

        if (lastScreen != null){
            return lastScreen.getPath();
        }else{
            return null;
        }
    }

    @Override
    public String getReportContent() {
        return null;
    }


    public Screen takeAndSaveScreen(){

        final Screen screen = takeScreenIntoFile();
        if (screen != null){
            Toast.makeText(context.getApplicationContext(), "Screen saved", Toast.LENGTH_LONG).show();
            ThreadUtils.runOnBackThread(new Runnable() {
                @Override
                public void run() {
                    DevTools.getDatabase().screenDao().insertAll(screen);
                }
            });
        }
        return screen;
    }

    private Screen takeScreenIntoFile() {

        List<Pair<String, View>> rootViews = ViewHierarchyUtils.getRootViews(false);
        Pair<String, View> selectedRootView = rootViews.get(0);
        String selectedName = selectedRootView.first;
        View selectedView = selectedRootView.second;
        //ViewHierarchyUtils.getWindowName(selectedView);

        String activityName = ViewHierarchyUtils.getActivityNameFromRootView(selectedView);

        try {
            selectedView.setDrawingCacheEnabled(true);
            selectedView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(selectedView.getDrawingCache());
            selectedView.setDrawingCacheEnabled(false);

            //File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), SCREENSHOTS_DIR_NAME);
            if (!folder.exists())
                folder.mkdir();

            long mImageTime = System.currentTimeMillis();
            //String imageDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(mImageTime));
            String fileName = String.format(FILE_NAME_TEMPLATE, "Screen", mImageTime);
            File imageFile = new File(folder, fileName);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 80;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            FileUtils.scanMediaFile(context, imageFile);

            Screen screen = new Screen();
            screen.setSession(0);
            screen.setRootViewName(selectedName);
            screen.setActivityName(activityName);
            screen.setDate(mImageTime);
            screen.setPath(imageFile.getAbsolutePath());

            return screen;

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return null;
    }

    public byte[] buildPendingData() {
        //takeScreenAsByteArray
        List<Pair<String, View>> rootViews = ViewHierarchyUtils.getRootViews(false);
        Pair<String, View> selectedRootView = rootViews.get(0);
        View selectedView = selectedRootView.second;

        try {
            selectedView.setDrawingCacheEnabled(true);
            selectedView.buildDrawingCache();
            Bitmap bitmap = Bitmap.createBitmap(selectedView.getDrawingCache());
            selectedView.setDrawingCacheEnabled(false);

            return getByteArray(bitmap);

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
        return null;
    }

    private byte[] getByteArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
        return stream.toByteArray();
    }

    public String solvePendingData(Crash crash){
        //storeByteArray
        long mImageTime = crash.getDate();
        byte[] imageByteArray = crash.getRawScreen();

        if (imageByteArray == null){
            return "";
        }

        try {
            //File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            File folder = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), SCREENSHOTS_DIR_NAME);
            if (!folder.exists())
                folder.mkdir();


            //String imageDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date(mImageTime));
            String fileName = String.format(FILE_NAME_TEMPLATE, "Crash", mImageTime);
            File imageFile = new File(folder, fileName);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            outputStream.write(imageByteArray);
            outputStream.close();

            FileUtils.scanMediaFile(context, imageFile);
            String filePath = imageFile.getAbsolutePath();

            Screen screen = new Screen();
            screen.setSession(0);
            screen.setDate(crash.getDate());
            screen.setPath(filePath);
            //TODO: interesting for crash
            //screen.setRootViewName(selectedName);
            //screen.setActivityName(activityName);

            long screenId = DevTools.getDatabase().screenDao().insert(screen);
            if (!TextUtils.isEmpty(filePath)){
                crash.setScreenId(screenId);
                crash.setRawScreen(null);
                DevTools.getDatabase().crashDao().update(crash);
            }
            return filePath;

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
            return "";
        }
    }
}
