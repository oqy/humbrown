package com.minyisoft.webapp.core.model.enumField;

import java.util.Locale;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * @author qingyong_ou 枚举帮助类
 */
public final class DescribableEnumHelper {
	private DescribableEnumHelper() {

	}

	/**
	 * 加载枚举对象中文描述的messageSource
	 */
	private static ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

	public static void setDescriptionBaseNames(String... basenames) {
		messageSource.setBasenames(basenames);
	}

	/**
	 * 获取描述信息
	 * 
	 * @param target
	 * @return
	 */
	public static String getDescription(
			Enum<? extends DescribableEnum<?>> target) {
		if (target == null) {
			return "";
		} else {
			return messageSource.getMessage(target.getClass().getName() + "_"
					+ target.name(), null, "", Locale.getDefault());
		}
	}

	public static <T extends Enum<? extends DescribableEnum<?>>> T getEnum(
			Class<T> coreEnumClazz, Object value) {
		if (coreEnumClazz != null
				&& ArrayUtils.isNotEmpty(coreEnumClazz.getEnumConstants())) {
			for (T e : coreEnumClazz.getEnumConstants()) {
				if (((DescribableEnum<?>) e).getValue().equals(value)) {
					return e;
				}
			}
		}
		return null;
	}
}
