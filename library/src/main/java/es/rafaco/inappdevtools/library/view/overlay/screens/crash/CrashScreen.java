/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2020 Rafael Acosta Alvarez
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.rafaco.inappdevtools.library.view.overlay.screens.crash;

import android.text.TextUtils;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.reports.ReportType;
import es.rafaco.inappdevtools.library.storage.db.entities.Screenshot;
import es.rafaco.inappdevtools.library.storage.files.utils.FileProviderUtils;
import es.rafaco.inappdevtools.library.view.components.items.ButtonBorderlessFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.logic.utils.ClipboardUtils;
import es.rafaco.inappdevtools.library.logic.utils.ExternalIntentUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Crash;
import es.rafaco.inappdevtools.library.storage.db.entities.CrashDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Sourcetrace;
import es.rafaco.inappdevtools.library.view.components.cards.CardHeaderFlexData;
import es.rafaco.inappdevtools.library.view.components.groups.CardGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.FlexAdapter;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.CollapsibleFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ImageData;
import es.rafaco.inappdevtools.library.view.components.items.OverviewData;
import es.rafaco.inappdevtools.library.view.components.groups.RecyclerGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.SeparatorFlexData;
import es.rafaco.inappdevtools.library.view.components.items.TextFlexData;
import es.rafaco.inappdevtools.library.view.components.items.TraceGroupItem;
import es.rafaco.inappdevtools.library.view.components.items.TraceItemData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.log.LogScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;
import es.rafaco.inappdevtools.library.view.utils.UiUtils;

public class CrashScreen extends AbstractFlexibleScreen {

    public CrashScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public boolean hasHorizontalMargin(){
        return false;
    }

    @Override
    protected void onAdapterStart() {
        Crash data = getData();
        List<Object> flexData =  getFlexibleData(data);
        updateAdapter(flexData);
    }

    private boolean isJustCrashed() {
        return TextUtils.isEmpty(getParam());
    }

    private long getCrashIdFromParam() {
        if (!TextUtils.isEmpty(getParam()))
            return Long.parseLong(getParam());
        return -1;
    }

    private Crash getData() {
        Crash result = null;
        CrashDao crashDao = IadtController.getDatabase().crashDao();
        result = (isJustCrashed()) ? crashDao.getLast() : crashDao.findById(getCrashIdFromParam());
        return result;
    }

    private List<Object> getFlexibleData(Crash crash) {
        List<Object> data = new ArrayList<>();
        if (crash == null){
            addEmptyOverview(data);
            return data;
        }

        addOverview(data, crash);
        addImageAndButtons(data, crash);

        TraceGrouper traceGrouper = initTraceGrouper(crash);
        initExceptionCard(data, crash, traceGrouper, false);
        initExceptionCard(data, crash, traceGrouper, true);

        return data;
    }

    private void addEmptyOverview(List<Object> data) {
        data.add(new OverviewData("Crash not found",
                "This is weird :(",
                R.string.gmd_bug_report,
                R.color.iadt_text_low));
    }

    private void addOverview(List<Object> data, Crash crash) {
        String title = isJustCrashed()
                ? "Your app just crashed"
                : "Session " + crash.getSessionId() + " crashed ";

        StringBuilder content = new StringBuilder();
        content.append("When: " + Humanizer.getElapsedTimeLowered(crash.getDate()));
        content.append(Humanizer.newLine());
        content.append("App status: " + (crash.isForeground() ? "Foreground" : "Background"));
        content.append(Humanizer.newLine());
        content.append("Last activity: " + crash.getLastActivity());
        content.append(Humanizer.newLine());
        content.append("Thread: " + crash.getThreadName());

        data.add(new OverviewData(title,
                content.toString(),
                R.string.gmd_bug_report,
                R.color.rally_orange));
    }

    private void addImageAndButtons(List<Object> data, final Crash crash) {
        LinearGroupFlexData horizontalContainer = new LinearGroupFlexData();
        horizontalContainer.setHorizontal(true);
        horizontalContainer.setHorizontalMargin(true);
        addVerticalButtons(horizontalContainer, crash);
        addImage(horizontalContainer, crash);
        data.add(horizontalContainer);
    }

    private void addImage(LinearGroupFlexData data, Crash crash) {
        long screenId = crash.getScreenId();
        final Screenshot screenshot = IadtController.get().getDatabase().screenshotDao().findById(screenId);
        if (screenshot !=null && !TextUtils.isEmpty(screenshot.getPath())){
            ImageData image = new ImageData(screenshot.getPath());
            image.setIcon(R.drawable.ic_photo_library_white_24dp);
            image.setPerformer(new Runnable() {
                @Override
                public void run() {
                    FileProviderUtils.viewExternally("", screenshot.getPath() );
                }
            });
            data.add(image);
        }
    }

