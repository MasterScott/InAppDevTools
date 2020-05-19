/*
 * This source file is part of InAppDevTools, which is available under
 * Apache License, Version 2.0 at https://github.com/rafaco/InAppDevTools
 *
 * Copyright 2018-2019 Rafael Acosta Alvarez
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

package es.rafaco.inappdevtools.library.view.overlay.screens.builds;

import java.util.ArrayList;
import java.util.List;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.DocumentRepository;
import es.rafaco.inappdevtools.library.logic.documents.DocumentType;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.view.components.cards.CardData;
import es.rafaco.inappdevtools.library.view.components.groups.LinearGroupFlexData;
import es.rafaco.inappdevtools.library.view.components.items.ButtonFlexData;
import es.rafaco.inappdevtools.library.storage.db.entities.Build;
import es.rafaco.inappdevtools.library.view.overlay.OverlayService;
import es.rafaco.inappdevtools.library.view.overlay.ScreenManager;
import es.rafaco.inappdevtools.library.view.overlay.screens.AbstractDocumentScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.Screen;
import es.rafaco.inappdevtools.library.view.overlay.screens.session.SessionsScreen;
import es.rafaco.inappdevtools.library.view.overlay.screens.sources.RepoInfoScreen;

public class BuildDetailScreen extends AbstractDocumentScreen {

    private long buildId;

    public BuildDetailScreen(ScreenManager manager) {
        super(manager);
        buildId = getRealBuildIdParam();
    }

    @Override
    public String getTitle() {
        return "";
    }

    @Override
    protected DocumentType getDocumentType() {
        return DocumentType.BUILD_INFO;
    }

    @Override
    protected Object getDocumentParam() {
        return Long.parseLong(getParam());
    }

    @Override
    protected Class<? extends Screen> getMasterScreenClass() {
        return BuildsScreen.class;
    }

    protected long getRealBuildIdParam() {
        long sessionId = (long) getDocumentParam();
        Build build = IadtController.getDatabase().buildDao().findBySessionId(sessionId);
        return build.getUid();
    }

    @Override
    protected List<Object> buildDataFromDocument(DocumentData reportData) {
        List<Object> objectList = new ArrayList<Object>(reportData.getSections());
        objectList.add(0, buildOverviewData(reportData));
        objectList.add(1, getButtonList());
        objectList.add(2, "");
        return objectList;
    }

    private LinearGroupFlexData getButtonList() {
        LinearGroupFlexData linearGroupData = new LinearGroupFlexData();
        linearGroupData.setHorizontal(true);
        linearGroupData.add(new ButtonFlexData(
                "Repo status",
                R.drawable.ic_code_white_24dp,
                R.color.rally_blue_med,
                new Runnable() {
                    @Override
                    public void run() {OverlayService.performNavigation(RepoInfoScreen.class, getDocumentParam() + "");
                    }
                }));
        linearGroupData.add(new ButtonFlexData(
                "Sessions",
                R.drawable.ic_timeline_white_24dp,
                R.color.rally_purple,
                new Runnable() {
                    @Override
                    public void run() {OverlayService.performNavigation(SessionsScreen.class, buildId + "");
                    }
                }));

        return linearGroupData;
    }
}
