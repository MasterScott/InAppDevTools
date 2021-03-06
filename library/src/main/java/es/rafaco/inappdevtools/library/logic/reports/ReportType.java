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

package es.rafaco.inappdevtools.library.logic.reports;

import java.util.HashMap;
import java.util.Map;

public enum ReportType {
    CRASH(1),
    SESSION(2),
    ISSUE(3);

    public int code;
    private static final Map<Integer, ReportType> TYPES = new HashMap<>();
    ReportType(int code) {
        this.code = code;
    }
    static {
        for (ReportType value : values()) {
            TYPES.put(value.code, value);
        }
    }
    public int getCode() {
        return code;
    }
    public static ReportType getByCode(int code) {
        return TYPES.get(code);
    }
}
