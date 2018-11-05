package es.rafaco.devtools.view.overlay.screens.errors;

import androidx.room.InvalidationTracker;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TooManyListenersException;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.storage.db.entities.Anr;
import es.rafaco.devtools.storage.db.entities.Crash;
import es.rafaco.devtools.storage.db.DevToolsDatabase;
import es.rafaco.devtools.logic.crash.SimulatedException;
import es.rafaco.devtools.view.utils.RecyclerViewUtils;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.layers.NavigationStep;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.utils.DecoratedToolInfo;
import es.rafaco.devtools.view.utils.DecoratedToolInfoAdapter;
import es.rafaco.devtools.logic.utils.ThreadUtils;

import static es.rafaco.devtools.logic.utils.DateUtils.getElapsedTimeLowered;

public class ErrorsScreen extends OverlayScreen {

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;

    private Button crashUiButton;
    private Button crashBackButton;
    private Button anrButton;

    private InvalidationTracker.Observer anrObserver;
    private InvalidationTracker.Observer crashObserver;
    private InvalidationTracker tracker;
    private TextView emptyView;

    public ErrorsScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Error log";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_errors_body; }

    @Override
    public int getToolbarLayoutId() {
        return R.menu.errors;
    }

    @Override
    protected void onCreate() {

    }


    @Override
    protected void onStart(ViewGroup view) {
        initView(bodyView);

        getErrors();

        anrObserver = new InvalidationTracker.Observer(new String[]{"anr"}){
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                getErrors();
            }
        };
        crashObserver = new InvalidationTracker.Observer(new String[]{"crash"}){
            @Override
            public void onInvalidated(@NonNull Set<String> tables) {
                getErrors();
            }
        };
        tracker = DevTools.getDatabase().getInvalidationTracker();
        tracker.addObserver(anrObserver);
        tracker.addObserver(crashObserver);
    }

    @Override
    protected void onStop() {
        tracker.removeObserver(anrObserver);
        tracker.removeObserver(crashObserver);
        anrObserver = null;
        crashObserver = null;
    }

    @Override
    protected void onDestroy() {
    }


    private void initView(View view) {
        welcome = view.findViewById(R.id.welcome);
        welcome.setText(getWelcomeMessage());

        initAdapter();

        anrButton = view.findViewById(R.id.anr_button);
        anrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAnrButton();
            }
        });

        crashUiButton = view.findViewById(R.id.crash_ui_button);
        crashUiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashUiButton();
            }
        });

        crashBackButton = view.findViewById(R.id.crash_back_button);
        crashBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCrashBackButton();
            }
        });
    }

    private void onRefresh() {
        getErrors();
    }

    private void onClearAll() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                db.crashDao().deleteAll();
                db.anrDao().deleteAll();

                ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                updateList(array);
            }
        });
    }

    public void onAnrButton() {
        Log.i(DevTools.TAG, "ANR requested, sleeping main thread for a while...");
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    Log.e(DevTools.TAG, "Something wrong happen", e);
                }
            }
        });
    }

    public void onCrashUiButton() {
        Log.i(DevTools.TAG, "Simulated crash on the UI thread...");
        final Exception cause = new TooManyListenersException("The scenic panic make you pressed that button :)");
        ThreadUtils.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                throw new SimulatedException("Simulated crash on the UI thread", cause);
            }
        });
    }

    public void onCrashBackButton() {
        Log.i(DevTools.TAG, "Simulated crash on a background thread...");
        final Exception cause = new TooManyListenersException("The scenic panic make you pressed that button :)");
        ThreadUtils.runOnBackThread(new Runnable() {
            @Override
            public void run() {
                throw new SimulatedException("Simulated crash on a background thread", cause);
            }
        });
    }



    public String getWelcomeMessage(){
        return "Crashes and ANRs (memory leaks and logcat exceptions coming soon):";
    }

    private void getErrors(){
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DevToolsDatabase db = DevTools.getDatabase();
                final ArrayList<DecoratedToolInfo> array = new ArrayList<>();
                List<Crash> crashes = db.crashDao().getAll();
                for (Crash crash : crashes){
                    NavigationStep step = new NavigationStep(CrashDetailScreen.class, String.valueOf(crash.getUid()));
                    array.add(new DecoratedToolInfo(
                            "Crash " + getElapsedTimeLowered(crash.getDate()),
                            crash.getException() + " - " + crash.getMessage(),
                            R.color.rally_orange,
                            crash.getDate(),
                            step));
                }

                List<Anr> anrs = db.anrDao().getAll();
                for (Anr anr : anrs){
                    //TODO: AnrDetailScreen
                    NavigationStep step = new NavigationStep(AnrDetailScreen.class, String.valueOf(anr.getUid()));
                    array.add(new DecoratedToolInfo(
                            "ANR " + getElapsedTimeLowered(anr.getDate()),
                            anr.getCause(),
                            R.color.rally_blue,
                            anr.getDate(),
                            step));
                }

                Collections.sort(array, new Comparator<DecoratedToolInfo>() {
                    @Override
                    public int compare(DecoratedToolInfo o1, DecoratedToolInfo o2) {
                        return o2.getOrder().compareTo(o1.getOrder());
                    }
                });

                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateList(array);
                    }
                });
            }
        });
    }

    private void initAdapter(){

        adapter = new DecoratedToolInfoAdapter(getContext(), new ArrayList<DecoratedToolInfo>());

        recyclerView = getView().findViewById(R.id.errors_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(adapter);

        emptyView = getView().findViewById(R.id.empty_errors_list);
    }

    private void updateList(List<DecoratedToolInfo> errors) {
        adapter.replaceAll(errors);
        RecyclerViewUtils.updateEmptyState(recyclerView, emptyView, errors);
    }


    //region [ TOOL BAR ]

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int selected = item.getItemId();
        if (selected == R.id.action_delete)
        {
            onClearAll();
        }
        else if (selected == R.id.action_simulate)
        {
            //TODO: dialog or popup to choose type of error
            //TODO: show message before crash or anr
            onCrashUiButton();
        }
        else if (selected == R.id.action_send)
        {
            //TODO: send all errors
            DevTools.showMessage("Not already implemented");
        }
        return super.onMenuItemClick(item);
    }

    //endregion
}
