package es.rafaco.devtools.view.overlay.screens.home;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.screens.OverlayScreen;
import es.rafaco.devtools.view.overlay.OverlayScreenManager;
import es.rafaco.devtools.view.DecoratedToolInfoAdapter;
import es.rafaco.devtools.view.DecoratedToolInfo;
import es.rafaco.devtools.view.overlay.screens.errors.ErrorsScreen;
import es.rafaco.devtools.view.overlay.screens.info.InfoHelper;
import es.rafaco.devtools.view.overlay.screens.info.InfoScreen;
import es.rafaco.devtools.view.overlay.screens.report.ReportScreen;
import es.rafaco.devtools.view.overlay.screens.screenshots.ScreensScreen;

public class HomeScreen extends OverlayScreen {

    private DecoratedToolInfoAdapter adapter;
    private RecyclerView recyclerView;
    private TextView welcome;
    private ArrayList<DecoratedToolInfo> dataList;

    public HomeScreen(OverlayScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Home";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.tool_home_body; }

    @Override
    protected void onCreate() {
    }

    @Override
    protected void onStart(ViewGroup view) {
        initView(view);
        initAdapter(view);
        updateList();
    }

    @Override
    protected void onStop() {
    }

    @Override
    protected void onDestroy() {
    }

    private void initView(View toolView) {
        welcome = toolView.findViewById(R.id.home_welcome);
        welcome.setText(getWelcomeMessage());
    }

    public String getWelcomeMessage(){
        InfoHelper helper = new InfoHelper(getContext());
        return "Welcome to " + helper.getAppName() + "'s DevTools";
    }

    private void initAdapter(View view) {
        adapter = new DecoratedToolInfoAdapter(getContext(), new ArrayList<DecoratedToolInfo>());

        recyclerView = view.findViewById(R.id.home_list);
        ViewCompat.setNestedScrollingEnabled(recyclerView, false);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void updateList() {
        //TODO: getManager().requestHomeInfos()
        //adapter.add(getManager().getScreenClass(InfoScreen.class).getHomeInfo());
        //adapter.add(getManager().getScreenClass(ErrorsScreen.class).getHomeInfo());
        //adapter.add(getManager().getScreenClass(ScreensScreen.class).getHomeInfo());
        //adapter.add(getManager().getScreenClass(ReportScreen.class).getHomeInfo());

        adapter.notifyDataSetChanged();
        recyclerView.requestLayout();
    }

    //TODO: datalist not initialized any more!
    public void updateContent(Class<?> toolClass, String content){
        if (dataList!=null && dataList.size()>0){
            for (DecoratedToolInfo info: dataList){
                if (info.getClass().equals(toolClass)){
                    info.setMessage(content);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}