    private void addVerticalButtons(LinearGroupFlexData data, final Crash crash) {
        LinearGroupFlexData verticalButtons = new LinearGroupFlexData();
        verticalButtons.setFullSpan(false);

        verticalButtons.add(new ButtonFlexData("Report now!",
                R.drawable.ic_send_white_24dp,
                R.color.rally_orange,
                new Runnable() {
                    @Override
                    public void run() {
                        IadtController.get().startReportWizard(ReportType.CRASH, crash.getUid());
                    }
                }));

        final long logId = IadtController.getDatabase().friendlyDao()
                .findLogIdByCrashId(crash.getUid());

        verticalButtons.add(new ButtonFlexData("Repro Steps",
                R.drawable.ic_format_list_numbered_white_24dp,
                R.color.rally_green_alpha,
                new Runnable() {
                    @Override
                    public void run() {
                        LogFilterHelper stepsFilter = new LogFilterHelper(LogFilterHelper.Preset.REPRO_STEPS);
                        stepsFilter.setSessionById(crash.getSessionId());
                        OverlayService.performNavigation(LogScreen.class,
                                LogScreen.buildParams(stepsFilter.getUiFilter(), logId));
                    }
                }));
        verticalButtons.add(new ButtonFlexData("Full Logs",
                R.drawable.ic_format_align_left_white_24dp,
                R.color.rally_blue_med,
                new Runnable() {
                    @Override
                    public void run() {
                        LogFilterHelper logsFilter = new LogFilterHelper(LogFilterHelper.Preset.DEBUG);
                        logsFilter.setSessionById(crash.getSessionId());
                        OverlayService.performNavigation(LogScreen.class,
                                LogScreen.buildParams(logsFilter.getUiFilter(), logId));
                    }
                }));
        data.add(verticalButtons);
    }

    private TraceGrouper initTraceGrouper(Crash crash) {
        List<Sourcetrace> traces = DevToolsDatabase.getInstance().sourcetraceDao().filterCrash(crash.getUid());
        TraceGrouper grouper = new TraceGrouper();
        grouper.process(traces);
        return grouper;
    }

    private void initExceptionCard(List<Object> data, Crash crash, TraceGrouper traceGrouper, boolean isCause) {

        String title, message;
        final String composedMessage;
        final FlexAdapter adapter;

        TextFlexData overCard = new TextFlexData("");
        overCard.setGravity(Gravity.RIGHT);
        int horizontalMargin = (int) UiUtils.dpToPx(getContext(), 14); //standard+innerCard
        int topMargin = (int) UiUtils.dpToPx(getContext(), 4);
        int buttonMargin = (int) UiUtils.dpToPx(getContext(), 0);
        overCard.setMargins(horizontalMargin, topMargin, horizontalMargin, buttonMargin);

        if (!isCause){
            title = crash.getException();
            message = crash.getMessage();
            composedMessage = title + ". " + message;
            adapter = traceGrouper.getExceptionAdapter();
            overCard.setText("Exception");
            data.add(overCard);
        }
        else{
            title = crash.getCauseException();
            message = crash.getCauseMessage();
            if (TextUtils.isEmpty(title) && TextUtils.isEmpty(message)){
                return;
            }
            composedMessage = title + ". " + message;
            adapter = traceGrouper.getCauseAdapter();
            overCard.setText("Cause");
            data.add(overCard);
        }
        
        CardGroupFlexData cardGroup = new CardGroupFlexData();
        cardGroup.setFullWidth(true);
        cardGroup.setVerticalMargin(true);
        cardGroup.setBgColorResource(R.color.rally_orange_alpha);
        LinearGroupFlexData cardData = new LinearGroupFlexData();

        CardHeaderFlexData headerData = new CardHeaderFlexData.Builder(title)
                .setExpandable(false)
                .setExpanded(true)
                //.setOverview("Exception")
                .build();
        cardData.add(headerData);

        TextFlexData messageData = new TextFlexData(message);
        messageData.setSize(TextFlexData.Size.LARGE);
        messageData.setHorizontalMargin(true);
        cardData.add(messageData);

        List<Object> groupLinear = new ArrayList<>();
        groupLinear.add(new ButtonBorderlessFlexData("Copy",
                R.drawable.ic_content_copy_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        ClipboardUtils.save(getContext(), composedMessage);
                        Iadt.showMessage("Exception copied to clipboard");
                    }
                }));

        groupLinear.add(new ButtonBorderlessFlexData("Google it",
                R.drawable.ic_google_brands,
                new Runnable() {
                    @Override
                    public void run() {
                        String url = "https://www.google.com/search?q=" + composedMessage;
                        ExternalIntentUtils.viewUrl(url);
                    }
                }));
        LinearGroupFlexData buttonList = new LinearGroupFlexData(groupLinear);
        buttonList.setHorizontal(true);
        cardData.add(buttonList);

        SeparatorFlexData separator = new SeparatorFlexData(true);
        separator.setVerticalMargin(true);
        cardData.add(separator);

        if (adapter != null && adapter.getItemCount()>0){
            int tracesCount = adapter.getItemCount();
            String tracesTitle = "";
            Object firstTrace = adapter.getItems().get(0);
            if (firstTrace instanceof TraceGroupItem)
                firstTrace = adapter.getItems().get(1);
            if (firstTrace instanceof TraceItemData){
                Sourcetrace firstSourcetrace = ((TraceItemData) firstTrace).getSourcetrace();
                tracesTitle = firstSourcetrace.formatClassAndMethod();
            }

            CardHeaderFlexData tracesHeaderData = new CardHeaderFlexData.Builder(tracesTitle)
                    .setExpandable(true)
                    .setExpanded(false)
                    .setOverview(tracesCount + "")
                    .build();
            
            RecyclerGroupFlexData tracesContentData = new RecyclerGroupFlexData(adapter);
            tracesContentData.setHorizontalMargin(true);

            CollapsibleFlexData collapsibleData = new CollapsibleFlexData(tracesHeaderData,
                    tracesContentData, false);
            cardData.add(collapsibleData);
        }

        cardGroup.add(cardData);
        data.add(cardGroup);
    }

    //TODO: restore share crash?
    private void share(Crash crash){
        Iadt.showMessage("Sharing crash document");
        DocumentRepository.shareDocument(DocumentType.CRASH, crash);
    }
}