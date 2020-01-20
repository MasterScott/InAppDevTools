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

package es.rafaco.inappdevtools.library.logic.documents.detail;

import android.content.Context;

import java.util.List;

import es.rafaco.inappdevtools.library.R;
import es.rafaco.inappdevtools.library.logic.documents.AbstractDocumentGenerator;
import es.rafaco.inappdevtools.library.logic.documents.DetailDocument;
import es.rafaco.inappdevtools.library.logic.documents.data.DocumentData;
import es.rafaco.inappdevtools.library.logic.log.datasource.LogQueryHelper;
import es.rafaco.inappdevtools.library.logic.log.filter.LogFilterHelper;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.DevToolsDatabase;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;
import es.rafaco.inappdevtools.library.storage.db.entities.FriendlyDao;
import es.rafaco.inappdevtools.library.storage.db.entities.Session;

public class SessionLogsDetailGenerator extends AbstractDocumentGenerator {

    private final Session session;
    private List<Friendly> rawData;

    public SessionLogsDetailGenerator(Context context, DetailDocument report, Session param) {
        super(context, report, param);
        this.session = param;
    }

    @Override
    public DocumentData getData() {
        DocumentData.Builder builder = new DocumentData.Builder(getTitle())
                .setIcon(R.string.gmd_history)
                .setOverview(getOverview());

        extractRawData();
        insertRawData(builder);
        return builder.build();
    }


    @Override
    public String getOverview() {
        return "";
    }

    public String getTitle() {
        return "Logcat from Session " + session.getUid();
    }


    private void extractRawData() {
        LogFilterHelper logFilterHelper = new LogFilterHelper(LogFilterHelper.Preset.DEBUG);
        logFilterHelper.setSessionById(session.getUid());
        LogQueryHelper logQueryHelper = new LogQueryHelper(logFilterHelper.getBackFilter());
        FriendlyDao dao = DevToolsDatabase.getInstance().friendlyDao();
        rawData = dao.filterListWithQuery(logQueryHelper.getFilterQuery());
    }

    private void insertRawData(DocumentData.Builder builder) {
        for (Friendly step: rawData) {
            String parsed = String.format("%s %s/%s: %s",
                    DateUtils.formatLogcatDate(step.getDate()),
                    step.getSeverity(),
                    step.getSubcategory(),
                    step.getMessage());
            builder.add(parsed);
        }
    }
}
