package com.mzitu.utils;

import java.util.HashMap;
import java.util.Map;

public class Const {
	public final static Map<String, String> headers   = new HashMap<>();
	public final static Map<String, String> cookies   = new HashMap<>();
	public final static String				userAgent;
	
	static {
		// set headers
		headers.put("Referer", "http://www.mzitu.com/");
		// set cookies
		cookies.put("Hm_lvt_dbc355aef238b6c32b43eacbbf161c3c", "1519793288,1519811785,1519833803,1519834263");
		cookies.put("Hm_lpvt_dbc355aef238b6c32b43eacbbf161c3c", "1519847570");
		// like google
		userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
					+ "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36";
	}
}
