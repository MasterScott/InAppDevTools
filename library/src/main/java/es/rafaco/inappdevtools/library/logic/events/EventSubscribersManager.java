package es.rafaco.inappdevtools.library.logic.events;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.DevTools;
import es.rafaco.inappdevtools.library.DevToolsConfig;
import es.rafaco.inappdevtools.library.logic.events.subscribers.*;
import es.rafaco.inappdevtools.library.logic.utils.ClassHelper;

public class EventSubscribersManager {

    private final EventManager eventManager;
    private List<EventSubscriber> items = new ArrayList<>();

    public EventSubscribersManager(EventManager eventManager) {
        this.eventManager = eventManager;
        init(DevTools.getConfig());
    }

    public void init(DevToolsConfig config) {
        initItems();
    }


    private void initItems() {
        initItem(ForegroundInitSubscriber.class);
        initItem(ForegroundSyncSubscriber.class);
    }

    private void initItem(Class<? extends EventSubscriber> className) {
        EventSubscriber subscriber = new ClassHelper<EventSubscriber>().createClass(className,
                EventManager.class, eventManager);
        if (subscriber != null){
            items.add(subscriber);
        }
    }

    public EventSubscriber get(Class<? extends EventSubscriber> className) {
        for (EventSubscriber subscriber : items) {
            if (subscriber.getClass().equals(className)){
                return subscriber;
            }
        }
        return  null;
    }

    public void destroy() {
        items = null;
    }
}