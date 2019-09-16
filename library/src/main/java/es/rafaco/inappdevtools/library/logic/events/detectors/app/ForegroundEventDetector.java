package es.rafaco.inappdevtools.library.logic.events.detectors.app;

import android.os.Handler;
import android.os.Looper;

import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.logic.events.EventDetector;
import es.rafaco.inappdevtools.library.logic.events.EventManager;
import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.AppUtils;

public class ForegroundEventDetector extends EventDetector {

    private boolean mInBackground = true;
    private static final long BACKGROUND_DELAY = 500;
    private final Handler mBackgroundDelayHandler = new Handler(Looper.getMainLooper());
    private Runnable mBackgroundTransition;

    public ForegroundEventDetector(EventManager eventManager) {
        super(eventManager);
    }

    @Override
    public void subscribe() {
        eventManager.subscribe(Event.APP_START, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                onAppStart();
            }
        });

        eventManager.subscribe(Event.ACTIVITY_ON_RESUME, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                updateOnActivityResumed();
            }
        });
        eventManager.subscribe(Event.ACTIVITY_ON_PAUSE, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                updateOnActivityPaused();
            }
        });

        eventManager.subscribe(Event.IMPORTANCE_FOREGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I","App", "Foreground",
                        "App is foreground");
            }
        });
        eventManager.subscribe(Event.IMPORTANCE_BACKGROUND, new EventManager.Listener() {
            @Override
            public void onEvent(Event event, Object param) {
                FriendlyLog.log("I","App", "Background",
                        "App is background");
            }
        });
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    private void onAppStart() {
        if (!AppUtils.isForegroundImportance(getContext())) {
            mInBackground = true;
            eventManager.fire(Event.IMPORTANCE_BACKGROUND);
        }else{
            mInBackground = false;
            eventManager.fire(Event.IMPORTANCE_FOREGROUND);
        }
    }

    private void updateOnActivityResumed() {
        if (mBackgroundTransition != null) {
            mBackgroundDelayHandler.removeCallbacks(mBackgroundTransition);
            mBackgroundTransition = null;
        }

        if (mInBackground && AppUtils.isForegroundImportance(getContext())) {
            mInBackground = false;
            eventManager.fire(Event.IMPORTANCE_FOREGROUND);
        }
    }

    private void updateOnActivityPaused() {
        if (!mInBackground && mBackgroundTransition == null) {
            mBackgroundTransition = new Runnable() {
                @Override
                public void run() {
                    mInBackground = true;
                    eventManager.fire(Event.IMPORTANCE_BACKGROUND);
                    mBackgroundTransition = null;
                }
            };
            mBackgroundDelayHandler.postDelayed(mBackgroundTransition, BACKGROUND_DELAY);
        }
    }
}
