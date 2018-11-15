package es.rafaco.devtools.view.overlay.screens.home;

import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.devtools.DevTools;
import es.rafaco.devtools.R;
import es.rafaco.devtools.logic.integrations.RunnableConfig;
import es.rafaco.devtools.view.components.FlexibleAdapter;
import es.rafaco.devtools.view.overlay.OverlayUIService;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.screens.friendlylog.FriendlyLogScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.report.ReportScreen;

public class HomeScreen extends OverlayScreen {

    private FlexibleAdapter adapter;
    private RecyclerView recyclerView;

    public HomeScreen(MainOverlayLayerManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "DevTools";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_flexible; }

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

        InfoHelper helper = new InfoHelper();
        String welcome = helper.getFormattedAppLong() + "\n" + helper.getFormattedDeviceLong();
        data.add(welcome);
        //data.add(new TextConfig("Text", ))

        data.add(new RunnableConfig("Run",
                R.drawable.ic_run_white_24dp,
                () ->  OverlayUIService.performNavigation(RunScreen.class)));

        data.add(new RunnableConfig("Steps",
                R.drawable.ic_history_white_24dp,
                () ->  OverlayUIService.performNavigation(FriendlyLogScreen.class)));

        data.add(new RunnableConfig("Report",
                R.drawable.ic_send_rally_24dp,
                () ->  OverlayUIService.performNavigation(ReportScreen.class)));

        data.add(new RunnableConfig("Info",
                R.drawable.ic_info_white_24dp,
                () -> OverlayUIService.performNavigation(InfoScreen.class)));

        data.add(new RunnableConfig("Config",
                R.drawable.ic_settings_white_24dp,
                () -> DevTools.showMessage("TODO")));

        data.add(new RunnableConfig("Inspect",
                R.drawable.ic_developer_mode_white_24dp,
                () -> OverlayUIService.performNavigation(InspectScreen.class)));

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
