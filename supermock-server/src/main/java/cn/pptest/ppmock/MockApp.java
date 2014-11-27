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
package cn.pptest.ppmock;

import java.awt.Container;
import java.util.List;

import cn.pptest.ppmock.matcher.RequestPattern;
import cn.pptest.ppmock.runner.MappingsLoader;
import cn.pptest.ppmock.setting.GlobalSettings;
import cn.pptest.ppmock.setting.GlobalSettingsHolder;
import cn.pptest.ppmock.setting.RequestDelayControl;
import cn.pptest.ppmock.setting.RequestDelaySpec;
import cn.pptest.ppmock.stubbing.InMemoryStubMappings;
import cn.pptest.ppmock.stubbing.ListStubMappingsResult;
import cn.pptest.ppmock.stubbing.StubMapping;
import cn.pptest.ppmock.stubbing.StubMappings;
import cn.pptest.ppmock.verification.DisabledRequestJournal;
import cn.pptest.ppmock.verification.FindRequestsResult;
import cn.pptest.ppmock.verification.InMemoryRequestJournal;
import cn.pptest.ppmock.verification.LoggedRequest;
import cn.pptest.ppmock.verification.RequestJournal;
import cn.pptest.ppmock.verification.RequestJournalDisabledException;
import cn.pptest.ppmock.verification.VerificationResult;

public class MockApp{
    
    public static final String FILES_ROOT = "__files";
    public static final String ADMIN_CONTEXT_ROOT = "/__admin";

    private final StubMappings stubMappings;
    private final RequestJournal requestJournal;
    private final GlobalSettingsHolder globalSettingsHolder;
    private final RequestDelayControl requestDelayControl;
    private final boolean browserProxyingEnabled;
    private final MappingsLoader defaultMappingsLoader;
    private final Server server;
    private final MappingsSaver mappingsSaver;

    public MockApp(
            RequestDelayControl requestDelayControl,
            boolean browserProxyingEnabled,
            MappingsLoader defaultMappingsLoader,
            MappingsSaver mappingsSaver,
            boolean requestJournalDisabled,
            Server server) {
        this.requestDelayControl = requestDelayControl;
        this.browserProxyingEnabled = browserProxyingEnabled;
        this.defaultMappingsLoader = defaultMappingsLoader;
        this.mappingsSaver = mappingsSaver;
        globalSettingsHolder = new GlobalSettingsHolder();
        stubMappings = new InMemoryStubMappings();
        requestJournal = requestJournalDisabled ? new DisabledRequestJournal() : new InMemoryRequestJournal();
        this.server = server;
        loadDefaultMappings();
    }

    public GlobalSettingsHolder getGlobalSettingsHolder() {
        return globalSettingsHolder;
    }

    private void loadDefaultMappings() {
        loadMappingsUsing(defaultMappingsLoader);
    }

    public void loadMappingsUsing(final MappingsLoader mappingsLoader) {
        mappingsLoader.loadMappingsInto(stubMappings);
    }
    
    
    public ResponseDefinition serveStubFor(Request request) {
        ResponseDefinition responseDefinition = stubMappings.serveFor(request);
        requestJournal.requestReceived(request);
        if (!responseDefinition.wasConfigured() && request.isBrowserProxyRequest() && browserProxyingEnabled) {
            return ResponseDefinition.browserProxy(request);
        }

        return responseDefinition;
    }

    
    public void addStubMapping(StubMapping stubMapping) {
        stubMappings.addMapping(stubMapping);
    }

    
    public ListStubMappingsResult listAllStubMappings() {
        return new ListStubMappingsResult(stubMappings.getAll());
    }

    
    public void saveMappings() {
        mappingsSaver.saveMappings(stubMappings);
    }

    
    public void resetMappings() {
        stubMappings.reset();
        requestJournal.reset();
        requestDelayControl.clearDelay();
    }

    
    public void resetToDefaultMappings() {
        resetMappings();
        loadDefaultMappings();
    }

    
    public void resetScenarios() {
        stubMappings.resetScenarios();
    }

    
    public VerificationResult countRequestsMatching(RequestPattern requestPattern) {
        try {
            return VerificationResult.withCount(requestJournal.countRequestsMatching(requestPattern));
        } catch (RequestJournalDisabledException e) {
            return VerificationResult.withRequestJournalDisabled();
        }
    }

    
    public FindRequestsResult findRequestsMatching(RequestPattern requestPattern) {
        try {
            List<LoggedRequest> requests = requestJournal.getRequestsMatching(requestPattern);
            return FindRequestsResult.withRequests(requests);
        } catch (RequestJournalDisabledException e) {
            return FindRequestsResult.withRequestJournalDisabled();
        }
    }

   
    public void updateGlobalSettings(GlobalSettings newSettings) {
        globalSettingsHolder.replaceWith(newSettings);
    }

    
    public void addSocketAcceptDelay(RequestDelaySpec delaySpec) {
        requestDelayControl.setDelay(delaySpec.milliseconds());
    }


}
