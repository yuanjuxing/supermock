package cn.pptest.ppmock.model;

import cn.pptest.ppmock.matcher.ValuePattern;

import com.google.common.base.Predicate;

import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Lists.newArrayList;

public class HttpHeader {

    private final String key;
    private final List<String> values;

    public HttpHeader(String key, String... values) {
        this.key = key;
        this.values = newArrayList(values);
    }

    public HttpHeader(String key, Collection<String> values) {
        this.key = key;
        this.values = newArrayList(values);
    }

    public static HttpHeader httpHeader(String key, String... values) {
        return new HttpHeader(key, values);
    }

    public static HttpHeader absent(String key) {
        return new HttpHeader(key);
    }

    public static HttpHeader copyOf(HttpHeader header) {
        return new HttpHeader(header.key(), header.values());
    }

    public boolean isPresent() {
        return values.size() > 0;
    }

    public String key() {
        return key;
    }

    public String firstValue() {
        checkPresent();
        return values.get(0);
    }

    public List<String> values() {
        checkPresent();
        return values;
    }

    private void checkPresent() {
        checkState(isPresent(), "No value for header " + key);
    }

    public boolean isSingleValued() {
        return values.size() == 1;
    }

    public boolean containsValue(String expectedValue) {
        return values.contains(expectedValue);
    }

    public boolean hasValueMatching(final ValuePattern valuePattern) {
        return (valuePattern.nullSafeIsAbsent() && !isPresent())
                || anyValueMatches(valuePattern);
    }

    private boolean anyValueMatches(final ValuePattern valuePattern) {
        return any(values, new Predicate<String>() {
            public boolean apply(String headerValue) {
                return valuePattern.isMatchFor(headerValue);
            }
        });
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            sb.append(key).append(": ").append(value).append("\n");
        }

        return sb.toString();
    }

}
