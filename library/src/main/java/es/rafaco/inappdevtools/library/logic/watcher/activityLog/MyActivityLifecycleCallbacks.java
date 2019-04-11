package es.rafaco.inappdevtools.library.logic.watcher.activityLog;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.logic.watcher.ShakeWatcher;
import es.rafaco.inappdevtools.library.logic.steps.FriendlyLog;

import static android.content.Context.SENSOR_SERVICE;

public class MyActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {

    private final ActivityLogManager manager;
    final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private SupportFragmentLifecycleCallbacks supportFragmentCallbacks;

    //private FragmentLifecycleCallbacks fragmentCallbacks;


    public MyActivityLifecycleCallbacks(ActivityLogManager manager) {
        this.manager = manager;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        friendlyLog("D","Create", activity);

        //TODO: Crash Handler
        if (false){ //activity.getClass() != config.getErrorActivityClass()) {
            // Copied from ACRA:
            // Ignore activityClass because we want the last
            // application Activity that was started so that we can
            // explicitly kill it off.
            manager.lastActivityCreated = new WeakReference<>(activity);
        }
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " created\n");
        registerFragmentLifecycleCallbacks(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        friendlyLog("D","Start", activity);

        manager.currentlyStartedActivities++;
        manager.isInBackground = (manager.currentlyStartedActivities == 0);
        //Do nothing
    }

    @Override
    public void onActivityResumed(final Activity activity) {
        friendlyLog("V","Resume", activity);

        if (!manager.getLastActivityResumed().equals(activity.getClass().getSimpleName())){
            FriendlyLog.log("I", "App", "Navigation", "Showing " + activity.getClass().getSimpleName());
        }
        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " resumed\n");
        manager.setLastActivityResumed(activity.getClass().getSimpleName());

        updateBackgroundStateOnActivityResumed();

