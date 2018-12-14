package com.appleframework.pay.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

public class UrlMapUtility {

	/**
	 * @param urlparam 带分隔的url参数
	 * @return
	 */
	public static Map<String, String> splitAndDecode(String urlparam) {
		Map<String, String> map = new HashMap<String, String>();
		String[] param = urlparam.split("&");
		for (String keyvalue : param) {
			String[] pair = keyvalue.split("=");
			if (pair.length == 2) {
				try {
					map.put(pair[0], URLDecoder.decode(pair[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					map.put(pair[0], pair[1]);
				}
			}
		}
		return map;
	}
}
