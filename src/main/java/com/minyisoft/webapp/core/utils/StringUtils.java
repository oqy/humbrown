package com.minyisoft.webapp.core.utils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import org.springframework.util.Assert;

import com.google.common.collect.Range;

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

	// 中文大写金额单位
	private static final String UNIT[] = { "万", "千", "佰", "拾", "亿", "千", "佰", "拾", "万", "千", "佰", "拾", "元", "角", "分" };
	// 中文大写金额数字
	private static final String NUM[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };

	private static final BigDecimal MAX_VALUE = new BigDecimal("9999999999999.99");

	/**
	 * 将金额小数转换成中文大写金额
	 * 
	 * @param money
	 * @return result
	 */
	public static String convert2RMB(BigDecimal money) {
		Assert.isTrue(money != null && Range.closed(BigDecimal.ZERO, MAX_VALUE).contains(money), "待转换中文大写的金额需大于等于"
				+ BigDecimal.ZERO.intValue() + "且小于等于" + MAX_VALUE.doubleValue());
		long money1 = Math.round(money.doubleValue() * 100); // 四舍五入到分
		if (money1 == 0)
			return "零元整";
		String strMoney = String.valueOf(money1);
		int numIndex = 0; // numIndex用于选择金额数值
		int unitIndex = UNIT.length - strMoney.length(); // unitIndex用于选择金额单位
		boolean isZero = false; // 用于判断当前为是否为零
		String result = "";
		for (; numIndex < strMoney.length(); numIndex++, unitIndex++) {
			char num = strMoney.charAt(numIndex);
			if (num == '0') {
				isZero = true;
				if (UNIT[unitIndex] == "亿" || UNIT[unitIndex] == "万" || UNIT[unitIndex] == "元") { // 如果当前位是亿、万、元，且数值为零
					result = result + UNIT[unitIndex]; // 补单位亿、万、元
					isZero = false;
				}
			} else {
				if (isZero) {
					result = result + "零";
					isZero = false;
				}
				result = result + NUM[Integer.parseInt(String.valueOf(num))] + UNIT[unitIndex];
			}
		}
		// 不是角分结尾就加"整"字
		if (!result.endsWith("角") && !result.endsWith("分")) {
			result = result + "整";
		}
		// 例如没有这行代码，数值"400000001101.2"，输出就是"肆千亿万壹千壹佰零壹元贰角"
		result = result.replaceAll("亿万", "亿");
		return result;
	}
}
