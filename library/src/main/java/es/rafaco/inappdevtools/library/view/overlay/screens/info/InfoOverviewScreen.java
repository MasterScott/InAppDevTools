package es.rafaco.inappdevtools.library.view.overlay.screens.info;

import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.view.components.flex.CardData;
import es.rafaco.inappdevtools.library.view.components.flex.FlexibleAdapter;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;

//#ifdef ANDROIDX
//@import androidx.recyclerview.widget.RecyclerView;
//#else
import android.support.v7.widget.RecyclerView;
//#endif

public class InfoOverviewScreen extends Screen {

    public InfoOverviewScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "Info";
    }

    @Override
    public int getBodyLayoutId() { return R.layout.flexible_container; }

    @Override
    protected void onCreate() {
        //Nothing needed
    }

    @Override
    protected void onStart(ViewGroup view) {
        List<Object> data = initData();
        initAdapter(data);
    }

    private List<Object> initData() {
        List<Object> data = new ArrayList<>();

        InfoPage[] infoPages = InfoPage.values();

        for (int i = 0 ; i<infoPages.length ; i++){
            InfoPage page = infoPages[i];
            final String pageParam = String.valueOf(i);

            data.add(new CardData(page.getTitle(),
                    page.getOverview(),
                    page.getIcon(),
                    new Runnable() {
                        @Override
                        public void run() { OverlayService.performNavigation(InfoScreen.class, pageParam);
                        }
                    }));
        }
        return data;
    }

    private void initAdapter(List<Object> data) {
        FlexibleAdapter adapter = new FlexibleAdapter(1, data);
        RecyclerView recyclerView = bodyView.findViewById(R.id.flexible);
        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        //Nothing needed
    }

    @Override
    protected void onDestroy() {
        //Nothing needed
    }
}
