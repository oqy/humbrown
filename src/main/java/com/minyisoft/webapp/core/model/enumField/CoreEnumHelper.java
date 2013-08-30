package com.minyisoft.webapp.core.model.enumField;

import java.util.Locale;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.minyisoft.webapp.core.utils.spring.RegexResourceBundleMessageSource;

/**
 * @author qingyong_ou 枚举帮助类
 */
public final class CoreEnumHelper {
	private CoreEnumHelper(){
		
	}
	
	/**
	 * 获取描述信息
	 * @param target
	 * @return
	 */
	public static String getDescription(ICoreEnum<?> target){
		if(target==null){
			return "";
		}else{
			return RegexResourceBundleMessageSource.getSystemDefaultMessageSource().getMessage(target.getClass().getName()+"_"+target.name(), null, Locale.getDefault());
		}
	}
	
	/**
	 * 获取指定整形枚举值
	 * @param coreEnumClazz
	 * @param value
	 * @return
	 */
	public static <T extends ICoreEnum<Integer>> T getEnum(Class<T> coreEnumClazz,int value){
		if (coreEnumClazz != null && ArrayUtils.isNotEmpty(coreEnumClazz.getEnumConstants())) {
			for (T e : coreEnumClazz.getEnumConstants()) {
				if (e.getValue()==value) {
					return e;
				}
			}
		}
		return null;
	}
	
	/**
	 * 获取指定字符枚举值
	 * @param coreEnumClazz
	 * @param value
	 * @return
	 */
	public static <T extends ICoreEnum<String>> T getEnum(Class<T> coreEnumClazz,String value){
		if (coreEnumClazz != null && ArrayUtils.isNotEmpty(coreEnumClazz.getEnumConstants())) {
			for (T e : coreEnumClazz.getEnumConstants()) {
				if (StringUtils.equals(e.getValue(),value)) {
					return e;
				}
			}
		}
		return null;
	}
}
