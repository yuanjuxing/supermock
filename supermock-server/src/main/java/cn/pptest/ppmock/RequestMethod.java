package cn.pptest.ppmock;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import static java.util.Arrays.asList;

public enum RequestMethod {
	GET, POST, PUT, DELETE, PATCH, OPTIONS, HEAD, TRACE, ANY;

    @JsonCreator
    public static RequestMethod fromString(String value) {
        return RequestMethod.valueOf(value);
    }

    @JsonValue
    public String value() {
        return super.toString();
    }

    public boolean isOneOf(RequestMethod... methods) {
        return asList(methods).contains(this);
    }
}
