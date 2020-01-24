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

package es.rafaco.inappdevtools.library.storage.files.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.webkit.MimeTypeMap;

//#ifdef ANDROIDX
//@import androidx.core.content.FileProvider;
//#else
import android.support.v4.content.FileProvider;
//#endif

import java.io.File;

public class FileProviderUtils {

    public static final String ROOT_FOLDER = "iadt";

    public static String getAuthority(Context context) {
        return context.getApplicationContext().getPackageName() + ".iadt.files";
    }

    public static void openFileExternally(Context context, String filePath) {
        openFileExternally(context, filePath, Intent.ACTION_VIEW);
    }

    public static void openFileExternally(Context context, String filePath, String action ) {
        File file = new File(filePath);
        String type = getMimeType(file);

        Intent intent = new Intent();
        intent.setAction(action);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            String authority = getAuthority(context);
            Uri contentUri = FileProvider.getUriForFile(context, authority, file);
            intent.setDataAndType(contentUri, type);
        } else {
            intent.setDataAndType(Uri.fromFile(file), type);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        try{
            context.startActivity(intent);
        }catch (ActivityNotFoundException exception){

        }
    }

    private static String getMimeType(File file) {
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
        return mime.getMimeTypeFromExtension(extension);
    }
}