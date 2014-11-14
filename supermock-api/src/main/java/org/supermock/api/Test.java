package org.supermock.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Test {
	private String testinfo;
	private String name;
	@JsonProperty
	public String getTestinfo() {
		return testinfo;
	}
	@JsonProperty
	public void setTestinfo(String testinfo) {
		this.testinfo = testinfo;
	}
	@JsonProperty
	public String getName() {
		return name;
	}
	@JsonProperty
	public void setName(String name) {
		this.name = name;
	}
	
}
