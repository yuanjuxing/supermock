/*
 * Copyright (C) 2011 Thomas Akehurst
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.pptest.ppmock.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.Optional;


public enum HttpProtocolVersion {
    VERSION_0_9("HTTP/0.9"),
    VERSION_1_0("HTTP/1.0"),
    VERSION_1_1("HTTP/1.1");

    private final String text;

    private HttpProtocolVersion(final String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }

    @Override
    public String toString() {
        return text;
    }

    public static HttpProtocolVersion versionOf(final String version) {
        HttpProtocolVersion[] values = HttpProtocolVersion.values();
        for (HttpProtocolVersion value : values) {
            if (value.text.equalsIgnoreCase(version)) {
                return value;
            }
        }

        throw new IllegalArgumentException("unknown HTTP version: " + version);
    }
}