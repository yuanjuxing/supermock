package cn.pptest.ppmock.matcher;

import cn.pptest.ppmock.Request;
import cn.pptest.ppmock.RequestMethod;
import cn.pptest.ppmock.common.Json;
import cn.pptest.ppmock.model.HttpHeader;
import cn.pptest.ppmock.RequestMethod.*;
import cn.pptest.ppmock.matcher.ValuePattern;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize.Inclusion;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Maps.newLinkedHashMap;

@JsonSerialize(include=Inclusion.NON_NULL)
public class RequestPattern {

    private String urlPattern;
	private String url;
	private RequestMethod method;
	private Map<String, ValuePattern> headerPatterns;
	private List<ValuePattern> bodyPatterns;
	
	public RequestPattern(RequestMethod method, String url, Map<String, ValuePattern> headerPatterns) {
		this.url = url;
		this.method = method;
		this.headerPatterns = headerPatterns;
	}
	
	public RequestPattern(RequestMethod method) {
		this.method = method;
	}
	
	public RequestPattern(RequestMethod method, String url) {
		this.url = url;
		this.method = method;
	}
	
	public RequestPattern() {
	}

    public static RequestPattern everything() {
        RequestPattern requestPattern = new RequestPattern(RequestMethod.ANY);
        requestPattern.setUrlPattern(".*");
        return requestPattern;
    }

    public static RequestPattern buildRequestPatternFrom(String json) {
        return Json.read(json, RequestPattern.class);
    }

    private void assertIsInValidState() {
		if (url != null && urlPattern != null) {
			throw new IllegalStateException("URL and URL pattern may not be set simultaneously");
		}
	}
	
	public boolean isMatchedBy(Request request) {
		return (urlIsMatch(request) &&
				methodMatches(request) &&
                requiredAbsentHeadersAreNotPresentIn(request) &&
				headersMatch(request) &&
				bodyMatches(request));
	}
	
	private boolean urlIsMatch(Request request) {
		String candidateUrl = request.getUrl();
		boolean matched;
		if (urlPattern == null) {
			matched = url.equals(candidateUrl);
		} else {
			matched = candidateUrl.matches(urlPattern);
		}
		
		return matched;
	}
	
	private boolean methodMatches(Request request) {
		boolean matched = method == RequestMethod.ANY || request.getMethod() == method;
		if (!matched) {
			//notifier().info(String.format("URL %s is match, but method %s is not", request.getUrl(), request.getMethod()));
		}
		
		return matched;
	}

    private boolean requiredAbsentHeadersAreNotPresentIn(final Request request) {
        return !any(requiredAbsentHeaderKeys(), new Predicate<String>() {
            public boolean apply(String key) {
                return request.getAllHeaderKeys().contains(key);
            }
        });
    }

    private Set<String> requiredAbsentHeaderKeys() {
        if (headerPatterns == null) {
            return ImmutableSet.of();
        }

        return ImmutableSet.copyOf(filter(transform(headerPatterns.entrySet(), TO_KEYS_WHERE_VALUE_ABSENT), REMOVING_NULL));
    }
	
	private boolean headersMatch(final Request request) {
        return noHeadersAreRequiredToBePresent() ||
                all(headerPatterns.entrySet(), matchHeadersIn(request));
	}

    private boolean noHeadersAreRequiredToBePresent() {
        return headerPatterns == null || allHeaderPatternsSpecifyAbsent();
    }

    private boolean allHeaderPatternsSpecifyAbsent() {
        return size(filter(headerPatterns.values(), new Predicate<ValuePattern>() {
            public boolean apply(ValuePattern headerPattern) {
                return !headerPattern.nullSafeIsAbsent();
            }
        })) == 0;
    }

    private boolean bodyMatches(Request request) {
		if (bodyPatterns == null) {
			return true;
		}
		
		boolean matches = all(bodyPatterns, ValuePattern.matching(request.getBodyAsString()));
		
		if (!matches) {
			//().info(String.format("URL %s is match, but body is not: %s", request.getUrl(), request.getBodyAsString()));
		}
		
		return matches;
	}
	
	public String getUrlPattern() {
		return urlPattern;
	}

	public void setUrlPattern(String urlPattern) {
		this.urlPattern = urlPattern;
		assertIsInValidState();
	}
	
	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

	public Map<String, ValuePattern> getHeaders() {
		return headerPatterns;
	}
	
	public void addHeader(String key, ValuePattern pattern) {
		if (headerPatterns == null) {
			headerPatterns = newLinkedHashMap();
		}
		
		headerPatterns.put(key, pattern);
	}
	
	public void setHeaders(Map<String, ValuePattern> headers) {
		this.headerPatterns = headers;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
		assertIsInValidState();
	}
	
	public List<ValuePattern> getBodyPatterns() {
		return bodyPatterns;
	}

	public void setBodyPatterns(List<ValuePattern> bodyPatterns) {
		this.bodyPatterns = bodyPatterns;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bodyPatterns == null) ? 0 : bodyPatterns.hashCode());
		result = prime * result + ((headerPatterns == null) ? 0 : headerPatterns.hashCode());
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result
				+ ((urlPattern == null) ? 0 : urlPattern.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RequestPattern other = (RequestPattern) obj;
		if (bodyPatterns == null) {
			if (other.bodyPatterns != null) {
				return false;
			}
		} else if (!bodyPatterns.equals(other.bodyPatterns)) {
			return false;
		}
		if (headerPatterns == null) {
			if (other.headerPatterns != null) {
				return false;
			}
		} else if (!headerPatterns.equals(other.headerPatterns)) {
			return false;
		}
		if (method != other.method) {
			return false;
		}
		if (url == null) {
			if (other.url != null) {
				return false;
			}
		} else if (!url.equals(other.url)) {
			return false;
		}
		if (urlPattern == null) {
			if (other.urlPattern != null) {
				return false;
			}
		} else if (!urlPattern.equals(other.urlPattern)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return Json.write(this);
	}

    private static final Function<Map.Entry<String,ValuePattern>,String> TO_KEYS_WHERE_VALUE_ABSENT = new Function<Map.Entry<String, ValuePattern>, String>() {
        public String apply(Map.Entry<String, ValuePattern> input) {
            return input.getValue().nullSafeIsAbsent() ? input.getKey() : null;
        }
    };

    private static final Predicate<String> REMOVING_NULL = new Predicate<String>() {
        public boolean apply(String input) {
            return input != null;
        }
    };

    private static final Predicate<Map.Entry<String, ValuePattern>> matchHeadersIn(final Request request) {
        return new Predicate<Map.Entry<String, ValuePattern>>() {
            public boolean apply(Map.Entry<String, ValuePattern> headerPattern) {
                ValuePattern headerValuePattern = headerPattern.getValue();
                String key = headerPattern.getKey();
                HttpHeader header = request.header(key);

                boolean match = header.hasValueMatching(headerValuePattern);

                if (!match) {
                    //
                }

                return match;
            }
        };
    }
}
