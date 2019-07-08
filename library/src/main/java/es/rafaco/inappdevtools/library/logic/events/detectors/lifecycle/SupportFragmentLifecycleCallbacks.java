package es.rafaco.inappdevtools.library.logic.events.detectors.lifecycle;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

//#ifdef MODERN
//@import androidx.fragment.app.Fragment;
//@import androidx.fragment.app.FragmentManager;
//#else
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
//#endif

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;

public class SupportFragmentLifecycleCallbacks extends FragmentManager.FragmentLifecycleCallbacks {

    @Override
    public void onFragmentPreAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentPreAttached(fm, f, context);
        friendlyLog("V","PreAttach", fm, f);
    }

    @Override
    public void onFragmentAttached(FragmentManager fm, Fragment f, Context context) {
        super.onFragmentAttached(fm, f, context);
        friendlyLog("D","Attach", fm, f);
    }

    @Override
    public void onFragmentCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentCreated(fm, f, savedInstanceState);
        friendlyLog("D","Create", fm, f);
    }

    @Override
    public void onFragmentActivityCreated(FragmentManager fm, Fragment f, Bundle savedInstanceState) {
        super.onFragmentActivityCreated(fm, f, savedInstanceState);
        friendlyLog("V","ActivityCreate", fm, f);
    }

    @Override
    public void onFragmentViewCreated(FragmentManager fm, Fragment f, View v, Bundle savedInstanceState) {
        super.onFragmentViewCreated(fm, f, v, savedInstanceState);
        friendlyLog("V","ViewCreate", fm, f);
    }

    @Override
    public void onFragmentStarted(FragmentManager fm, Fragment f) {
        super.onFragmentStarted(fm, f);
        friendlyLog("D","Start", fm, f);
    }

    @Override
    public void onFragmentResumed(FragmentManager fm, Fragment f) {
        super.onFragmentResumed(fm, f);
        friendlyLog("D","Resume", fm, f);

        //TODO: skip on rotation like with activities
        friendlyLog("I","Shown", fm, f);
    }

    @Override
    public void onFragmentPaused(FragmentManager fm, Fragment f) {
        super.onFragmentPaused(fm, f);
        friendlyLog("V","Pause", fm, f);
    }

    @Override
    public void onFragmentStopped(FragmentManager fm, Fragment f) {
        super.onFragmentStopped(fm, f);
        friendlyLog("D","Stop", fm, f);
    }

    @Override
    public void onFragmentSaveInstanceState(FragmentManager fm, Fragment f, Bundle outState) {
        super.onFragmentSaveInstanceState(fm, f, outState);
        friendlyLog("V","SaveInstanceState", fm, f);
    }

    @Override
    public void onFragmentViewDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentViewDestroyed(fm, f);
        friendlyLog("V","ViewDestroyed", fm, f);
    }

    @Override
    public void onFragmentDestroyed(FragmentManager fm, Fragment f) {
        super.onFragmentDestroyed(fm, f);
        friendlyLog("D","Destroyed", fm, f);
    }

    @Override
    public void onFragmentDetached(FragmentManager fm, Fragment f) {
        super.onFragmentDetached(fm, f);
        friendlyLog("D","Detach", fm, f);
    }


    public static void friendlyLog(String severity, String type, FragmentManager fm, Fragment f) {
        String message = "Fragment " + type.toLowerCase() + ": "
                + f.getClass().getSimpleName() + " at "
                + f.getActivity().getClass().getSimpleName()
                + " [" + f.toString() + "]";
        FriendlyLog.log(severity, "Fragment", type, message);
    }
}