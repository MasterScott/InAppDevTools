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

package es.rafaco.inappdevtools.library.logic.events;

import es.rafaco.inappdevtools.library.logic.log.FriendlyLog;
import es.rafaco.inappdevtools.library.logic.utils.DateUtils;
import es.rafaco.inappdevtools.library.storage.db.entities.Friendly;

public class IadtEvent {

    private long date;
    private String severity = "V";
    private String category = "Custom";
    private String subcategory = "Default";
    private String message;
    private String extra;
    private long linkedId;

    public IadtEvent(long date) {
        this.date = date;
    }

    public IadtEvent() {
        setDateNow();
    }

    public IadtEvent setDateNow() {
        date = DateUtils.getLong();
        return this;
    }

    public IadtEvent setDate(long date) {
        this.date = date;
        return this;
    }

    public IadtEvent setMessage(String message) {
        this.message = message;
        return this;
    }


    /**
     * @param severity (V, D, I, W, E, F, WTF)
     * @return
     */
    public IadtEvent setSeverity(String severity) {
        this.severity = severity;
        return this;
    }

    public IadtEvent setCategory(String category) {
        this.category = category;
        return this;
    }

    public IadtEvent setSubcategory(String subcategory) {
        this.subcategory = subcategory;
        return this;
    }

    public IadtEvent setExtra(String extra) {
        this.extra = extra;
        return this;
    }

    public IadtEvent setLinkedId(long linkedId) {
        this.linkedId = linkedId;
        return this;
    }

    public void fire(){
        final Friendly log = new Friendly();
        log.setDate(date);
        log.setSeverity(severity);
        log.setCategory(category);
        log.setSubcategory(subcategory);
        log.setMessage(message);
        log.setExtra(extra);
        log.setLinkedId(linkedId);

        FriendlyLog.log(log);
    }
}