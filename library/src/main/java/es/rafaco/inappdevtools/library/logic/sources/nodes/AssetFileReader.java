package es.rafaco.inappdevtools.library.logic.sources.nodes;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipFile;

import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

public class AssetFileReader {

    private final Context context;

    public AssetFileReader(Context context) {
        this.context = context;
    }

    public ZipFile getFile(String target) {
        ZipFile zip = null;
        try {
            zip = new ZipFile(getLocalFile(target));
        } catch (IOException e) {
            FriendlyLog.logException("Exception", e);
        }
        return zip;
    }

    public File getLocalFile(String target ){
        if (target.startsWith("assets/")){
            target = target.substring("assets/".length()-1);
        }
        File f = new File(context.getCacheDir()+"/"+ target);
        if (!f.exists()){
            FileOutputStream fos = null;
            try {
                f.getParentFile().mkdirs();
                InputStream is = context.getAssets().open(target);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();

                fos = new FileOutputStream(f);
                fos.write(buffer);
            } catch (Exception e) {
                FriendlyLog.logException("SourceReader exception", e);
            }finally {
                if (fos!=null){
                    try {
                        fos.close();
                    } catch (IOException e) {
                        FriendlyLog.logException("Exception", e);
                    }
                }
            }
        }

        return f;
    }
}
