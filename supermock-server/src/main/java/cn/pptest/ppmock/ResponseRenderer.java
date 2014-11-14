package cn.pptest.ppmock;

public interface ResponseRenderer {
	
	public static final String CONTEXT_KEY = "ResponseRenderer";

	Response render(ResponseDefinition responseDefinition);
	
}
