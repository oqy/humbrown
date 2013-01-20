package com.minyisoft.webapp.core.model.enumField;

import java.util.Locale;

import com.minyisoft.webapp.core.utils.RegexResourceBundleMessageSource;

/**
 * @author qingyong_ou 枚举描述信息帮助类
 */
public final class EnumDescriptionHelper {
	private EnumDescriptionHelper(){
		
	}
	
	/**
	 * 枚举描述信息资源
	 */
	private static RegexResourceBundleMessageSource enumDescriptionMessageSource=new RegexResourceBundleMessageSource(new String[]{"classpath*:**/enumField/enumDescription.properties"});
	
	/**
	 * 获取描述信息
	 * @param target
	 * @return
	 */
	public static String getDescription(CoreEnumInterface<?> target){
		if(target==null){
			return "";
		}else{
			return enumDescriptionMessageSource.getMessage(target.getClass().getName()+"_"+target.name(), null, Locale.getDefault());
		}
	}
}
