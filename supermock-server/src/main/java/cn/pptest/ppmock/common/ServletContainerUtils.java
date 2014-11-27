package cn.pptest.ppmock.common;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.net.URI;

public class ServletContainerUtils {

	public static boolean isBrowserProxyRequest(HttpServletRequest request) {
		if (!hasField(request, "_uri")) {
			return false;
		}
		
		String uriString = getPrivateField(request, "_uri").toString();
		URI uri = URI.create(uriString);
		return uri.isAbsolute();
	}
	
	@SuppressWarnings("unchecked")
	private static <T> T getPrivateField(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			field.setAccessible(true);
			return (T) field.get(obj);
		} catch (RuntimeException re) {
			throw re;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private static boolean hasField(Object obj, String name) {
		try {
			Field field = obj.getClass().getDeclaredField(name);
			return field != null;
		} catch (RuntimeException re) {
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
}
