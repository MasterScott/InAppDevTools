package es.rafaco.devtools.view.overlay.screens;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import es.rafaco.devtools.R;
import es.rafaco.devtools.view.overlay.layers.MainOverlayLayerManager;

public abstract class OverlayScreen {

    private final MainOverlayLayerManager manager;
    private ViewGroup headView;
    public ViewGroup bodyView;
    private String param;
    private ViewGroup headContainer;
    private ViewGroup bodyContainer;

    //Abstract constants to define by implementation
    public abstract String getTitle();
    public boolean isMain() {
        return true;
    }
    public int getHeadLayoutId() { return -1;}
    public abstract int getBodyLayoutId();

    //Abstract logic to define by implementation
    protected abstract void onCreate();
    protected abstract void onStart(ViewGroup toolHead);
    protected abstract void onStop();
    protected abstract void onDestroy();

    public OverlayScreen(MainOverlayLayerManager manager) {
        this.manager = manager;
        onCreate();
    }

    public void start(String param){
        this.param = param;

        if (getHeadLayoutId() != -1){
            headContainer = getView().findViewById(R.id.tool_head_container);
            headView = (ViewGroup) getInflater().inflate(getHeadLayoutId(), headContainer, false);
            headView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            headView.setVisibility(View.GONE);
            //targetContainer.removeAllViews();
            headContainer.addView(headView);
        }

        bodyContainer = getView().findViewById(R.id.tool_body_container);
        bodyView = (ViewGroup) getInflater().inflate(getBodyLayoutId(), bodyContainer, false);
        bodyView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        bodyView.setVisibility(View.GONE);
        //targetContainer.removeAllViews();
        bodyContainer.addView(bodyView);

        onStart(getView());
    }

    public void stop(){
        if (headView !=null) headContainer.removeView(headView); //headView.removeAllViews();
        if (bodyView !=null) bodyContainer.removeView(bodyView); //bodyView.removeAllViews();
        onStop();
    }

    public void show(){
        if (headView !=null) headView.setVisibility(View.VISIBLE);
        if (bodyView !=null) bodyView.setVisibility(View.VISIBLE);
    }

    public void destroy(){
        onDestroy();
    }


    public MainOverlayLayerManager getScreenManager() {
        return manager;
    }
    public String getParam() {
        return param;
    }
    public Context getContext() {
        return getScreenManager().getContext();
    }
    public LayoutInflater getInflater() {
        return getScreenManager().getInflater();
    }
    public ViewGroup getView() {
        return getScreenManager().getView();
    }
}
