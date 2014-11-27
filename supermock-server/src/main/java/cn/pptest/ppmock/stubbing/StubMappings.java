package cn.pptest.ppmock.stubbing;


import java.util.List;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.ResponseDefinition;

public interface StubMappings {

	ResponseDefinition serveFor(Request request);
	void addMapping(StubMapping mapping);
	void reset();
	void resetScenarios();

    List<StubMapping> getAll();
}
