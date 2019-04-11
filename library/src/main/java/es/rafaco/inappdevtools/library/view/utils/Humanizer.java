package es.rafaco.inappdevtools.library.view.utils;

import android.text.TextUtils;

public class Humanizer {

    public static String capital(String s){
        return s.substring(0,1).toUpperCase() + s.substring(1);
    }

    public static int countLines(String text) {
        if (TextUtils.isEmpty(text)){
            return 0;
        }
        return text.split(newLine()).length;
    }

    public static String newLine(){
        return System.getProperty("line.separator");
    }
}
