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
package cn.pptest.ppmock.runner;

import static com.google.common.collect.Collections2.filter;

import java.util.Collection;

import cn.pptest.ppmock.MappingsSaver;
import cn.pptest.ppmock.common.FileSource;
import cn.pptest.ppmock.common.Json;
import cn.pptest.ppmock.common.VeryShortIdGenerator;
import cn.pptest.ppmock.stubbing.StubMapping;
import cn.pptest.ppmock.stubbing.StubMappings;

import com.google.common.base.Predicate;

public class JsonFileMappingsSaver implements MappingsSaver {
    private final FileSource mappingsFileSource;
    private final VeryShortIdGenerator idGenerator;

    public JsonFileMappingsSaver(FileSource mappingsFileSource) {
        this.mappingsFileSource = mappingsFileSource;
        idGenerator = new VeryShortIdGenerator();
    }

    public void saveMappings(StubMappings stubMappings) {
        Collection<StubMapping> transientStubs = filter(stubMappings.getAll(), new Predicate<StubMapping>() {
            public boolean apply(StubMapping input) {
                return input != null && input.isTransient();
            }
        });

        for (StubMapping mapping : transientStubs) {
            String fileId = idGenerator.generate();
            String mappingFileName = "saved-mapping-" + fileId + ".json";
            mappingsFileSource.writeTextFile(mappingFileName, Json.write(mapping));
            mapping.setTransient(false);
        }
    }
}
