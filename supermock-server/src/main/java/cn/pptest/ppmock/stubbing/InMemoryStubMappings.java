package cn.pptest.ppmock.stubbing;

import static cn.pptest.ppmock.stubbing.StubMapping.NOT_CONFIGURED;
import static com.google.common.collect.Iterables.find;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.ResponseDefinition;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;


public class InMemoryStubMappings implements StubMappings {
	
	private final SortedConcurrentMappingSet mappings = new SortedConcurrentMappingSet();
	private final ConcurrentHashMap<String, Scenario> scenarioMap = new ConcurrentHashMap<String, Scenario>();
	
	@Override
	public ResponseDefinition serveFor(Request request) {
		StubMapping matchingMapping = find(
				mappings,
				mappingMatchingAndInCorrectScenarioState(request),
				StubMapping.NOT_CONFIGURED);
		
		notifyIfResponseNotConfigured(request, matchingMapping);
		matchingMapping.updateScenarioStateIfRequired();
		return ResponseDefinition.copyOf(matchingMapping.getResponse());
	}

	private void notifyIfResponseNotConfigured(Request request, StubMapping matchingMapping) {
		if (matchingMapping == NOT_CONFIGURED) {
		   // notifier().info("No mapping found matching URL " + request.getUrl());
		}
	}

	@Override
	public void addMapping(StubMapping mapping) {
		if (mapping.isInScenario()) {
			scenarioMap.putIfAbsent(mapping.getScenarioName(), Scenario.inStartedState());
			Scenario scenario = scenarioMap.get(mapping.getScenarioName());
			mapping.setScenario(scenario);
		}
		
		mappings.add(mapping);
	}

	@Override
	public void reset() {
		mappings.clear();
        scenarioMap.clear();
	}
	
	@Override
	public void resetScenarios() {
		for (Scenario scenario: scenarioMap.values()) {
			scenario.reset();
		}
	}

    @Override
    public List<StubMapping> getAll() {
        return ImmutableList.copyOf(mappings);
    }

    private Predicate<StubMapping> mappingMatchingAndInCorrectScenarioState(final Request request) {
		return new Predicate<StubMapping>() {
			public boolean apply(StubMapping mapping) {
				return mapping.getRequest().isMatchedBy(request) &&
				(mapping.isIndependentOfScenarioState() || mapping.requiresCurrentScenarioState());
			}
		};
	}
}
