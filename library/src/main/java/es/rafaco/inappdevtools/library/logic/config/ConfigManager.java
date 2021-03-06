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

package es.rafaco.inappdevtools.library.logic.config;

import android.content.Context;

import es.rafaco.inappdevtools.library.IadtController;
import es.rafaco.inappdevtools.library.logic.events.Event;
import es.rafaco.inappdevtools.library.storage.files.IadtPath;
import es.rafaco.inappdevtools.library.storage.files.utils.AssetFileReader;
import es.rafaco.inappdevtools.library.storage.files.utils.JsonHelper;
import es.rafaco.inappdevtools.library.storage.prefs.DevToolsPrefs;

public class ConfigManager {

    private final JsonHelper compileConfig;

    public ConfigManager(Context context) {
        //Always read buildConfig directly from asset
        String fileContents = new AssetFileReader(context).getFileContents(IadtPath.BUILD_CONFIG);
        compileConfig = new JsonHelper(fileContents);
    }

    public Object get(BuildConfigField config) {
        if (config.getValueType() == boolean.class){
            return getBoolean(config);
        }
        else if (config.getValueType() == String.class){
            return getString(config);
        }
        else if (config.getValueType() == long.class){
            return getLong(config);
        }
        else{
            return null;
        }
    }

    public void set(BuildConfigField config, Object value) {
        if (config.getValueType() == boolean.class){
            setBoolean(config, (Boolean) value);
        }
        else if (config.getValueType() == String.class){
            setString(config, (String) value);
        }
        else if (config.getValueType() == long.class){
            setLong(config, (Long) value);
        }
    }

    public boolean getBoolean(BuildConfigField config) {
        if (DevToolsPrefs.contains(config.getKey())){
            return DevToolsPrefs.getBoolean(config.getKey(), false);
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getBoolean(config.getKey());
        }
        else{
            return (boolean) config.getDefaultValue();
        }
    }

    public void setBoolean(BuildConfigField config, boolean value) {
        DevToolsPrefs.setBoolean(config.getKey(), value);
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
    }

    public String getString(BuildConfigField config) {
        if (DevToolsPrefs.contains(config.getKey())){
            return DevToolsPrefs.getString(config.getKey(), "");
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getString(config.getKey());
        }
        else{
            return (String) config.getDefaultValue();
        }
    }

    public void setString(BuildConfigField config, String value) {
        DevToolsPrefs.setString(config.getKey(), value);
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
    }

    public long getLong(BuildConfigField config) {
        if (DevToolsPrefs.contains(config.getKey())){
            return DevToolsPrefs.getLong(config.getKey(), 0);
        }
        else if (compileConfig.contains(config.getKey())){
            return compileConfig.getLong(config.getKey());
        }
        else{
            return (long) config.getDefaultValue();
        }
    }

    public void setLong(BuildConfigField config, long value) {
        DevToolsPrefs.setLong(config.getKey(), value);
        IadtController.get().getEventManager().fire(Event.CONFIG_CHANGED, config);
    }
}
