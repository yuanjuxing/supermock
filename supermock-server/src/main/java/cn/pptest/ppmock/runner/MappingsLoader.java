package cn.pptest.ppmock.runner;

import cn.pptest.ppmock.stubbing.StubMappings;

public interface MappingsLoader {

	void loadMappingsInto(StubMappings stubMappings);

}