        addRotationListener();
        addShakeListener();
        addTouchListener(activity);
    }


    private String getResourceName(View v, Activity activity) {
        try{
            return activity.getResources().getResourceName(v.getId());
        }catch (Resources.NotFoundException e){
            return "[ not set ]";
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        friendlyLog("V","Pause", activity);

        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " paused\n");

        updateBackgroundStateOnActivityPaused();
        removeShakeListener();
        removeRotationListener();
        removeTouchListener(activity);
    }

    @Override
    public void onActivityStopped(Activity activity) {
        friendlyLog("D","Stop", activity);

        //Do nothing
        manager.currentlyStartedActivities--;
        manager.isInBackground = (manager.currentlyStartedActivities == 0);
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        friendlyLog("V","SaveInstanceState", activity);
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        friendlyLog("D","Destroy", activity);

        manager.activityLog.add(dateFormat.format(new Date()) + ": " + activity.getClass().getSimpleName() + " destroyed\n");
        unregisterFragmentLifecycleCallbacks(activity);
    }


    //region [ TOUCH LISTENER ]

    private void addTouchListener(final Activity activity) {
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(DevTools.TAG, "Resumed activity without decorView");
            return;
        }
        decorView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(DevTools.TAG, "Click X:" + event.getX() + " Y:" + event.getY() + " at " + activity.getClass().getSimpleName() +
                        " - " + v.getClass().getSimpleName() + ": " + getResourceName(v, activity));
                return false;
            }
        });
    }

    private void removeTouchListener(Activity activity) {
        FrameLayout decorView = (FrameLayout) activity.getWindow().getDecorView();
        if (decorView == null){
            Log.d(DevTools.TAG, "Paused activity without decorView");
            return;
        }
        decorView.setOnTouchListener(null);
    }

    //endregion

    //region [ SHAKE LISTENER ]

    private void addShakeListener() {
        ShakeWatcher watcher = DevTools.getWatcherManager().getShakeWatcher();
        if (watcher!=null) watcher.start();
    }

    private void removeShakeListener() {
        ShakeWatcher watcher = DevTools.getWatcherManager().getShakeWatcher();
        if (watcher!=null) watcher.stop();
    }

    //endregion

    //region [ ROTATION LISTENER ]

    private int currentOrientation = -1;
    private SensorEventListener  m_sensorEventListener =
            new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {
                    int detectedOrientation = DevTools.getAppContext().getResources().getConfiguration().orientation;
                    if (detectedOrientation == Configuration.ORIENTATION_PORTRAIT && currentOrientation != detectedOrientation) {
                        currentOrientation = detectedOrientation;
                        FriendlyLog.log("I", "Device", "Portrait", "Orientation changed to portrait");
                    }
                    else if (detectedOrientation == Configuration.ORIENTATION_LANDSCAPE && currentOrientation != detectedOrientation) {
                        currentOrientation = detectedOrientation;
                        FriendlyLog.log("I", "Device", "Landscape", "Orientation changed to landscape");
                    }
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };

    private void addRotationListener() {
        SensorManager sm = (SensorManager) DevTools.getAppContext().getSystemService(SENSOR_SERVICE);
        sm.registerListener(m_sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void removeRotationListener() {
        SensorManager sm = (SensorManager) DevTools.getAppContext().getSystemService(SENSOR_SERVICE);
        sm.unregisterListener(m_sensorEventListener, sm.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR));
    }

    //endregion

    //region [ BACKGROUND STATE ]

    private boolean mInBackground = true;
    private static final long BACKGROUND_DELAY = 500;
    private final List<BackgroundStateListener> listeners = new ArrayList<>();
    private final Handler mBackgroundDelayHandler = new Handler();
    private Runnable mBackgroundTransition;

    public interface BackgroundStateListener {
        void onBecameForeground();

        void onBecameBackground();
    }

    public void registerListener(BackgroundStateListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(BackgroundStateListener listener) {
        listeners.remove(listener);
    }

    public boolean isInBackground() {
        return mInBackground;
    }

    private void updateBackgroundStateOnActivityResumed() {
        if (mBackgroundTransition != null) {
            mBackgroundDelayHandler.removeCallbacks(mBackgroundTransition);
            mBackgroundTransition = null;
        }

        if (mInBackground) {
            mInBackground = false;
            FriendlyLog.log("I","App", "Foreground", "App to foreground");
            notifyOnBecameForeground();
            if (currentOrientation == -1){
                currentOrientation = DevTools.getAppContext().getResources().getConfiguration().orientation;
                String orientationType = (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) ? "Landscape" : "Portrait";
                FriendlyLog.log("I", "Device", orientationType, "Orientation is " + orientationType.toLowerCase());
            }
        }
    }

    private void updateBackgroundStateOnActivityPaused() {
        if (!mInBackground && mBackgroundTransition == null) {
            mBackgroundTransition = new Runnable() {
                @Override
                public void run() {
                    mInBackground = true;
                    mBackgroundTransition = null;
                    FriendlyLog.log("I","App", "Background", "App to background");
                    notifyOnBecameBackground();
                }
            };
            mBackgroundDelayHandler.postDelayed(mBackgroundTransition, BACKGROUND_DELAY);
        }
    }

    private void notifyOnBecameForeground() {
        for (BackgroundStateListener listener : listeners) {
            try {
                listener.onBecameForeground();
            } catch (Exception e) {
                Log.d(DevTools.TAG, "Listener threw exception!", e);
            }
        }
    }

    private void notifyOnBecameBackground() {
        for (BackgroundStateListener listener : listeners) {
            try {
                listener.onBecameBackground();
            } catch (Exception e) {
                Log.d(DevTools.TAG, "Listener threw exception!", e);
            }
        }
    }

    //endregion

    //region [ FRAGMENT LOGGER ]

    private void registerFragmentLifecycleCallbacks(Activity activity) {
        if (activity instanceof AppCompatActivity){
            if (supportFragmentCallbacks==null)
                this.supportFragmentCallbacks = new SupportFragmentLifecycleCallbacks();

            FragmentManager supportFragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            supportFragmentManager.registerFragmentLifecycleCallbacks(supportFragmentCallbacks, true);

            String message = "FragmentCallbacks registered: "
                    + activity.getClass().getSimpleName()
                    + " - " + supportFragmentManager.toString();
            FriendlyLog.log("V", "Fragment", "CallbacksRegistered", message);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //TODO
            /*if (fragmentCallbacks==null)
                this.fragmentCallbacks = new FragmentLifecycleCallbacks();

            activity.getFragmentManager()
                    .registerFragmentLifecycleCallbacks(fragmentCallbacks, true);*/
        }
    }

    private void unregisterFragmentLifecycleCallbacks(Activity activity){
        if (activity instanceof AppCompatActivity){
            FragmentManager supportFragmentManager = ((AppCompatActivity) activity).getSupportFragmentManager();
            supportFragmentManager.unregisterFragmentLifecycleCallbacks(supportFragmentCallbacks);

            String message = "FragmentCallbacks unregistered: "
                    + activity.getClass().getSimpleName()
                    + " - " + supportFragmentManager.toString();
            FriendlyLog.log("V", "Fragment", "CallbacksUnregistered", message);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            //TODO
            /*activity.getFragmentManager()
                    .unregisterFragmentLifecycleCallbacks(fragmentCallbacks);*/
        }
    }

    //endregion

    public static void friendlyLog(String severity, String type, Activity activity) {
        String message = "Activity " + type.toLowerCase() + ": " + activity.getClass().getSimpleName();
        FriendlyLog.log(severity, "Activity", type, message);
    }
}