package es.rafaco.inappdevtools.library.view.overlay.screens.home;

import android.view.ViewGroup;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.runnables.RunnableItem;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayUIService;
import es.rafaco.inappdevtools.library.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.OverlayScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.logcat.LogcatScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.network.NetworkScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.screenshots.ScreensScreen;

public class MoreScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public MoreScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "More";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
    }
    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        data.add("Playgrounds and old screens (pending to remove):\n" +
                " - Go to Log screen to see all items from Network, Screens, Errors and Logcat\n" +
                " - Config is now on toolbar 'Settings'\n");

        data.add(new RunnableItem("Network",
                R.drawable.ic_cloud_queue_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(NetworkScreen.class);
                    }
                }));


        data.add(new RunnableItem("Screens",
                R.drawable.ic_photo_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(ScreensScreen.class);
                    }
                }));

        data.add(new RunnableItem("Errors",
                R.drawable.ic_bug_report_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(ErrorsScreen.class);
                    }
                }));

        /*data.add(new RunnableItem("Analysis",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(AnalysisScreen.class);
                    }
                }));*/

        data.add(new RunnableItem("Logcat",
                R.drawable.ic_android_white_24dp,
                new Runnable() {
                    @Override
                    public void run() { OverlayUIService.performNavigation(LogcatScreen.class);
                    }
                }));

        data.add(new RunnableItem("Config",
                R.drawable.ic_settings_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {OverlayUIService.performNavigation(ConfigScreen.class);
                    }
                }));

        return data;
    }

    private void initAdapter(List<Object> data) {
        adapter = new FlexibleAdapter(3, data);
        recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }
}
