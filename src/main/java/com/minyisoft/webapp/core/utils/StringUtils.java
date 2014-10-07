package com.minyisoft.webapp.core.utils;

import java.util.regex.Pattern;

/**
 * @author qingyong_ou 字符串工具类
 */
public final class StringUtils extends org.apache.commons.lang3.StringUtils {
	private StringUtils() {

	}

	// 定义script的正则表达式
	private static final Pattern p_script = Pattern.compile("<script[^>]*?>[\\s\\S]*?<\\/script>",
			Pattern.CASE_INSENSITIVE);
	// 定义style的正则表达式
	private static final Pattern p_style = Pattern.compile("<style[^>]*?>[\\s\\S]*?<\\/style>",
			Pattern.CASE_INSENSITIVE);
	// 定义style的正则表达式
	private static final Pattern p_xml = Pattern.compile("<xml[^>]*?>[\\s\\S]*?<\\/xml>", Pattern.CASE_INSENSITIVE);
	// 定义HTML标签的正则表达式
	private static final Pattern p_html = Pattern.compile("<[^>]+>", Pattern.CASE_INSENSITIVE);

	public static String removeHTMLTag(String htmlStr) {
		if (isBlank(htmlStr)) {
			return null;
		}
		htmlStr = p_script.matcher(htmlStr).replaceAll(""); // 过滤script标签
		htmlStr = p_style.matcher(htmlStr).replaceAll(""); // 过滤style标签
		htmlStr = p_xml.matcher(htmlStr).replaceAll(""); // 过滤style标签
		htmlStr = p_html.matcher(htmlStr).replaceAll(""); // 过滤html标签
		return htmlStr.replaceAll("\\&[a-zA-Z]{1,10};", "").trim();
	}
}
