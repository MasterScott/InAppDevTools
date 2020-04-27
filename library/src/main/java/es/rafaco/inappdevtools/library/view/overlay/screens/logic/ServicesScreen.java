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

package es.rafaco.inappdevtools.library.view.overlay.screens.logic;

import android.app.ActivityManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.Iadt;
import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentSectionData;
import es.rafaco.inappdevtools.library.logic.runnables.RunButton;
import es.rafaco.inappdevtools.library.logic.sources.SourcesManager;
import es.rafaco.inappdevtools.library.logic.utils.RunningServicesUtils;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.view.components.flex.OverviewData;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractFlexibleScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.SourceDetailScreen;
import es.rafaco.inappdevtools.library.view.utils.Humanizer;

public class ServicesScreen extends AbstractFlexibleScreen {

    public ServicesScreen(ScreenManager manager) {
        super(manager);
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    @Override
    protected void onAdapterStart() {
        updateAdapter(getFlexibleData());
    }

    private List<Object> getFlexibleData() {
        List<Object> data = new ArrayList<>();

        data.add(new OverviewData("Services",
                "Running services declared by this apk",
                R.string.gmd_store,
                R.color.rally_white));

        List<ActivityManager.RunningServiceInfo> serviceInfos = RunningServicesUtils.getList();

        if (serviceInfos.size() == 0){
            String title = "No running services";
            DocumentSectionData.Builder noServiceDataBuilder = new DocumentSectionData.Builder(title)
                    //.setIcon(R.string.gmd_view_carousel)
                    .setExpandable(false);
            data.add(noServiceDataBuilder.build());
        }
        else{
            for (ActivityManager.RunningServiceInfo info : serviceInfos) {

                String title = RunningServicesUtils.getTitle(info);
                String content = RunningServicesUtils.getContent(info);
                DocumentSectionData.Builder serviceDataBuilder = new DocumentSectionData.Builder(title)
                        //.setIcon(R.string.gmd_view_carousel)
                        .setExpandable(false)
                        .add(content);

                String className = RunningServicesUtils.getClassName(info);
                final String srcPath = getSourcesManager()
                        .getPathFromClassName(className);
                String srcFile = Humanizer.getLastPart(srcPath, "/");
                if (TextUtils.isEmpty(srcFile)){
                    serviceDataBuilder.addButton(new RunButton("Unavailable",
                            R.drawable.ic_code_white_24dp,
                            new Runnable() {
                                @Override
                                public void run() {
                                    Iadt.showMessage("Source not found");
                                }
                            }));
                }
                else{
                    serviceDataBuilder.addButton(new RunButton(srcFile,
                            R.drawable.ic_code_white_24dp,
                            new Runnable() {
                                @Override
                                public void run() {
                                    OverlayService.performNavigation(SourceDetailScreen.class,
                                            SourceDetailScreen.buildSourceParams(srcPath, -1));
                                }
                            }));
                }

                data.add(serviceDataBuilder.build());
            }
        }

        data.add("");
        data.add("View AndroidManifest");
        data.add(new RunButton("Original",
                R.drawable.ic_local_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String params = SourceDetailScreen.buildSourceParams(IadtPath.SOURCES
                                + "/AndroidManifest.xml");
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));
        data.add(new RunButton("Merged",
                R.drawable.ic_local_library_white_24dp,
                new Runnable() {
                    @Override
                    public void run() {
                        String params = SourceDetailScreen.buildSourceParams(IadtPath.GENERATED
                                + "/merged_manifests/AndroidManifest.xml");
                        OverlayService.performNavigation(SourceDetailScreen.class, params);
                    }
                }));
        return data;
    }

    private SourcesManager getSourcesManager() {
        return IadtController.get().getSourcesManager();
    }
}