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

package es.rafaco.inappdevtools.library.storage.db;

import android.content.Context;
import android.util.Log;

//#ifdef ANDROIDX
//@import androidx.room.Room;
//@import androidx.room.Database;
//@import androidx.room.RoomDatabase;
//#else
import android.arch.persistence.room.Room;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
//#endif

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.storage.db.entities.Anr;
import es.rafaco.inappdevtools.library.storage.db.entities.AnrDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Build;
import es.rafaco.inappdevtools.library.storage.db.entities.BuildDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.CrashDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.storage.db.entities.NetContent;
import es.rafaco.inappdevtools.library.storage.db.entities.NetContentDao;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummary;
import es.rafaco.inappdevtools.library.storage.db.entities.NetSummaryDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Report;
import es.rafaco.inappdevtools.library.storage.db.entities.ReportDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.db.entities.ScreenshotDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;
import es.rafaco.inappdevtools.library.storage.db.entities.SessionDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.storage.db.entities.SourcetraceDao;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

@Database(version = 29, exportSchema = false,
        entities = {
                Build.class,
                Session.class,
                Friendly.class,
                Screenshot.class,
                Crash.class,
                Anr.class,
                Sourcetrace.class,
                Report.class,
                NetSummary.class,
                NetContent.class,
        })
public abstract class DevToolsDatabase extends RoomDatabase {

    public static final String DB_NAME = "inappdevtools.db";
    private static DevToolsDatabase INSTANCE;

    public static DevToolsDatabase getInstance() {
        if (INSTANCE == null) {
            Context context = IadtController.get().getContext();
            INSTANCE =
                    Room.databaseBuilder(context, DevToolsDatabase.class, DB_NAME)
                            //TODO: Research alternatives, on crash we can't create new threads
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }


    //region [ DAOs ]
    public abstract SessionDao sessionDao();
    public abstract BuildDao buildDao();
    public abstract FriendlyDao friendlyDao();
    public abstract ScreenshotDao screenshotDao();
    public abstract CrashDao crashDao();
    public abstract AnrDao anrDao();
    public abstract SourcetraceDao sourcetraceDao();
    public abstract ReportDao reportDao();
    public abstract NetSummaryDao netSummaryDao();
    public abstract NetContentDao netContentDao();
    //endregion

    public void printOverview(){
        Log.d(Iadt.TAG, getOverview());
    }

    public String getOverview(){
        String overview = "";
        overview +="IadtDatabase overview: " + Humanizer.newLine();
        overview +="  Builds: " + buildDao().count() + Humanizer.newLine();
        overview +="  Sessions: " + sessionDao().count() + Humanizer.newLine();
        overview +="  FriendlyLogs: " + friendlyDao().count() + Humanizer.newLine();
        overview +="  Screenshots: " + screenshotDao().count() + Humanizer.newLine();
        overview +="  Crashs: " + crashDao().count() + Humanizer.newLine();
        overview +="  Anrs: " + anrDao().count() + Humanizer.newLine();
        overview +="  SourceTraces: " + sourcetraceDao().count() + Humanizer.newLine();
        overview +="  Reports: " + reportDao().count() + Humanizer.newLine();
        overview +="  NetSummary: " + netSummaryDao().count() + Humanizer.newLine();
        overview +="  NetContent: " + netContentDao().count();
        return overview;
    }
}
